FROM node:lts-alpine as ui_build
WORKDIR scheduler-app
COPY ./ .
RUN npm --prefix vue-app install
RUN npm run --prefix vue-app build -- --mode prod
RUN cp -r vue-app/dist/ src/main/resources/static/

# build backend jar
FROM gradle:jdk8
ENV GRADLE_USER_HOME .gradle
WORKDIR scheduler-app
COPY --from=ui_build scheduler-app/ .
RUN chmod +x gradlew
RUN sh gradlew build -x test

CMD java \
    -jar build/libs/spring-boot-quartz-scheduler-1.0-SNAPSHOT.jar
