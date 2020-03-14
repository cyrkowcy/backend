pipeline {
  agent any
  stages {
    stage('Preapre') {
      steps {
        echo 'Preparing...'
        withGradle() {
          sh 'chmod +x gradlew'
        }

        echo 'Preparing ended'
      }
    }

    stage('Test') {
      steps {
        echo 'Start test'
        withGradle() {
          sh './gradlew test'
        }

        echo 'End test'
      }
    }

    stage('Build jar') {
      steps {
        echo 'Starting build'
        withGradle() {
          sh './gradlew clean build'
        }

        echo 'Build ended'
      }
    }

  }
}