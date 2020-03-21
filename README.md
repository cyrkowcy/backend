# backend
---------

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
(for windows use `set` instead of `export`)

### Working with the project

Start app:
```
export DATABASE_HOST=127.0.0.1:5432
export DATABASE_NAME=table-name
export DATABASE_USER=postgres
export DATABASE_PASSWORD=123456
./gradlew run
```
or run the main class from IDE, don't forget to setup environment variables.

Run tests:
```
./gradlew test
```
or run tests from IDE.

Build fat jar:
```
./gradlew shadowJar
```
