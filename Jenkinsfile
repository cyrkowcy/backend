pipeline {
  agent any
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
      steps {
        echo 'Starting build'
        withGradle() {
          sh './gradlew clean build'
        }

        echo 'Build ended'
      }
    }

    stage('Migrate database') {
      when {
        anyOf {
          branch 'master'
        }

      }
      steps {
        withGradle() {
          sh './gradlew flywayMigrate'
        }

      }
    }

    stage('Docker prepare phase test') {
      when {
        anyOf {
          branch 'master'
        }

      }
      steps {
        sh 'docker stop backendruntest || true'
        sh 'docker rm backendruntest  || true'
        sh 'docker image rm backendtest || true'
      }
    }

    stage('Docker build phase test') {
      when {
        anyOf {
          branch 'master'
        }

      }
      steps {
        dir(path: '/var/lib/jenkins/workspace/backend_master/target/') {
          sh 'docker build . -t backendtest'
        }

      }
    }

    stage('Docker run phase test') {
      when {
        anyOf {
          branch 'master'
        }

      }
      steps {
        sh 'docker run -d -p 8091:8090 --name backendruntest -it backendtest'
      }
    }

    stage('Docker prepare ') {
      when {
        anyOf {
          branch 'master'
        }

      }
      steps {
        sh 'docker stop backendrun || true'
        sh 'docker rm backendrun  || true'
        sh 'docker image rm backend || true'
      }
    }

    stage('Docker build') {
      when {
        anyOf {
          branch 'master'
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
        }

      }
      steps {
        sh 'docker run -d -p 8090:8090 --name backendrun -it backend'
      }
    }

  }
  environment {
    DATABASE_HOST = '127.0.0.1:5432'
    DATABASE_TABLE = 'tourtool'
    DATABASE_USER = 'backend'
    DATABASE_PASSWORD = credentials('database-password')
  }
}