# Build source and run the integration tests inside a Java 21 environment.
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src ./src
RUN mvn -B verify

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
RUN groupadd --system app && useradd --system --gid app --home /app app
COPY --from=build --chown=app:app /workspace/target/job-application-tracker-2.0.0.jar /app/app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=50", "-XX:+ExitOnOutOfMemoryError", "-jar", "/app/app.jar"]
