# 🔐 Configuración de Seguridad JWT - Microservicio Inventario

## ✅ Cambios Realizados

### 1️⃣ Dependencias Agregadas en `pom.xml`

Se agregaron las siguientes dependencias:
- **Spring Security** - Framework de seguridad
- **JJWT (JSON Web Token)** - Librería para manejo de tokens JWT
  - `jjwt-api` (0.12.3)
  - `jjwt-impl` (0.12.3)
  - `jjwt-jackson` (0.12.3)

### 2️⃣ Clases de Seguridad Creadas (SINCRONIZADAS con login-service)

#### 📁 `src/main/java/com/fabricaescuela/security/`

1. **JwtUtil.java**
   - Utilidad para validar tokens JWT
   - Extrae información (username, role, permisos) de los tokens
   - Usa la misma secret key que el servicio de autenticación
   - ✨ **Nuevo**: Soporte para roles y permisos

2. **JwtAuthenticationFilter.java**
   - Filtro que intercepta todas las peticiones HTTP
   - Extrae el token del header `Authorization`
   - Valida el token y establece la autenticación con roles y permisos
   - ✨ **Nuevo**: Mapea roles como `ROLE_ADMIN`, `ROLE_EMPLEADO`, etc.
   - ✨ **Nuevo**: Mapea permisos individuales

3. **SecurityConfig.java**
   - Configuración de seguridad de Spring
   - Habilita `@EnableMethodSecurity` para usar anotaciones de seguridad
   - Define qué endpoints son públicos y cuáles requieren autenticación
   - Configuración CORS integrada
   - Endpoints públicos:
     - `/v3/api-docs/**` - Documentación Swagger
     - `/swagger-ui/**` - UI de Swagger
     - `/actuator/health` - Health check

### 3️⃣ Configuración Actualizada

#### `application.properties`

```properties
# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsLongEnough123456789
jwt.expiration=86400000  # 24 horas en milisegundos

# CORS Configuration
cors.allowed-origins=http://localhost:3000,http://localhost:4200,http://localhost:8081
```

#### `CorsConfig.java`
- Actualizado para leer los orígenes permitidos desde `application.properties`
- Agregado soporte para credenciales y caché de preflight

## 🚀 Cómo Funciona

### Flujo de Autenticación con Roles y Permisos

1. **Cliente hace login** en el servicio de autenticación
   - Recibe un token JWT con información de:
     - Username
     - Role (ADMIN, EMPLEADO, etc.)
     - Permisos específicos

2. **Cliente envía petición** al servicio de inventario
   - Incluye el token en el header: `Authorization: Bearer <token>`

3. **JwtAuthenticationFilter intercepta** la petición
   - Extrae el token del header
   - Valida el token usando `JwtUtil`
   - Extrae role y permisos del token
   - Establece la autenticación en el contexto de Spring Security con:
     - Username como principal
     - Role como `ROLE_<nombre_role>`
     - Lista de permisos individuales

4. **SecurityConfig permite o deniega** el acceso
   - Si el token es válido → ✅ Acceso permitido
   - Si el token es inválido → ❌ 401 Unauthorized
   - Si el rol/permiso no es suficiente → ❌ 403 Forbidden

## 🔐 Usando Roles y Permisos en Controladores

### Opción 1: Anotaciones a nivel de método

```java
@RestController
@RequestMapping("/api/paquetes")
public class PaqueteController {

    // Solo usuarios con rol ADMIN pueden acceder
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPaquete(@PathVariable Long id) {
        // código
    }

    // Solo usuarios con el permiso específico pueden acceder
    @PreAuthorize("hasAuthority('PAQUETE_CREAR')")
    @PostMapping
    public ResponseEntity<?> crearPaquete(@RequestBody PaqueteDto dto) {
        // código
    }

    // Múltiples roles permitidos
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
    @GetMapping
    public ResponseEntity<?> listarPaquetes() {
        // código
    }

    // Combinación de roles y permisos
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PAQUETE_CONSULTAR')")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPaquete(@PathVariable Long id) {
        // código
    }
}
```

### Opción 2: Anotaciones a nivel de clase

```java
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // Toda la clase requiere rol ADMIN
public class AdminController {
    
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas() {
        // código
    }
}
```

### Opción 3: Obtener información del usuario autenticado

```java
@RestController
@RequestMapping("/api/perfil")
public class PerfilController {

    @GetMapping
    public ResponseEntity<?> obtenerPerfil(Authentication authentication) {
        // Obtener username
        String username = authentication.getName();
        
        // Obtener roles
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        
        // Verificar si tiene un rol específico
        boolean isAdmin = authorities.stream()
            .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        
        return ResponseEntity.ok(Map.of(
            "username", username,
            "authorities", authorities,
            "isAdmin", isAdmin
        ));
    }
}
```

## 📝 Uso en el Frontend

### Ejemplo de petición con token

```javascript
// Después de hacer login y obtener el token
const token = localStorage.getItem('token');

// Hacer petición al inventario
fetch('http://localhost:8080/api/paquetes', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

### Ejemplo con Axios

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Interceptor para agregar el token a todas las peticiones
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// Usar el cliente configurado
api.get('/paquetes').then(response => console.log(response.data));
```

## 🔧 Configuración Importante

### ⚠️ Secret Key

**CRÍTICO**: La `jwt.secret` debe ser **EXACTAMENTE LA MISMA** en:
- ✅ Servicio de Autenticación (login-service)
- ✅ Servicio de Inventario (inventario-service)

Si las claves son diferentes, los tokens generados por un servicio no serán válidos en el otro.

**Secret Key Actual:**
```
mySecretKeyForJWTTokenGenerationThatIsLongEnough123456789
```

### 🔄 CORS

Los orígenes permitidos están configurados para desarrollo local:
- `http://localhost:3000` - React, Vue, etc.
- `http://localhost:4200` - Angular
- `http://localhost:8081` - Otro servicio o frontend

Para producción, actualiza estos valores en `application.properties`.

## 🧪 Probar la Configuración

### 1. Endpoint Público (sin token)
```bash
# Health check - debe funcionar sin token
curl http://localhost:8080/actuator/health

# Swagger - debe funcionar sin token
curl http://localhost:8080/swagger-ui/index.html
```
✅ Debe funcionar sin token

### 2. Endpoint Protegido (con token)
```bash
# Sin token - debe fallar con 401
curl http://localhost:8080/api/paquetes

# Con token - debe funcionar
curl -H "Authorization: Bearer <tu-token-jwt>" http://localhost:8080/api/paquetes
```

### 3. Endpoint con restricción de roles
```bash
# Con token de EMPLEADO intentando acceder a endpoint de ADMIN - debe fallar con 403
curl -H "Authorization: Bearer <token-empleado>" http://localhost:8080/api/admin/estadisticas

# Con token de ADMIN - debe funcionar
curl -H "Authorization: Bearer <token-admin>" http://localhost:8080/api/admin/estadisticas
```

## 📊 Estados de Respuesta

- **200 OK** - Petición exitosa con token válido y permisos suficientes
- **401 Unauthorized** - Token inválido, expirado o no proporcionado
- **403 Forbidden** - Token válido pero sin los permisos necesarios

## 🎯 Estructura del Token JWT

El token JWT generado por el login-service contiene:

```json
{
  "sub": "juan@example.com",           // Username (email)
  "role": "ADMIN",                      // Rol del usuario
  "permisos": [                         // Lista de permisos
    "PAQUETE_CREAR",
    "PAQUETE_EDITAR",
    "PAQUETE_ELIMINAR",
    "EMPLEADO_GESTIONAR"
  ],
  "iat": 1698765432,                    // Fecha de emisión
  "exp": 1698851832                     // Fecha de expiración
}
```

## ✅ Compilación Exitosa

El proyecto se compiló correctamente con todas las dependencias y clases actualizadas:
```
BUILD SUCCESS
Total time: 9.290 s
```

---

**Fecha de configuración**: 31 de Octubre, 2025  
**Versión Spring Boot**: 3.5.5  
**Versión JWT**: 0.12.3  
**Estado**: ✅ Sincronizado con login-service


### 3️⃣ Configuración Actualizada

#### `application.properties`

```properties
# JWT Configuration
jwt.secret=mySecretKeyForJWTTokenGenerationThatIsLongEnough123456789
jwt.expiration=86400000  # 24 horas en milisegundos

# CORS Configuration
cors.allowed-origins=http://localhost:3000,http://localhost:4200,http://localhost:8081
```

#### `CorsConfig.java`
- Actualizado para leer los orígenes permitidos desde `application.properties`
- Agregado soporte para credenciales y caché de preflight

## 🚀 Cómo Funciona

### Flujo de Autenticación

1. **Cliente hace login** en el servicio de autenticación
   - Recibe un token JWT

2. **Cliente envía petición** al servicio de inventario
   - Incluye el token en el header: `Authorization: Bearer <token>`

3. **JwtAuthenticationFilter intercepta** la petición
   - Extrae el token del header
   - Valida el token usando `JwtUtil`
   - Establece la autenticación en el contexto de Spring Security

4. **SecurityConfig permite o deniega** el acceso
   - Si el token es válido → ✅ Acceso permitido
   - Si el token es inválido → ❌ 401 Unauthorized

## 📝 Uso en el Frontend

### Ejemplo de petición con token

```javascript
// Después de hacer login y obtener el token
const token = localStorage.getItem('token');

// Hacer petición al inventario
fetch('http://localhost:8080/api/paquetes', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

### Ejemplo con Axios

```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Interceptor para agregar el token a todas las peticiones
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// Usar el cliente configurado
api.get('/paquetes').then(response => console.log(response.data));
```

## 🔧 Configuración Importante

### ⚠️ Secret Key

**CRÍTICO**: La `jwt.secret` debe ser **EXACTAMENTE LA MISMA** en:
- ✅ Servicio de Autenticación (login-service)
- ✅ Servicio de Inventario (inventario-service)

Si las claves son diferentes, los tokens generados por un servicio no serán válidos en el otro.

### 🔄 CORS

Los orígenes permitidos están configurados para desarrollo local:
- `http://localhost:3000` - React, Vue, etc.
- `http://localhost:4200` - Angular
- `http://localhost:8081` - Otro servicio o frontend

Para producción, actualiza estos valores en `application.properties`.

## 🧪 Probar la Configuración

### 1. Endpoint Público (sin token)
```bash
curl http://localhost:8080/api/test
```
✅ Debe funcionar sin token

### 2. Endpoint Protegido (con token)
```bash
# Sin token - debe fallar
curl http://localhost:8080/api/paquetes

# Con token - debe funcionar
curl -H "Authorization: Bearer <tu-token-jwt>" http://localhost:8080/api/paquetes
```

## 📊 Estados de Respuesta

- **200 OK** - Petición exitosa con token válido
- **401 Unauthorized** - Token inválido, expirado o no proporcionado
- **403 Forbidden** - Token válido pero sin permisos (si implementas roles)

## 🔄 Próximos Pasos (Opcional)

Si deseas agregar autorización basada en roles:

1. Modificar `JwtUtil` para incluir roles en los claims
2. Actualizar `JwtAuthenticationFilter` para extraer roles
3. Modificar `SecurityConfig` para requerir roles específicos:
```java
.requestMatchers("/api/admin/**").hasRole("ADMIN")
.requestMatchers("/api/empleado/**").hasAnyRole("ADMIN", "EMPLEADO")
```

## ✅ Compilación Exitosa

El proyecto se compiló correctamente con todas las dependencias:
```
BUILD SUCCESS
Total time: 23.506 s
```

---

**Fecha de configuración**: 31 de Octubre, 2025
**Versión Spring Boot**: 3.5.5
**Versión JWT**: 0.12.3
