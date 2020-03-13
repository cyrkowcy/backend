pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        echo 'Start Build'
        withGradle() {
          sh 'chmod +x gradlew'
          sh './gradlew clean build'
        }

        echo 'End build'
      }
    }

  }
}