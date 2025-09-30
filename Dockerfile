# Multi-stage build para optimizar el tamaño de la imagen
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn



# Copiar código fuente
COPY src src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Imagen de runtime
FROM eclipse-temurin:21

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


# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
