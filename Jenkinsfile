pipeline {
    agent any

    tools {
        maven 'maven-3.99'
        jdk 'java-21'
    }

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials' // Jenkins中的DockerHub凭据ID
        DOCKER_IMAGE_NAME = "jiyuli/owlsgame"
        SSH_CREDENTIALS_ID = 'aws-ec2-id_rsa' // Jenkins中的EC2 SSH私钥ID (sshUserPrivateKey)
        APP_HOST = "3.252.140.1"
        APP_USER = "ec2-user"

        // 非敏感：DB 主机/数据库名（请改为你的私有IP或RDS endpoint）
        DB_HOST = "10.0.1.5"        // <-- 替换为你的 DB 私有 IP 或 RDS endpoint（不要用 'db'）
        DB_NAME = "owlsgame_db"
        // 可在 Jenkins 全局环境变量或通过凭据管理设置 DB_HOST
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
                    bat "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
                    bat "docker push ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"
                    bat "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }

        stage('Deploy to EC2 APP') {
            steps {
                // 获取 SSH 私钥 和 DB 用户名/密码（db-credentials 要在 Jenkins 中新增）
                withCredentials([
                    sshUserPrivateKey(credentialsId: SSH_CREDENTIALS_ID, keyFileVariable: 'KEYFILE', usernameVariable: 'USERNAME'),
                    usernamePassword(credentialsId: 'db-credentials', usernameVariable: 'DB_USER', passwordVariable: 'DB_PASS')
                ]) {
                    // 注意：在 Windows/Jenkins 的 bat block 中，使用双引号和转义需小心
                    bat """
                    icacls %KEYFILE% /inheritance:r
                    icacls %KEYFILE% /remove BUILTIN\\Users
                    icacls %KEYFILE% /grant SYSTEM:R

                    echo Deploying image to ${APP_HOST}...

                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} ^
"docker pull ${DOCKER_IMAGE_NAME}:latest && \
 docker stop owlsgame-app || true && docker rm owlsgame-app || true && \
 docker run -d --name owlsgame-app -p 8080:8080 \
 -e SPRING_PROFILES_ACTIVE=prod \
 -e SPRING_DATASOURCE_URL='jdbc:mysql://${DB_HOST}:3306/${DB_NAME}?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC' \
 -e SPRING_DATASOURCE_USERNAME=${DB_USER} \
 -e SPRING_DATASOURCE_PASSWORD=${DB_PASS} \
 -e SPRING_JPA_PROPERTIES_HIBERNATE_GLOBALLY_QUOTED_IDENTIFIERS=false \
 --restart unless-stopped ${DOCKER_IMAGE_NAME}:latest"

                    """
                }
            }
        }

        stage('Post-deploy check') {
            steps {
                withCredentials([sshUserPrivateKey(credentialsId: SSH_CREDENTIALS_ID, keyFileVariable: 'KEYFILE', usernameVariable: 'USERNAME')]) {
                    bat """
                    REM 等待容器启动，然后检查状态与日志（可根据需要延长sleep）
                    timeout /t 5 /nobreak >nul

                    echo Checking container status on ${APP_HOST}...
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker ps --filter name=owlsgame-app --format 'table {{.ID}}\\t{{.Names}}\\t{{.Status}}\\t{{.Ports}}'"

                    echo Showing last 200 lines of app logs...
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "docker logs --tail 200 owlsgame-app"

                    echo Performing quick HTTP check...
                    ssh -i %KEYFILE% -o StrictHostKeyChecking=no %USERNAME%@${APP_HOST} "curl -I --max-time 5 http://localhost:8080/ || true"
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
        failure {
            echo 'Pipeline failed. Check logs.'
        }
    }
}