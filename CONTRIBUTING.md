# 🤝 Guía de Contribución

## Para el equipo de UI/Frontend

### Requisitos previos

- Node.js 18+
- npm o yarn

### Endpoints del Backend

#### Login Service (Puerto 8081)

- Base URL: `http://localhost:8081`
- Swagger: `http://localhost:8081/swagger-ui.html`

**Endpoints principales:**

- `POST /api/auth/login` - Iniciar sesión
- `POST /api/auth/refresh` - Renovar token (si está implementado)
- `POST /api/auth/logout` - Cerrar sesión (si está implementado)

#### Inventario Service (Puerto 8080)

- Base URL: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger-ui.html`

**Endpoints principales:**

- `GET /api/paquetes` - Listar paquetes
- `GET /api/paquetes/{codigo}` - Consultar paquete
- `POST /api/paquetes` - Crear paquete
- `PUT /api/paquetes/en-ruta/{codigo}/direccion` - Actualizar dirección
- `POST /api/paquetes/{codigoPaquete}/ubicaciones` - Registrar ubicación
- `GET /api/empleados` - Listar empleados
- `GET /api/novedades` - Listar novedades
- `POST /api/novedades` - Crear novedad
- `GET /api/estados` - Listar estados
- `GET /api/historial-estado` - Consultar historial de estados

### Flujo de autenticación

1. **Login:**

```javascript
const response = await fetch('http://localhost:8081/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ 
    email: 'usuario@example.com', 
    password: 'contraseña' 
  })
});

const data = await response.json();
// Response: { token: "eyJhbGc...", username: "admin", role: "ADMIN", permisos: [...] }

// Guardar en localStorage
localStorage.setItem('token', data.token);
localStorage.setItem('username', data.username);
localStorage.setItem('role', data.role);
```

2. **Usar token en requests:**

```javascript
const token = localStorage.getItem('token');

const response = await fetch('http://localhost:8080/api/paquetes', {
  headers: { 
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
});

const paquetes = await response.json();
```

3. **Manejo de errores 401 (Token expirado):**

```javascript
if (response.status === 401) {
  // Token inválido o expirado
  localStorage.clear();
  window.location.href = '/login';
}
```

### Configuración con Axios (Recomendado)

```javascript
import axios from 'axios';

// Cliente para Login Service
export const apiAuth = axios.create({
  baseURL: 'http://localhost:8081/api/auth'
});

// Cliente para Inventario Service
export const apiInventario = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Interceptor: Agrega token automáticamente
apiInventario.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// Interceptor: Maneja errores 401
apiInventario.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

### Variables de entorno del Frontend

**Para Vite (React/Vue):**

```bash
VITE_API_LOGIN_URL=http://localhost:8081
VITE_API_INVENTARIO_URL=http://localhost:8080
```

**Para Create React App:**

```bash
REACT_APP_API_LOGIN_URL=http://localhost:8081
REACT_APP_API_INVENTARIO_URL=http://localhost:8080
```

**Para Next.js:**

```bash
NEXT_PUBLIC_API_LOGIN_URL=http://localhost:8081
NEXT_PUBLIC_API_INVENTARIO_URL=http://localhost:8080
```

**Para Angular:**

```typescript
// src/environments/environment.ts
export const environment = {
  production: false,
  apiLoginUrl: 'http://localhost:8081',
  apiInventarioUrl: 'http://localhost:8080'
};
```

### Estructura de datos principales

**Login Response:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ADMIN",
  "permisos": ["VER_PAQUETES", "CREAR_PAQUETES", "EDITAR_PAQUETES"]
}
```

**Paquete:**

```json
{
  "codigo": "PKG001",
  "peso": 2.5,
  "direccionRemitente": "Calle 123",
  "direccionDestinatario": "Calle 456",
  "nombreDestinatario": "Juan Pérez",
  "ciudadOrigen": "Bogotá",
  "ciudadDestino": "Medellín",
  "estado": {
    "id": 1,
    "nombre": "EN_RUTA"
  }
}
```

**Novedad:**

```json
{
  "id": 1,
  "descripcion": "Paquete dañado",
  "fechaRegistro": "2025-10-31T10:30:00",
  "paquete": {
    "codigo": "PKG001"
  }
}
```

### Documentación completa

Para más detalles, consulta:

- **Guía completa de Frontend:** `GUIA-FRONTEND-JWT.md`
- **Documentación de Seguridad:** `SEGURIDAD-JWT-README.md`

---

## Para el equipo de Despliegue/DevOps

### Tecnologías

- **Backend:** Spring Boot 3.5.5 con Java 21
- **Base de datos:** PostgreSQL 15+
- **Autenticación:** JWT (stateless)
- **Contenedores:** Docker + Docker Compose

### Arquitectura del Sistema

```
┌─────────────┐
│   Frontend  │
│  (Angular/  │
│   React)    │
└──────┬──────┘
       │
       ├────────────────┬────────────────┐
       │                │                │
       ▼                ▼                ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│   Login     │  │ Inventario  │  │   Nginx     │
│  Service    │  │  Service    │  │ (Opcional)  │
│ (Port 8081) │  │ (Port 8080) │  │             │
└──────┬──────┘  └──────┬──────┘  └─────────────┘
       │                │
       └────────┬───────┘
                ▼
         ┌─────────────┐
         │ PostgreSQL  │
         │   Database  │
         └─────────────┘
```

### Variables de entorno requeridas

#### Login Service

```bash
# Servidor
SERVER_PORT=8081
SPRING_APPLICATION_NAME=login

# JWT Configuration (CRÍTICO: debe ser igual en ambos servicios)
JWT_SECRET=mySecretKeyForJWTTokenGenerationThatIsLongEnough123456789
JWT_EXPIRATION=86400000

# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/login_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=tu_password_seguro

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,https://tu-dominio.com

# JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

#### Inventario Service

```bash
# Servidor
SERVER_PORT=8080
SPRING_APPLICATION_NAME=inventario

# JWT Configuration (CRÍTICO: MISMA clave que login-service)
JWT_SECRET=mySecretKeyForJWTTokenGenerationThatIsLongEnough123456789
JWT_EXPIRATION=86400000

# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/inventario_db
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=tu_password_seguro

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,https://tu-dominio.com

# JPA
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_SHOW_SQL=false
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

### Docker Compose

```yaml
version: '3.8'

services:
  # Base de datos PostgreSQL
  postgres:
    image: postgres:15-alpine
    container_name: couriersync-db
    environment:
      POSTGRES_USER: ${DATABASE_USERNAME:-postgres}
      POSTGRES_PASSWORD: ${DATABASE_PASSWORD:-postgres}
      POSTGRES_DB: couriersync
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./init-db.sql:/docker-entrypoint-initdb.d/init-db.sql
    networks:
      - couriersync-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  # Login Service
  login-service:
    image: couriersync/login-service:latest
    container_name: login-service
    build:
      context: ./login-service
      dockerfile: Dockerfile
    ports:
      - "8081:8081"
    environment:
      SERVER_PORT: 8081
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION:-86400000}
      DATABASE_URL: jdbc:postgresql://postgres:5432/couriersync
      DATABASE_USERNAME: ${DATABASE_USERNAME:-postgres}
      DATABASE_PASSWORD: ${DATABASE_PASSWORD:-postgres}
      CORS_ALLOWED_ORIGINS: ${CORS_ALLOWED_ORIGINS}
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      postgres:
        condition: service_healthy
    networks:
      - couriersync-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8081/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    restart: unless-stopped

  # Inventario Service
  inventario-service:
    image: couriersync/inventario-service:latest
    container_name: inventario-service
    build:
      context: ./inventario-service
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      SERVER_PORT: 8080
      JWT_SECRET: ${JWT_SECRET}
      JWT_EXPIRATION: ${JWT_EXPIRATION:-86400000}
      DATABASE_URL: jdbc:postgresql://postgres:5432/couriersync
      DATABASE_USERNAME: ${DATABASE_USERNAME:-postgres}
      DATABASE_PASSWORD: ${DATABASE_PASSWORD:-postgres}
      CORS_ALLOWED_ORIGINS: ${CORS_ALLOWED_ORIGINS}
      SPRING_JPA_HIBERNATE_DDL_AUTO: update
    depends_on:
      postgres:
        condition: service_healthy
      login-service:
        condition: service_healthy
    networks:
      - couriersync-network
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s
    restart: unless-stopped

  # Nginx (Opcional - Reverse Proxy)
  nginx:
    image: nginx:alpine
    container_name: couriersync-nginx
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx/nginx.conf:/etc/nginx/nginx.conf:ro
      - ./nginx/ssl:/etc/nginx/ssl:ro
    depends_on:
      - login-service
      - inventario-service
    networks:
      - couriersync-network
    restart: unless-stopped

networks:
  couriersync-network:
    driver: bridge

volumes:
  postgres_data:
    driver: local
```

### Archivo `.env` para Docker Compose

```bash
# JWT Configuration (CRÍTICO: Cambiar en producción)
JWT_SECRET=mySecretKeyForJWTTokenGenerationThatIsLongEnough123456789
JWT_EXPIRATION=86400000

# Database
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=tu_password_super_seguro_en_produccion

# CORS (Agregar dominios de producción)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200,https://tu-dominio.com
```

### Script de inicialización de BD (`init-db.sql`)

```sql
-- Crear bases de datos si no existen
CREATE DATABASE IF NOT EXISTS login_db;
CREATE DATABASE IF NOT EXISTS inventario_db;

-- Nota: PostgreSQL crea una base de datos por contenedor
-- Este script es opcional si usas una BD unificada
```

### Dockerfile para servicios Spring Boot

```dockerfile
# Build stage
FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Add health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${SERVER_PORT}/actuator/health || exit 1

EXPOSE ${SERVER_PORT}
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Comandos útiles

**Construir y levantar servicios:**

```bash
docker-compose up -d --build
```

**Ver logs:**

```bash
# Todos los servicios
docker-compose logs -f

# Solo un servicio
docker-compose logs -f inventario-service
```

**Detener servicios:**

```bash
docker-compose down
```

**Detener y eliminar volúmenes:**

```bash
docker-compose down -v
```

**Reiniciar un servicio:**

```bash
docker-compose restart inventario-service
```

### Health Checks

- **Login Service:** `http://localhost:8081/actuator/health`
- **Inventario Service:** `http://localhost:8080/actuator/health`
- **PostgreSQL:** `pg_isready -U postgres`

**Verificar health checks:**

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8080/actuator/health
```

### Configuración de Nginx (Opcional)

```nginx
# nginx/nginx.conf
http {
    upstream login-backend {
        server login-service:8081;
    }

    upstream inventario-backend {
        server inventario-service:8080;
    }

    server {
        listen 80;
        server_name tu-dominio.com;

        # Login Service
        location /api/auth {
            proxy_pass http://login-backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Inventario Service
        location /api {
            proxy_pass http://inventario-backend;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Frontend (si se sirve desde Nginx)
        location / {
            root /usr/share/nginx/html;
            try_files $uri $uri/ /index.html;
        }
    }
}
```

### Monitoreo y Logs

**Ver métricas de Spring Boot:**

```bash
curl http://localhost:8080/actuator/metrics
curl http://localhost:8081/actuator/metrics
```

**Logs en producción:**

```bash
# Configurar log aggregation (ELK Stack, CloudWatch, etc.)
# O usar volumes para persistir logs:
volumes:
  - ./logs:/app/logs
```

### Seguridad en Producción

#### ⚠️ Checklist de Seguridad

- [ ] Cambiar `JWT_SECRET` a un valor aleatorio y seguro (mínimo 256 bits)
- [ ] Usar contraseñas fuertes para PostgreSQL
- [ ] Configurar HTTPS con certificados SSL
- [ ] Limitar CORS solo a dominios permitidos
- [ ] Configurar firewall para limitar acceso a puertos
- [ ] Deshabilitar `spring.jpa.show-sql` en producción
- [ ] Usar variables de entorno para secrets (no hardcodear)
- [ ] Implementar rate limiting
- [ ] Configurar backups automáticos de BD
- [ ] Monitorear logs y métricas

#### Generar JWT Secret seguro:

```bash
# En Linux/Mac
openssl rand -base64 64

# En PowerShell
[Convert]::ToBase64String((1..64 | ForEach-Object { Get-Random -Maximum 256 }))
```

### CI/CD (GitHub Actions)

```yaml
# .github/workflows/deploy.yml
name: Deploy to Production

on:
  push:
    branches: [main]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Build and push Docker images
        run: |
          docker-compose build
          docker-compose push
      
      - name: Deploy to server
        run: |
          ssh user@server "cd /app && docker-compose pull && docker-compose up -d"
```

### Troubleshooting

**Problema: Servicios no se conectan a la BD**

```bash
# Verificar que PostgreSQL esté corriendo
docker-compose ps postgres

# Ver logs de PostgreSQL
docker-compose logs postgres

# Verificar conectividad
docker-compose exec inventario-service ping postgres
```

**Problema: Token JWT inválido**

- Verificar que `JWT_SECRET` sea **exactamente igual** en ambos servicios
- Verificar que no haya espacios al inicio/final del secret

**Problema: CORS errors**

- Verificar `CORS_ALLOWED_ORIGINS` incluye el dominio del frontend
- Asegurarse que no haya espacios en la lista de orígenes

---

## 📞 Contacto y Soporte

- **Documentación del API:** Swagger UI (ver URLs arriba)
- **Documentación completa:**
  - `README.md` - Información general del proyecto
  - `GUIA-FRONTEND-JWT.md` - Guía detallada para frontend
  - `SEGURIDAD-JWT-README.md` - Documentación de seguridad

---

## 🔄 Flujo de trabajo Git

```bash
# 1. Crear rama para feature
git checkout -b feature/mi-feature

# 2. Hacer cambios y commit
git add .
git commit -m "feat: descripción del cambio"

# 3. Push a remoto
git push origin feature/mi-feature

# 4. Crear Pull Request en GitHub

# 5. Después de merge, actualizar main
git checkout main
git pull origin main
```

### Convenciones de commits

- `feat:` - Nueva funcionalidad
- `fix:` - Corrección de bug
- `docs:` - Cambios en documentación
- `style:` - Cambios de formato (no afectan código)
- `refactor:` - Refactorización de código
- `test:` - Agregar o modificar tests
- `chore:` - Tareas de mantenimiento
