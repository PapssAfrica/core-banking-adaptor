FROM maven:3.8.7-openjdk-18 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:18-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/core-banking-adaptor-0.0.1-SNAPSHOT.jar /core-banking-adaptor/core-banking-adaptor-0.0.1-SNAPSHOT.jar
ARG GITHUB_BRANCH_NAME
ENV SPRING_PROFILES_ACTIVE=dev

RUN mkdir -p /var/papss/cert
COPY certs/* /var/papss/cert/

EXPOSE 8080

ENTRYPOINT exec java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /core-banking-adaptor/core-banking-adaptor-0.0.1-SNAPSHOT.jar