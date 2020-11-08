# spring-boot-quartz-scheduler
Spring Boot + Quartz + vueJs(Vuetify) application. 

Introduction:
--------------------
The solution provide managing and scheduling jobs which makes HTTP calls to supplied endpoint.
The good thing about the scheduler is that we can deploy it in clusters.(Inspired by quartz cluster mode setting)

 ![Screenshot](Screenshot_vueUi.png)
 
System Requirements:
--------------------
- OpenJDK for Java 1.8
- Git
- Project Lombok https://projectlombok.org
- Flyway schema migration tool

Building the example project:
-----------------------------

Environment variable:

    export DB_NAME='<your_db_name>'
    export DB_HOST='<your_db_host>'
    export DB_PORT='<your_db_port>'
    export DB_USERNAME='<your_db_user>'
    export DB_PASSWORD='<your_db_password>'

Quartz schema creation:

    ./gradlew flywayMigrate 
    #Note : this is alreday initialise by spring hibernate.ddl-auto: update property
    
To build the fat JAR and run tests:

    ./gradlew build test

Jacoco tst coverage:
    
    ./gradlew jacocoTestReport
    
Run:

    java -jar build/libs/spring-boot-quartz-scheduler-1.0-SNAPSHOT.jar
    OR
    ./gradlew bootRun

Docker:

       ## build
       docker build -t needubey/quartz_scheduler
       
       # RUN
       docker run --name quartz_scheduler \
           -p 1234:1234 \
           --env DB_NAME="quartz_scheduler" \
           --env DB_HOST="postgres" \
           --env DB_PORT="5432" \
           --env DB_USER_NAME="postgres" \
           --env DB_PASSWORD="postgres" \
           --detach \
           needubey/quartz_scheduler
           
Docker-Compose:
    
    
    version: '3'
    services:
      postgres:
        container_name: postgres
        image: postgres
        environment:
          POSTGRES_DB: ${DB_NAME:-quartz_scheduler}
          POSTGRES_USER: ${DB_USERNAME:-postgres}
          POSTGRES_PASSWORD: ${DB_PASSWORD:-postgres}
          PGDATA: /data/postgres
        volumes:
          - postgres:/data/postgres
        ports:
          - "5432:5432"
        networks:
          - postgres
        restart: unless-stopped
    
      quartzscheduler:
        depends_on:
          - "postgres"
        container_name: quartz_scheduler
        build: .
        image: needubey/quartz_scheduler
        environment:
          DB_NAME: ${DB_NAME:-quartz_scheduler}
          DB_HOST: ${DB_HOST:-postgres}
          DB_PORT: 5432
          DB_USERNAME: ${DB_USERNAME:-postgres}
          DB_PASSWORD: ${DB_PASSWORD:-postgres}
        ports:
          - "${SCHEDULER_PORT:-1234}:1234"
        networks:
          - postgres
        restart: unless-stopped
    
    networks:
      postgres:
        driver: bridge
    
    volumes:
      postgres:
    
Urls:
-----------------------------
application -> http://localhost:1234/

swagger -> http://localhost:1234/swagger-ui.html

Assumptions:
-------------------------
- Vue js UI project could be run separately.
However, we can run it together by copying the vue-app/dist/ to src/main/resources/static/ folder
- To generate the Vue js UI build follow ==> [link to section](./vue-app/README.md)

References:
-----------
