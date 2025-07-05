pipeline {
    agent any

    tools {
        maven 'maven-3.99'
        jdk 'java-21'
    }

    environment {
        DOCKERHUB_CREDENTIALS_ID = 'dockerhub-credentials'
        DOCKER_IMAGE_NAME = "jiyuli/owlsgame"
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
                // 关键改动：构建时直接使用符合 Docker Hub 格式的名字
                // 同时打上版本号和 latest 两个标签
                bat "docker build -t ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER} -t ${DOCKER_IMAGE_NAME}:latest ."
            }
        }

        // 新增的 Push 阶段
        stage('Push to Docker Hub') {
            steps {
                // 使用 withCredentials 块来安全地处理密码
                withCredentials([usernamePassword(credentialsId: DOCKERHUB_CREDENTIALS_ID, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    echo "Logging in to Docker Hub as ${DOCKER_USER}..."
                    // 使用 --password-stdin 安全登录
                    bat "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"

                    echo "Pushing image tag: ${env.BUILD_NUMBER}"
                    bat "docker push ${DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"

                    echo "Pushing image tag: latest"
                    bat "docker push ${DOCKER_IMAGE_NAME}:latest"
                }
            }
        }

        // 当前的 Deploy 阶段是在本地部署，这对于 CI/CD 来说只是一个中间步骤
        // 真正的 CD (Continuous Deployment) 是部署到云服务器
        // 我们把它重命名并保留，但可以根据需要禁用它
        stage('Deploy Locally (for testing)') {
            steps {
                echo "Deploying on the Jenkins agent machine..."
                bat 'docker-compose down' // 注意：这里会停掉您本地正在运行的服务
                bat 'docker-compose up -d' // 注意：这里会用本地的 compose 文件启动
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
            // 登出 Docker Hub
            bat 'docker logout'
            // 清理悬空镜像
            bat 'docker image prune -f'
        }
    }
}