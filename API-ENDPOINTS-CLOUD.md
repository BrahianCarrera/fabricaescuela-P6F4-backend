# Guía de Consumo de API - CourierSync (Para Equipo Cloud)

Esta guía documenta **cómo consumir los endpoints que requieren payload (body)** en la API de CourierSync.

---

## **Autenticación**

**TODOS los endpoints requieren autenticación JWT** (excepto Swagger).

### **Header requerido en todas las peticiones:**
```http
Authorization: Bearer <tu_token_jwt>
Content-Type: application/json
```

### **Ejemplo con cURL:**
```bash
curl -X GET "http://localhost:8080/api/paquetes" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json"
```

---

## **Endpoints con Payload**

### **1. HU 2.1 - Actualizar Dirección de Paquete en Ruta**

**Endpoint:**
```http
PUT /api/paquetes/en-ruta/{codigo}/direccion
```

**Descripción:**  
Permite actualizar la dirección de destino y/o destinatario cuando el paquete está en estado "En Ruta".

**Parámetros de URL:**
- `codigo` (string): Código del paquete (ej: `PKG-001`)

**Body (JSON):**
```json
{
  "destino": "Cali, Valle del Cauca",
  "destinatario": "Juan Pérez García"
}
```

**Validaciones:**
- ✅ `destino`: **OBLIGATORIO**, máximo 30 caracteres
- ✅ `destinatario`: **OPCIONAL**, máximo 70 caracteres
- ⚠️ El paquete debe estar en estado "En Ruta"

**Ejemplo completo con cURL:**
```bash
curl -X PUT "http://localhost:8080/api/paquetes/en-ruta/PKG-001/direccion" \
  -H "Authorization: Bearer tu_token_jwt_aqui" \
  -H "Content-Type: application/json" \
  -d '{
    "destino": "Cali, Valle del Cauca",
    "destinatario": "Juan Pérez García"
  }'
```

**Ejemplo con JavaScript (fetch):**
```javascript
const actualizarDireccion = async (codigo) => {
  const response = await fetch(`http://localhost:8080/api/paquetes/en-ruta/${codigo}/direccion`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      destino: "Cali, Valle del Cauca",
      destinatario: "Juan Pérez García"
    })
  });
  
  return await response.json();
};
```

**Respuestas:**
- **200 OK**: Dirección actualizada exitosamente
  ```json
  {
    "codigo": "PKG-001",
    "destino": "Cali, Valle del Cauca",
    "destinatario": "Juan Pérez García",
    "estadoActual": "En Ruta",
    ...
  }
  ```
- **400 Bad Request**: Paquete no está en estado "En Ruta"
- **404 Not Found**: Paquete no encontrado
- **401 Unauthorized**: Token inválido o no proporcionado

---

### **2. Registrar Nueva Novedad**

**Endpoint:**
```http
POST /api/novedades
```

**Descripción:**  
Registra una incidencia o evento especial relacionado con un paquete.

**Body (JSON):**
```json
{
  "idPaquete": {
    "id": 1
  },
  "tipoNovedad": "Retraso en entrega",
  "descripcion": "Demora por condiciones climáticas adversas en la ruta",
  "fechaHora": "2025-11-02"
}
```

**Validaciones:**
- ✅ `idPaquete.id`: **OBLIGATORIO** (número entero)
- ✅ `tipoNovedad`: **OBLIGATORIO**, máximo 30 caracteres
- ✅ `descripcion`: **OPCIONAL**, máximo 255 caracteres
- ✅ `fechaHora`: **OBLIGATORIO**, formato ISO `YYYY-MM-DD`

**Ejemplo con cURL:**
```bash
curl -X POST "http://localhost:8080/api/novedades" \
  -H "Authorization: Bearer tu_token_jwt_aqui" \
  -H "Content-Type: application/json" \
  -d '{
    "idPaquete": {"id": 1},
    "tipoNovedad": "Retraso en entrega",
    "descripcion": "Demora por condiciones climáticas adversas",
    "fechaHora": "2025-11-02"
  }'
```

**Ejemplo con Python (requests):**
```python
import requests

url = "http://localhost:8080/api/novedades"
headers = {
    "Authorization": f"Bearer {token}",
    "Content-Type": "application/json"
}
payload = {
    "idPaquete": {"id": 1},
    "tipoNovedad": "Retraso en entrega",
    "descripcion": "Demora por condiciones climáticas adversas",
    "fechaHora": "2025-11-02"
}

response = requests.post(url, json=payload, headers=headers)
print(response.json())
```

**Respuestas:**
- **201 Created**: Novedad registrada exitosamente
- **400 Bad Request**: Datos inválidos
- **401 Unauthorized**: Token inválido

---

### **3. Registrar Cambio de Estado**

**Endpoint:**
```http
POST /api/historial-estados
```

**Descripción:**  
Registra un cambio de estado para un paquete (ej: de "En Bodega" a "En Ruta").

**Body (JSON):**
```json
{
  "idPaquete": {
    "id": 1
  },
  "idEstado": {
    "id": 2
  },
  "idEmpleado": {
    "id": 5
  },
  "fechaHora": "2025-11-02"
}
```

**Validaciones:**
- ✅ `idPaquete.id`: **OBLIGATORIO**
- ✅ `idEstado.id`: **OBLIGATORIO**
- ✅ `idEmpleado.id`: **OBLIGATORIO**
- ✅ `fechaHora`: **OBLIGATORIO**, formato ISO `YYYY-MM-DD`

**Ejemplo con cURL:**
```bash
curl -X POST "http://localhost:8080/api/historial-estados" \
  -H "Authorization: Bearer tu_token_jwt_aqui" \
  -H "Content-Type: application/json" \
  -d '{
    "idPaquete": {"id": 1},
    "idEstado": {"id": 2},
    "idEmpleado": {"id": 5},
    "fechaHora": "2025-11-02"
  }'
```

---

### **4. Registrar Nueva Ubicación**

**Endpoint:**
```http
POST /api/paquetes/{codigoPaquete}/ubicaciones
```

**Descripción:**  
Registra la ubicación geográfica actual del paquete durante su trayecto.

**Parámetros de URL:**
- `codigoPaquete` (string): Código del paquete

**Body (JSON):**
```json
{
  "ubicacion": "Bodega Central Bogotá - Calle 100 #15-20"
}
```

**Validaciones:**
- ✅ `ubicacion`: **OBLIGATORIO**, máximo 255 caracteres

**Ejemplo con cURL:**
```bash
curl -X POST "http://localhost:8080/api/paquetes/PKG-001/ubicaciones" \
  -H "Authorization: Bearer tu_token_jwt_aqui" \
  -H "Content-Type: application/json" \
  -d '{
    "ubicacion": "Bodega Central Bogotá - Calle 100 #15-20"
  }'
```

**Ejemplo con JavaScript (axios):**
```javascript
const registrarUbicacion = async (codigoPaquete, ubicacion) => {
  try {
    const response = await axios.post(
      `http://localhost:8080/api/paquetes/${codigoPaquete}/ubicaciones`,
      { ubicacion },
      {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('Error:', error.response.data);
  }
};
```

---

### **5. Crear Nuevo Estado (CRUD)**

**Endpoint:**
```http
POST /api/estados
```

**Body (JSON):**
```json
{
  "nombreEstado": "En Ruta",
  "descripcionEstado": "El paquete está en camino hacia su destino"
}
```

**Validaciones:**
- ✅ `nombreEstado`: **OBLIGATORIO**, máximo 30 caracteres
- ✅ `descripcionEstado`: **OPCIONAL**, máximo 255 caracteres

---

## 🧪 **Probar en Swagger**

Puedes probar todos estos endpoints interactivamente en:

```
http://localhost:8080/swagger-ui.html
```

**No requiere autenticación** para acceder a la interfaz de Swagger.

---

## ❌ **Errores Comunes**

### **401 Unauthorized**
```json
{
  "error": "Unauthorized",
  "message": "Token JWT inválido o no proporcionado"
}
```
**Solución:** Verifica que incluyas el header `Authorization: Bearer <token>`.

### **400 Bad Request - Validación**
```json
{
  "destino": "El destino es obligatorio",
  "destinatario": "El destinatario no puede superar los 70 caracteres"
}
```
**Solución:** Revisa que los campos cumplan con las validaciones especificadas.

### **404 Not Found**
```json
{
  "error": "Paquete no encontrado"
}
```
**Solución:** Verifica que el código del paquete existe en la base de datos.

---

## 📞 **Contacto Backend**

**Equipo Backend:**
- Juan David Villota Cordoba
- Oswal Gutierrez

**Puerto del servicio:** `8080` (local)  
**Base URL (producción):** _[Por definir con Cloud]_

---

## 🔗 **Referencias Adicionales**

- [SEGURIDAD-JWT-README.md](./SEGURIDAD-JWT-README.md) - Configuración de seguridad y JWT
- [GUIA-FRONTEND-JWT.md](./GUIA-FRONTEND-JWT.md) - Guía de integración JWT para frontend
- [EJEMPLO-SEGURIDAD-CONTROLADOR.md](./EJEMPLO-SEGURIDAD-CONTROLADOR.md) - Ejemplos de controladores seguros

---

**Última actualización:** 2 de noviembre de 2025
