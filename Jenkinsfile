pipeline {
    agent any

    tools {
        maven 'maven-3.99'  // 确保名称与 Jenkins 全局工具一致
        jdk 'java-23'        // 确保名称与 Jenkins 全局工具一致
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'master', url: 'https://github.com/JreeyL/OwlsGame.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                    jacoco execPattern: '**/target/jacoco.exec'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                // 使用 Jenkins 中配置的 SonarQube 服务器名称
                withSonarQubeEnv('sonarqube-local') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Report') {
            steps {
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                publishHTML target: [
                    allowMissing: false,
                    alwaysLinkToLastBuild: false,
                    keepAll: true,
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Report'
                ]
            }
        }
    }

    triggers {
        pollSCM('H 10 * * *')  // 保持或调整时间
        githubPush()
    }

    post {
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}