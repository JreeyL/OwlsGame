pipeline {
    agent any

    triggers {
        githubPush()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        skipDefaultCheckout(true)
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
        stage('Checkout') {
            when {
                anyOf {
                    branch 'master'
                    expression { env.BRANCH_NAME == 'master' }
                    expression { return true } // Fallback for manual builds
                }
            }
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
                echo "Building and tagging image with name: ${DOCKER_IMAGE_NAME}"
                bat "docker build -t ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} -t ${DOCKER_IMAGE_NAME}:latest ."
            }
        }

        stage('Push to Docker Hub') {
            steps {
                withCredentials([usernamePassword(credentialsId: DOCKERHUB_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    echo "Logging in to Docker Hub as ${DOCKER_USER}..."
                    bat "echo %DOCKER_PASS% | docker login -u %DOCKER_USER% --password-stdin"
                    bat "docker push ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"
                    bat "docker push ${DOCKER_IMAGE_NAME}:latest"
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
                    
                    echo "=== Starting Database Container (if not already running) ==="
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker run -d --name owlsgame-db -e MYSQL_DATABASE=owlsgame_db -e MYSQL_ROOT_PASSWORD=RootRoot## --restart unless-stopped mysql:8 || echo 'Database container already exists'"
                    
                    echo "=== Waiting for database to be ready ==="
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "for i in {1..30}; do if docker exec owlsgame-db mysqladmin ping -h localhost -pRootRoot## --silent; then echo 'Database is ready'; break; else echo 'Waiting for database...'; sleep 2; fi; done"
                    
                    echo "=== Deploying Application Container ==="
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker pull ${DOCKER_IMAGE_NAME}:latest && docker stop owlsgame-app || true && docker rm owlsgame-app || true && docker run -d --name owlsgame-app -p 8080:8080 --link owlsgame-db:mysql -e SPRING_PROFILES_ACTIVE=prod ${DOCKER_IMAGE_NAME}:latest"
                    """
                }
            }
        }

        stage('Health Check') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: SSH_CREDENTIALS_ID, keyFileVariable: 'KEYFILE', usernameVariable: 'USERNAME')]) {
                    bat """
                    icacls %KEYFILE% /inheritance:r
                    icacls %KEYFILE% /remove BUILTIN\\Users
                    icacls %KEYFILE% /grant SYSTEM:R
                    
                    echo "=== Waiting for application to start ==="
                    timeout /t 30 /nobreak >nul
                    
                    echo "=== Checking container status ==="
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker ps --filter name=owlsgame"
                    
                    echo "=== Checking application logs ==="
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker logs --tail 50 owlsgame-app"
                    
                    echo "=== Testing application endpoint ==="
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "curl -f http://localhost:8080/ || echo 'Application health check failed'"
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
            bat 'docker logout'
            bat 'docker image prune -f'
        }
    }
}