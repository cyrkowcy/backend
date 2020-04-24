# backend
[![Build Status](http://149.156.146.249:60001/jenkins/job/backend/job/master/badge/icon?style=flat-square)](http://149.156.146.249:60001/jenkins/job/backend/job/master/)

Open [Swagger](http://149.156.146.249:60001/api-pre/swagger/index.html)

### Environments

Staging environment (PRE)
```
http://149.156.146.249:60001/api-pre
```

Production environment (PRO)
```
http://149.156.146.249:60001/api
```


### Setup

1. Clone the repository
2. Disable line ending normalization
```
git config core.autocrlf false
```
3. Open the project in Intellij IDEA
4. Disable wildcard imports in Intellij IDEA settings. See [ktlint](https://github.com/pinterest/ktlint#option-3) documentation. 
5. Install PostgresSQL
6. Create empty database in PostgresSQL
```sql
create database tourtool;
```
7. Perform initial database migrations
```
export DATABASE_HOST=127.0.0.1:5432
export DATABASE_NAME=tourtool
export DATABASE_USER=postgres
export DATABASE_PASSWORD=123456
./gradlew flywayMigrate
```

Example for Windows:
```
set DATABASE_HOST=127.0.0.1:5432
set DATABASE_NAME=tourtool
set DATABASE_USER=postgres
set DATABASE_PASSWORD=123456
gradlew flywayMigrate
```

### Working with the project

##### Start app
```
export DATABASE_HOST=127.0.0.1:5432
export DATABASE_NAME=tourtool
export DATABASE_USER=postgres
export DATABASE_PASSWORD=123456
export APP_SECRET=123456
export STAGE=LOCAL
./gradlew run
```
or run the main class from IDE, don't forget to setup environment variables.

Before committing make sure that linter checks and tests passes:
```
./gradlew clean ktlintCheck test
```

##### Run tests
```
./gradlew test
```
or run tests from IDE.

##### Build fat jar
```
./gradlew shadowJar
```

##### Swagger  
When updating endpoints you will have to update API definition
located under `api/src/main/resources/webroot/api.yaml`.  
Use [Swagger Editor](https://editor.swagger.io/) for preview and validation.

##### Local Swagger

To have working local Swagger UI you must run once:
```json
gradlew copySwaggerUi
```
This will copy Swagger files to `/api/src/main/resources/webroot/swagger` 
(this directory is excluded from Git)

Then after starting app you can access Swagger at
```json
http://127.0.0.1:8090/swagger/index.html
```
You will need to change yml path at the top to `/api.yaml`
