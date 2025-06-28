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
                bat 'docker stop owlsgame-container || exit 0'  // Windows 错误处理
                bat 'docker rm owlsgame-container || exit 0'
                bat 'docker run -d -p 9090:8080 --name owlsgame-container -e SPRING_PROFILES_ACTIVE=prod owlsgame:latest'
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