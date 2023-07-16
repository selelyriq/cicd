pipeline {
  agent any

  tools {
    maven 'maven3'
    jdk 'JDK8'
  }

  stages {
    stage('Checkout') {
      steps {
        checkout scm
      }
    }

    stage('Build maven') {
      steps {
        sh 'mvn clean install package'
      }
    }

    stage('Copy Artifact') {
      steps {
        sh 'cp -r target/*.jar docker'
      }
    }

    stage('Build docker image') {
      steps {
        script {
          def customImage = docker.build('initsixcloud/petclinic', "./docker")
          docker.withRegistry('https://hub.docker.com/repository/docker/lyriqsele/dockerhub/general', 'dockerhub') {
            customImage.push("${env.BUILD_NUMBER}")
          }
        }
      }
    }
  }
}
