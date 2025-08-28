pipeline {
    agent any

    triggers {
        // Only trigger builds for pushes to master branch
        githubPush()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // Prevent concurrent builds - use global lock
        disableConcurrentBuilds(abortPrevious: true)
        // Add timeout to prevent hanging builds
        timeout(time: 30, unit: 'MINUTES')
    }

    tools {
        maven 'maven-3.99'
        jdk 'java-21'
    }

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials' // Jenkins中的DockerHub凭据ID
        DOCKER_IMAGE_NAME = "jiyuli/owlsgame"
        SSH_CREDENTIALS_ID = 'aws-ec2-id_rsa' // Jenkins中的EC2 SSH私钥ID
        APP_HOST = "3.252.140.1"
        APP_USER = "ec2-user"
    }

    stages {
        stage('Branch Check') {
            steps {
                script {
                    // Only allow master branch to proceed
                    if (env.BRANCH_NAME != 'master' && env.BRANCH_NAME != null) {
                        currentBuild.result = 'ABORTED'
                        error("Pipeline only runs on master branch. Current branch: ${env.BRANCH_NAME}")
                    }
                    echo "Branch check passed. Running on master branch."
                }
            }
        }

        stage('Checkout') {
            steps {
                // Use lock to ensure only one build runs at a time
                lock(resource: 'owlsgame-build-lock') {
                    git branch: 'master', url: 'https://github.com/JreeyL/OwlsGame.git'
                }
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    jacoco execPattern: '**/target/jacoco.exec'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonarqube-local') {
                    bat 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build & Tag') {
            steps {
                // Use lock to prevent Docker conflicts
                lock(resource: 'docker-build-lock') {
                    echo "Building and tagging image with name: ${DOCKER_IMAGE_NAME}"
                    script {
                        // Clean up any existing build artifacts first
                        bat "docker rmi ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} || echo 'No existing image to remove'"
                        // Build with both build number and latest tags
                        bat "docker build -t ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} -t ${DOCKER_IMAGE_NAME}:latest ."
                    }
                }
            }
        }

        stage('Push to Docker Hub') {
            steps {
                // Use lock to prevent Docker Hub conflicts
                lock(resource: 'docker-push-lock') {
                    withCredentials([usernamePassword(credentialsId: DOCKERHUB_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        echo "Logging in to Docker Hub as ${DOCKER_USER}..."
                        bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                        bat "docker push ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"
                        bat "docker push ${DOCKER_IMAGE_NAME}:latest"
                    }
                }
            }
        }

        stage('Deploy to EC2 APP') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: SSH_CREDENTIALS_ID, keyFileVariable: 'KEYFILE', usernameVariable: 'USERNAME')]) {
                    bat """
                    icacls %KEYFILE% /inheritance:r
                    icacls %KEYFILE% /remove BUILTIN\\Users
                    icacls %KEYFILE% /grant SYSTEM:R
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker pull ${DOCKER_IMAGE_NAME}:latest && docker stop owlsgame-app || true && docker rm owlsgame-app || true && docker run -d --name owlsgame-app -p 8080:8080 --link owlsgame-db:mysql ${DOCKER_IMAGE_NAME}:latest"
                    """
                }
            }
        }

        stage('Report') {
            steps {
                archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
                publishHTML target: [
                    allowMissing: false,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Report'
                ]
            }
        }
    }

    post {
        always {
            echo "Pipeline finished. Logging out and cleaning up..."
            script {
                // Safe Docker cleanup - only remove specific build images
                try {
                    bat 'docker logout'
                    // Only remove the specific build image, not all dangling images
                    bat "docker rmi ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} || echo 'Build image already removed'"
                    // Use safer cleanup that won't hang
                    bat 'docker system prune -f --filter "until=24h" || echo "Cleanup skipped"'
                } catch (Exception e) {
                    echo "Cleanup failed: ${e.getMessage()}, continuing..."
                }
            }
        }
        failure {
            echo 'Build failed. Check logs for details.'
        }
        aborted {
            echo 'Build was aborted.'
            // Clean up any hanging Docker processes
            script {
                try {
                    bat "docker stop \$(docker ps -q --filter ancestor=${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}) || echo 'No containers to stop'"
                } catch (Exception e) {
                    echo "Container cleanup failed: ${e.getMessage()}"
                }
            }
        }
    }
}