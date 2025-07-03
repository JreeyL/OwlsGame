pipeline {
    agent any

    tools {
        maven 'maven-3.99'  // 使用您 Jenkins 中配置的名称
        jdk 'java-21'       // 使用您 Jenkins 中配置的名称
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/JreeyL/OwlsGame.git'
            }
        }

        stage('Build & Test') {
            steps {
                bat 'mvn clean verify'  // 在 Windows 上使用 bat 而不是 sh
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
                // 使用您 Jenkins 中配置的 SonarQube 名称
                withSonarQubeEnv('sonarqube-local') {
                    bat 'mvn sonar:sonar'  // 在 Windows 上使用 bat
                }
            }
        }

        stage('Docker Build') {
            steps {
                // 注意 Windows 环境变量语法
                bat 'docker build -t owlsgame:%BUILD_NUMBER% .'
                bat 'docker tag owlsgame:%BUILD_NUMBER% owlsgame:latest'
            }
        }

        stage('Deploy') {
            steps {
                // 直接使用 docker-compose 文件来部署，网络问题自动解决
                bat 'docker-compose down || exit 0'
                bat 'docker-compose up -d --build'
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

    triggers {
        pollSCM('H 10 * * *')  // 时间可调整
        githubPush()
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
        always {
            // Windows 批处理命令清理旧镜像
            bat 'docker image prune -f'
        }
    }
}