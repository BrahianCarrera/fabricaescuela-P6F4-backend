# Etapa de construcción
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia el JAR generado desde la etapa anterior
COPY --from=build /app/target/fe.jar fe.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "fe.jar"]
