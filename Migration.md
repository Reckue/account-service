# How to create migration using Liquibase

## Step 1
First of all add a dependency to build.gradle
```
implementation 'org.liquibase:liquibase-core:4.1.1'
```
For a newer version of liquibase-core go [here](https://mvnrepository.com/artifact/org.liquibase/liquibase-core).

## Step 2
You need to create a master changelog file.

Changelog files can be in either SQL, YAML, XML, or JSON format.

By default, the name of this file must be "db.changelog-master.yaml" and be situated by the path:
"resources/db/migration/db.changelog-master.yaml".

If you want to change the location, or the name of master changelog file, you need to add such information to 
application.yml.

For example,
```yaml
spring:
  liquibase:
    changeLog: "classpath:migrations/changelog-master.yaml"
```

## Step 3
Every time you want to make any changes to the database, you need to create a change log file with a set of changes and
place information about this file in the master changelog file.

For example, you want to create a table "users".

The master changelog file would be like this:
```yaml
databaseChangeLog:
  - include:
      file: migrations/0001-create-users.yaml
```
Then you must create the file "0001-create-users.yaml" and place it to the folder with 
master changelog file.

```yaml
databaseChangeLog:
  - changeSet:
      id: create-table-users
      author: camelya
      preConditions:
        - onFail: MARK_RAN
          not:
            tableExists:
              tableName: users
      changes:
        - createTable:
            columns:
              - column:
                  autoIncrement: false
                  constraints:
                    nullable: false
                    primaryKey: true
                    primaryKeyName: users_pkey
                  name: id
                  type: VARCHAR(255)
              - column:
                  constraints:
                    nullable: false
                  name: status
                  type: integer
              - column:
                  constraints:
                    nullable: false
                  name: username
                  type: VARCHAR(255)
              - column:
                  constraints:
                    nullable: false
                  name: email
                  type: VARCHAR(255)
              - column:
                  constraints:
                    nullable: false
                  name: password
                  type: VARCHAR(255)
              - column:
                  constraints:
                    nullable: false
                  name: refresh_token
                  type: VARCHAR(255)
              - column:
                  constraints:
                    nullable: false
                  name: last_visit
                  type: bigint
              - column:
                  constraints:
                    nullable: false
                  name: created
                  type: bigint
              - column:
                  constraints:
                    nullable: false
                  name: updated
                  type: bigint
            tableName: users
```

## Step 4
Run a project. Liquibase migrations will run automatically on startup.


For more information about another kinds of changes go to: https://docs.liquibase.com/change-types/community/create-table.html