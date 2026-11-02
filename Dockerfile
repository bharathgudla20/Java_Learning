FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp verify

FROM eclipse-temurin:21-jre-jammy
RUN groupadd --system stockroom && useradd --system --gid stockroom --home-dir /app stockroom
WORKDIR /app
COPY --from=build --chown=stockroom:stockroom /app/target/stockroom-1.0.0.jar app.jar
RUN mkdir -p /app/data && chown stockroom:stockroom /app/data
USER stockroom
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8081
ENTRYPOINT ["java","-jar","app.jar"]
