# Multi-stage build para optimizar el tamaño de la imagen
FROM maven:3.9.6-openjdk-21-slim AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (esto se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Imagen de runtime
FROM eclipse-temurin:21-jdk-alpine

# Instalar curl para health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN groupadd -r spring && useradd -r -g spring spring

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR construido desde la etapa de build
COPY --from=build /app/target/inventario-*.jar app.jar

# Cambiar ownership al usuario spring
RUN chown spring:spring app.jar

# Cambiar al usuario no-root
USER spring

# Exponer puerto
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
