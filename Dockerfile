FROM openjdk:17-jdk-slim AS build

# Instalar Maven
RUN apt-get update && apt-get install -y maven

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar archivos necesarios
COPY pom.xml /app/
COPY src /app/src/

# Construir la aplicación
RUN mvn clean package -DskipTests --debug

FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/target/*.jar app.jar

# Copiar el archivo de credenciales
COPY --from=build /app/src/main/resources/documentmanager-436319-7eda386e0f86.json /app/

ENV GOOGLE_APPLICATION_CREDENTIALS=/app/documentmanager-436319-7eda386e0f86.json

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
