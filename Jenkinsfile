pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'jdk21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
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
                    junit '**/target/surefire-reports/TEST-*.xml'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t owlsgame:${BUILD_NUMBER} .'
                sh 'docker tag owlsgame:${BUILD_NUMBER} owlsgame:latest'
            }
        }

        stage('Docker Push') {
            when {
                branch 'main'
            }
            steps {
                // Docker Hub push example code commented out
                echo "Image built successfully: owlsgame:${BUILD_NUMBER}"
            }
        }

        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                sh 'docker stop owlsgame-container || true'
                sh 'docker rm owlsgame-container || true'
                sh 'docker run -d -p 9090:8080 --name owlsgame-container -e SPRING_PROFILES_ACTIVE=prod owlsgame:latest'
            }
        }
    }

    post {
        success {
            echo 'Build successful!'
        }
        failure {
            echo 'Build failed!'
        }
        always {
            // Clean up old Docker images (keeping the 5 most recent)
            sh '''
                docker image prune -f
                docker images "owlsgame" --format "{{.ID}}" | sort | head -n -5 | xargs -r docker rmi -f
            '''
        }
    }
}