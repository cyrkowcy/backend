pipeline {
  agent any

  environment {
    DATABASE_HOST     = '127.0.0.1:5432'
    DATABASE_TABLE    = 'tourtool'
    DATABASE_USER     = 'backend'
    DATABASE_PASSWORD = credentials('database-password')
  }

  stages {
    stage('Prepare') {
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
          sh './gradlew ktlintCheck test'
        }

        echo 'End test'
      }
    }

    stage('Build jar') {
      when {
        anyOf {
          branch 'master'
          branch 'prerelease'
        }
      }
      steps {
        echo 'Starting build'
        withGradle() {
          sh './gradlew clean build'
        }

        echo 'Build ended'
      }
    }

    stage('Docker prepare') {
      when {
        anyOf {
          branch 'master'
          branch 'prerelease'
        }
      }
      steps {
        sh 'docker container stop backendrun || true'
        sh 'docker container rm backendrun || true'
        sh 'docker image rm backend || true'
      }
    }

    stage('Migrate database') {
      when {
        anyOf {
          branch 'master'
          branch 'prerelease'
        }
      }
      steps {
        withGradle() {
          sh './gradlew flywayMigrate'
        }
      }
    }

    stage('Docker build') {
      when {
        anyOf {
          branch 'master'
          branch 'prerelease'
        }
      }
      steps {
        dir(path: '/var/lib/jenkins/workspace/backend_master/target/') {
          sh 'docker build . -t backend'
        }

      }
    }

    stage('Docker run') {
      when {
        anyOf {
          branch 'master'
          branch 'prerelease'
        }
      }
      steps {
        sh 'docker run -d -p 8090:8090 --name backendrun -it backend'
      }
    }

  }
}
