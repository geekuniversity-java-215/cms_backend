pipeline {
    agent { docker { image 'maven:3.6.3-jdk-8' } }
    stages {
        // madness
        stage('dependencies') {
            steps {
                sh 'wget http://ya.ru'
            }
        }
        stage('build') {
            steps {
                sh 'mvn compile'
            }
        }
        stage('test') {
            steps {
                sh 'mvn test'
            }
        }
    }
}