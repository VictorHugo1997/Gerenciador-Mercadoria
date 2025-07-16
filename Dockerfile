# Use uma imagem base oficial do OpenJDK (Java)
FROM openjdk:17-jdk-slim

# Defina o diretório de trabalho dentro do container
WORKDIR /app

COPY target/Usuario-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]