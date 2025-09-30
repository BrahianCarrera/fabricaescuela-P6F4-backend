# 🐳 Docker Configuration para Fabrica Escuela - Inventario

Este proyecto incluye configuración completa de Docker para desplegar la aplicación Spring Boot en Render.com.

## 📁 Archivos creados

- `Dockerfile` - Imagen Docker optimizada con multi-stage build
- `.dockerignore` - Archivos a ignorar durante el build
- `docker-compose.yml` - Configuración para desarrollo local
- `render.yaml` - Configuración específica para Render.com
- `application-production.properties` - Configuración de producción

## 🚀 Despliegue en Render.com

### 1. Preparar el repositorio
Asegúrate de que todos los archivos estén en tu repositorio Git:
```bash
git add .
git commit -m "Add Docker configuration for Render deployment"
git push origin main
```

### 2. Configurar en Render.com

1. **Crear un nuevo Web Service** en Render.com
2. **Conectar tu repositorio** de GitHub
3. **Configurar el servicio:**
   - **Name**: `inventario-backend` (o el nombre que prefieras)
   - **Runtime**: `Docker`
   - **Dockerfile Path**: `Dockerfile` (dejar por defecto)
   - **Port**: `8080`

### 3. Configurar variables de entorno

En la sección "Environment Variables" de Render, agregar:

```
SPRING_PROFILES_ACTIVE=production
DATABASE_URL=jdbc:postgresql://tu-host:5432/tu-database
DATABASE_USERNAME=tu-usuario
DATABASE_PASSWORD=tu-password
```

### 4. Configurar base de datos PostgreSQL

1. **Crear una nueva PostgreSQL Database** en Render
2. **Copiar las credenciales** de conexión
3. **Usar esas credenciales** en las variables de entorno del Web Service

## 🏠 Desarrollo local con Docker

### Opción 1: Solo la aplicación
```bash
# Construir la imagen
docker build -t inventario-app .

# Ejecutar el contenedor
docker run -p 8080:8080 inventario-app
```

### Opción 2: Con docker-compose (recomendado)
```bash
# Levantar todos los servicios
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Parar los servicios
docker-compose down
```

## 🔧 Configuración de la base de datos

### Para desarrollo local:
El `docker-compose.yml` incluye una base de datos PostgreSQL configurada automáticamente.

### Para producción (Render):
1. Crear una PostgreSQL Database en Render
2. Usar las credenciales proporcionadas en las variables de entorno

## 📊 Health Checks

La aplicación incluye health checks configurados:
- **Endpoint**: `http://localhost:8080/actuator/health`
- **Docker**: Health check automático cada 30 segundos

## 🛠️ Comandos útiles

```bash
# Ver logs de la aplicación
docker-compose logs -f app

# Entrar al contenedor
docker exec -it inventario-app bash

# Reconstruir la imagen
docker-compose build --no-cache

# Limpiar volúmenes
docker-compose down -v
```

## 🔍 Troubleshooting

### Error de conexión a la base de datos
- Verificar que las variables de entorno estén configuradas correctamente
- Asegurarse de que la base de datos PostgreSQL esté ejecutándose

### Error de puerto
- Verificar que el puerto 8080 esté disponible
- Cambiar el puerto en `docker-compose.yml` si es necesario

### Error de build
- Verificar que Java 21 esté disponible
- Limpiar cache de Docker: `docker system prune -a`

## 📝 Notas importantes

- La aplicación usa **Java 21** y **Spring Boot 3.5.5**
- El build es **multi-stage** para optimizar el tamaño de la imagen
- Se ejecuta con un **usuario no-root** por seguridad
- Incluye **health checks** automáticos
- Configuración separada para **desarrollo** y **producción**
