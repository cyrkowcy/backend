pipeline {
  agent any
  stages {
    stage('Prepare') {
      steps {
        withGradle() {
          sh 'chmod +x gradlew'
        }

      }
    }

    stage('Test') {
      steps {
        withGradle() {
          sh './gradlew clean ktlintCheck test --info'
        }

      }
    }

    stage('Build jar') {
      steps {
        withGradle() {
          sh './gradlew clean shadowJar'
        }

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
        sh 'docker run -d -p 8091:8090 -e DATABASE_HOST=172.18.0.4:5432 -e DATABASE_NAME -e DATABASE_USER -e DATABASE_PASSWORD -e APP_SECRET --name backendruntest --restart always --net netapp -it backendtest'
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
        sh 'docker run -d -p 8090:8090 -e DATABASE_HOST=172.18.0.4:5432 -e DATABASE_NAME -e DATABASE_USER -e DATABASE_PASSWORD -e APP_SECRET --name backendrun --restart always --net netapp -it backend'
      }
    }

  }
  environment {
    DATABASE_HOST = '127.0.0.1:5432'
    DATABASE_NAME = 'tourtool'
    DATABASE_USER = 'backend'
    DATABASE_PASSWORD = credentials('database-password')
    APP_SECRET = credentials('app-secret')
    For stage env: STAGE=sta
    For prod env: STAGE=pro
    }
}
