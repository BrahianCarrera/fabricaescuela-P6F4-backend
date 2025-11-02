# 🎯 Ejemplo: Aplicar Seguridad a PaqueteController

## 📝 Guía Rápida para Proteger Controladores

### Controlador Actual (Sin Seguridad)

El `PaqueteController` actualmente no tiene restricciones de seguridad. Todos los endpoints están protegidos por JWT (requieren token), pero no hay control de roles o permisos.

### Controlador Sugerido (Con Seguridad por Roles y Permisos)

```java
package com.fabricaescuela.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fabricaescuela.models.dto.PaqueteDireccionUpdateRequest;
import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.models.dto.PaqueteRequestDto;
import com.fabricaescuela.service.PaqueteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes")
@Tag(name = "Paquetes", description = "API para gestión de paquetes")
@SecurityRequirement(name = "Bearer Authentication") // Para Swagger
public class PaqueteController {

    private final PaqueteService paqueteService;

    public PaqueteController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    /**
     * Consultar todos los paquetes
     * Permitido: ADMIN, EMPLEADO, SUPERVISOR
     */
    @Operation(summary = "Consultar todos los paquetes",
            description = "Retorna la lista de paquetes con su estado actual")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'SUPERVISOR')")
    @GetMapping
    public ResponseEntity<List<PaqueteResponseDto>> obtenerTodos() {
        return ResponseEntity.ok(paqueteService.obtenerTodos());
    }

    /**
     * Consultar paquete por código
     * Permitido: ADMIN, EMPLEADO, SUPERVISOR, CLIENTE (con permiso)
     */
    @Operation(summary = "Consultar paquete por código",
            description = "Devuelve información detallada del paquete")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO', 'SUPERVISOR') or hasAuthority('PAQUETE_CONSULTAR')")
    @GetMapping("/{codigo}")
    public ResponseEntity<PaqueteResponseDto> consultarPorCodigo(@PathVariable String codigo) {
        return paqueteService.consultarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Consultar paquete en ruta
     * Permitido: Cualquier usuario autenticado
     */
    @Operation(summary = "Consultar paquete en ruta por código",
        description = "Devuelve la información del paquete únicamente si su estado actual es en ruta")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/en-ruta/{codigo}")
    public ResponseEntity<PaqueteResponseDto> consultarEnRuta(@PathVariable String codigo) {
        return paqueteService.consultarEnRuta(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crear nuevo paquete
     * Permitido: ADMIN, EMPLEADO (con permiso específico)
     */
    @Operation(summary = "Crear nuevo paquete",
            description = "Crea un nuevo paquete en el sistema")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PAQUETE_CREAR')")
    @PostMapping
    public ResponseEntity<PaqueteResponseDto> crear(@Valid @RequestBody PaqueteRequestDto dto) {
        return ResponseEntity.ok(paqueteService.crear(dto));
    }

    /**
     * Actualizar dirección de paquete
     * Permitido: ADMIN, EMPLEADO (con permiso específico)
     */
    @Operation(summary = "Actualizar dirección de paquete",
            description = "Actualiza la dirección de entrega del paquete")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('PAQUETE_EDITAR')")
    @PutMapping("/{codigo}/direccion")
    public ResponseEntity<PaqueteResponseDto> actualizarDireccion(
            @PathVariable String codigo,
            @Valid @RequestBody PaqueteDireccionUpdateRequest request) {
        return paqueteService.actualizarDireccion(codigo, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Eliminar paquete
     * Permitido: Solo ADMIN
     */
    @Operation(summary = "Eliminar paquete",
            description = "Elimina un paquete del sistema (solo ADMIN)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{codigo}")
    public ResponseEntity<?> eliminar(@PathVariable String codigo) {
        paqueteService.eliminar(codigo);
        return ResponseEntity.ok()
                .body(Map.of("mensaje", "Paquete eliminado exitosamente"));
    }
}
```

---

## 🎨 Patrones de Autorización Recomendados

### 1. **Operaciones de Lectura (GET)**

```java
// Lectura pública para usuarios autenticados
@PreAuthorize("isAuthenticated()")
@GetMapping

// Lectura para roles específicos
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLEADO')")
@GetMapping

// Lectura con permiso específico
@PreAuthorize("hasAuthority('PAQUETE_CONSULTAR')")
@GetMapping
```

### 2. **Operaciones de Creación (POST)**

```java
// Solo ADMIN
@PreAuthorize("hasRole('ADMIN')")
@PostMapping

// ADMIN o con permiso específico
@PreAuthorize("hasRole('ADMIN') or hasAuthority('PAQUETE_CREAR')")
@PostMapping
```

### 3. **Operaciones de Actualización (PUT/PATCH)**

```java
// ADMIN o EMPLEADO con permiso
@PreAuthorize("hasRole('ADMIN') or hasAuthority('PAQUETE_EDITAR')")
@PutMapping
```

### 4. **Operaciones de Eliminación (DELETE)**

```java
// Solo ADMIN (operación crítica)
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping
```

---

## 📋 Matriz de Permisos Sugerida

| Endpoint | GET | POST | PUT | DELETE |
|----------|-----|------|-----|--------|
| **ADMIN** | ✅ | ✅ | ✅ | ✅ |
| **EMPLEADO** | ✅ | ✅ (con permiso) | ✅ (con permiso) | ❌ |
| **SUPERVISOR** | ✅ | ❌ | ✅ (solo algunos) | ❌ |
| **CLIENTE** | ✅ (solo sus paquetes) | ❌ | ❌ | ❌ |

---

## 🔧 Permisos Recomendados para Configurar en Login-Service

### Permisos de Paquetes
- `PAQUETE_CONSULTAR` - Ver información de paquetes
- `PAQUETE_CREAR` - Crear nuevos paquetes
- `PAQUETE_EDITAR` - Modificar paquetes existentes
- `PAQUETE_ELIMINAR` - Eliminar paquetes (solo ADMIN)

### Permisos de Empleados
- `EMPLEADO_CONSULTAR` - Ver información de empleados
- `EMPLEADO_GESTIONAR` - Crear, editar, eliminar empleados

### Permisos de Estados/Historial
- `ESTADO_GESTIONAR` - Modificar estados de paquetes
- `HISTORIAL_CONSULTAR` - Ver historial completo

### Permisos de Novedades
- `NOVEDAD_CREAR` - Registrar novedades
- `NOVEDAD_CONSULTAR` - Ver novedades

---

## 🚀 Cómo Aplicar los Cambios

### Paso 1: Agregar import
```java
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
```

### Paso 2: Agregar anotación a la clase
```java
@SecurityRequirement(name = "Bearer Authentication")
```

### Paso 3: Agregar @PreAuthorize a cada método
```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<?> eliminar(@PathVariable Long id) {
    // código
}
```

### Paso 4: Probar con diferentes usuarios
- Login como ADMIN → debe acceder a todo
- Login como EMPLEADO → debe acceder según permisos
- Login como CLIENTE → acceso limitado

---

## 🧪 Comandos de Prueba

### 1. Sin Token (debe fallar con 401)
```bash
curl http://localhost:8080/api/paquetes
```

### 2. Con Token de ADMIN (debe funcionar)
```bash
curl -H "Authorization: Bearer <token-admin>" \
     http://localhost:8080/api/paquetes
```

### 3. Con Token de EMPLEADO sin permisos (debe fallar con 403)
```bash
curl -X DELETE \
     -H "Authorization: Bearer <token-empleado>" \
     http://localhost:8080/api/paquetes/PKG001
```

### 4. Con Token de ADMIN (debe funcionar)
```bash
curl -X DELETE \
     -H "Authorization: Bearer <token-admin>" \
     http://localhost:8080/api/paquetes/PKG001
```

---

## 📊 Respuestas HTTP

| Código | Significado | Cuándo Ocurre |
|--------|-------------|---------------|
| 200 | OK | Token válido y permisos correctos |
| 401 | Unauthorized | Sin token o token inválido |
| 403 | Forbidden | Token válido pero sin permisos |
| 404 | Not Found | Recurso no encontrado |

---

## ✅ Checklist de Implementación

- [ ] Agregar imports necesarios
- [ ] Agregar `@SecurityRequirement` a nivel de clase
- [ ] Agregar `@PreAuthorize` a cada método
- [ ] Documentar qué roles/permisos se requieren en cada endpoint
- [ ] Configurar permisos en el login-service
- [ ] Asignar permisos a roles (ADMIN, EMPLEADO, etc.)
- [ ] Probar con diferentes usuarios y roles
- [ ] Verificar respuestas 401 y 403
- [ ] Actualizar documentación Swagger

---

**Nota:** Aplica el mismo patrón a todos los controladores del proyecto:
- `EmpleadoController`
- `EstadoController`
- `HistorialEstadoController`
- `HistorialUbicacionController`
- `NovedadController`

Cada controlador debe tener sus propias reglas de autorización basadas en la lógica de negocio.
