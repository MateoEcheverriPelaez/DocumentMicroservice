FROM openjdk:17-jdk-slim AS build

# Instalar Maven
RUN apt-get update && apt-get install -y maven

COPY pom.xml /app/
COPY src /app/src/
WORKDIR /app

RUN mvn clean package -DskipTests --debug && ls -l target/

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/documentmanager-436319-7eda386e0f86.json

COPY src/main/resources/documentmanager-436319-7eda386e0f86.json /app/

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]