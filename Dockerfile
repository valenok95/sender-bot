FROM gradle:8.13 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src

RUN gradle clean bootJar

FROM openjdk:21-jdk-slim AS prod
WORKDIR /app
ENV JVM_OPTS=-Xmx256g

COPY --from=build /home/gradle/src/build/libs/*.jar /app/bot.jar
ENTRYPOINT exec java -jar bot.jar