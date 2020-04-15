# backend
[![Build Status](http://149.156.146.249:60001/jenkins/job/backend/job/master/badge/icon?style=flat-square)](http://149.156.146.249:60001/jenkins/job/backend/job/master/)

Open [Swagger](http://149.156.146.249:60001/api/swagger/index.html)

### Setup

1. Clone the repository
2. Disable line ending normalization
```
git config core.autocrlf false
```
3. Open the project in Intellij IDEA
4. Install PostgresSQL
5. Create empty database in PostgresSQL
```sql
create database tourtool;
```
6. Perform initial database migrations
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

Start app:
```
export DATABASE_HOST=127.0.0.1:5432
export DATABASE_NAME=tourtool
export DATABASE_USER=postgres
export DATABASE_PASSWORD=123456
export APP_SECRET=123456
./gradlew run
```
or run the main class from IDE, don't forget to setup environment variables.

Before committing run:
```
./gradlew clean ktlintCheck test --info
```

Run tests:
```
./gradlew test
```
or run tests from IDE.

Build fat jar:
```
./gradlew shadowJar
```

