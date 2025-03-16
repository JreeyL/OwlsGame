pipeline {
    agent any
    tools {
        jdk 'jdk17'         // 与全局工具配置中的名称一致
        maven 'maven3.9'    // 与全局工具配置中的名称一致
    }
    stages {
        // 阶段 1：拉取代码
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        // 阶段 2：编译代码
        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        // 阶段 3：运行测试并生成 JaCoCo 报告
        stage('Test') {
            steps {
                sh 'mvn test'
                jacoco(
                    execPattern: 'target/jacoco.exec',
                    classPattern: 'target/classes',
                    sourcePattern: 'src/main/java',
                    exclusionPattern: 'src/test*'
                )
            }
        }

        // 阶段 4：SonarQube 分析
        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {  // 与 Jenkins 中配置的 SonarQube 名称一致
                    sh 'mvn sonar:sonar \
                        -Dsonar.projectKey=OwlsGame \
                        -Dsonar.java.binaries=target/classes \
                        -Dsonar.sources=src/main/java \
                        -Dsonar.tests=src/test/java'
                }
            }
        }
    }
    post {
        always {
            junit 'target/surefire-reports/**/*.xml'  // 归档 JUnit 报告
            jacoco exclusionPattern: 'src/test*'      // 归档 JaCoCo 报告
        }
    }
}