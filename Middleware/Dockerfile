FROM maven:3.6.3-openjdk-8-slim as builder
WORKDIR /tmp/
COPY pom.xml /tmp/
RUN mvn dependency:resolve
COPY src /tmp/src
RUN mvn package

FROM openjdk:8-jre-alpine as runner
COPY --from=builder /tmp/target/*.jar /app/AICMIDDLEWARE-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/AICMIDDLEWARE-0.0.1-SNAPSHOT.jar"]
