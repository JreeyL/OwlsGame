pipeline {
    agent any

    tools {
        maven 'maven-3.99'
        jdk 'java-21'
    }

    environment {
        // Jenkins Credentials IDs
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        SSH_CREDENTIALS_ID       = 'aws-ec2-id_rsa'
        DB_CREDENTIALS_ID        = 'db-credentials'

        // Repository and Host Configuration
        DOCKER_IMAGE_NAME        = "jiyuli/owlsgame"
        APP_HOST                 = "3.252.140.1"
        APP_USER                 = "ec2-user"
        DB_HOST                  = "10.0.1.5"        // <-- IMPORTANT: Replace with your DB private IP or RDS endpoint
        DB_NAME                  = "owlsgame_db"
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/JreeyL/OwlsGame.git'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            // Disabled for now. Remove the 'when' block to enable.
            when { expression { false } }
            steps {
                withSonarQubeEnv('sonarqube-local') {
                    bat 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build & Tag') {
            steps {
                echo "Building and tagging image: ${DOCKER_IMAGE_NAME}"
                // Tag with both build number and 'latest'
                bat "docker build -t ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} -t ${DOCKER_IMAGE_NAME}:latest ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: DOCKERHUB_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    echo "Logging in to Docker Hub as ${DOCKER_USER}..."
                    bat "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"

                    echo "Pushing tag: ${env.BUILD_NUMBER}"
                    bat "docker push ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"

                    echo "Pushing tag: latest"
                    bat "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                withCredentials([
                    sshUserPrivateKey(credentialsId: SSH_CREDENTIALS_ID, keyFileVariable: 'KEYFILE', usernameVariable: 'USERNAME'),
                    usernamePassword(credentialsId: DB_CREDENTIALS_ID, usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS')
                ]) {
                    script {
                        // Define the multi-line shell script to be executed remotely.
                        // This avoids complex escaping issues with Windows bat.
                        def remoteScript = """
                            #!/bin/bash
                            set -e

                            echo '--- Pulling latest image from Docker Hub ---'
                            docker pull ${DOCKER_IMAGE_NAME}:latest

                            echo '--- Stopping and removing old container ---'
                            docker stop owlsgame-app || true
                            docker rm owlsgame-app || true

                            echo '--- Starting new container ---'
                            docker run -d --name owlsgame-app -p 8080:8080 \\
                                -e SPRING_PROFILES_ACTIVE=prod \\
                                -e SPRING_DATASOURCE_URL='jdbc:mysql://${DB_HOST}:3306/${DB_NAME}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' \\
                                -e SPRING_DATASOURCE_USERNAME=${DB_USER} \\
                                -e SPRING_DATASOURCE_PASSWORD=${DB_PASS} \\
                                -e SPRING_JPA_DATABASE-PLATFORM=org.hibernate.dialect.MySQLDialect \\
                                -e SPRING_JPA_HIBERNATE_DDL_AUTO=update \\
                                --restart unless-stopped \\
                                ${DOCKER_IMAGE_NAME}:latest

                            echo '--- Deployment script finished successfully ---'
                        """

                        // Execute the remote script via SSH using a Here Document.
                        // The '\\n' is required by bat to correctly handle the multi-line input.
                        bat "ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} bash -s <<'EOF'\\n${remoteScript}\\nEOF"
                    }
                }
            }
        }

        stage('Post-deploy Check') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: SSH_CREDENTIALS_ID, keyFileVariable: 'KEYFILE', usernameVariable: 'USERNAME')]) {
                    bat """
                    REM Wait 15 seconds for the container to initialize.
                    timeout /t 15 /nobreak >nul

                    echo "--- Checking container status on ${APP_HOST} ---"
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker ps --filter name=owlsgame-app"

                    echo "--- Showing last 50 lines of app logs ---"
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker logs --tail 50 owlsgame-app"
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finished. Logging out and cleaning up Jenkins agent..."
            // It's a good practice to log out from the registry
            bat 'docker logout'
            // Clean up dangling images on the Jenkins agent
            bat 'docker image prune -f'
        }
        failure {
            echo 'Pipeline failed. Please check the console output for errors.'
        }
    }
}