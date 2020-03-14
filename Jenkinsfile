pipeline {
  agent any
  stages {
    stage('Build') {
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

  }
}