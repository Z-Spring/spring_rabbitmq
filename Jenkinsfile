pipeline {
    agent any

    stages {
        stage('pull code...') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '6dd1f548-6158-49a9-a691-83c5d0c300a8', url: 'git@github.com:Z-Spring/spring_rabbitmq.git']]])
            }
        }
        stage('build code...') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: '6dd1f548-6158-49a9-a691-83c5d0c300a8', url: 'git@github.com:Z-Spring/spring_rabbitmq.git']]])
            }
        }
    }
}
