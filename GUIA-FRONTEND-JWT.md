# 🔐 Guía de Integración JWT para Frontend

## 📋 Información General

**Backend Endpoints:**
- Login Service: `http://localhost:8081`
- Inventario Service: `http://localhost:8080`

**Secret Key JWT:** (Ambos servicios usan la misma)
```
mySecretKeyForJWTTokenGenerationThatIsLongEnough123456789
```

**Expiración del Token:** 24 horas (86400000 ms)

---

## 🚀 Flujo de Autenticación

### 1. Login (Obtener Token)

**Endpoint:** `POST http://localhost:8081/api/auth/login`

**Request:**
```json
{
  "email": "admin@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "admin",
  "role": "ADMIN",
  "permisos": ["VER_PAQUETES", "CREAR_PAQUETES", "EDITAR_PAQUETES"]
}
```

### 2. Usar el Token en Peticiones al Inventario

**Todas las peticiones al Inventario Service deben incluir:**

```
Header: Authorization: Bearer {token}
```

**Ejemplo:**
```javascript
fetch('http://localhost:8080/api/paquetes', {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
```

---

## 💾 Almacenamiento del Token

### Opción 1: LocalStorage (Persistente)
```javascript
// Guardar
localStorage.setItem('token', data.token);
localStorage.setItem('username', data.username);
localStorage.setItem('role', data.role);

// Recuperar
const token = localStorage.getItem('token');

// Eliminar
localStorage.removeItem('token');
```

### Opción 2: SessionStorage (Solo sesión actual)
```javascript
sessionStorage.setItem('token', data.token);
```

---

## 🔧 Configuración con Axios (Recomendado)

### Instalación
```bash
npm install axios
```

### Configuración (`src/services/api.js`)

```javascript
import axios from 'axios';

// ========================================
// CLIENTE PARA LOGIN SERVICE
// ========================================
export const apiAuth = axios.create({
  baseURL: 'http://localhost:8081/api/auth'
});

// ========================================
// CLIENTE PARA INVENTARIO SERVICE
// ========================================
export const apiInventario = axios.create({
  baseURL: 'http://localhost:8080/api'
});

// Interceptor: Agrega el token automáticamente
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

// Interceptor: Maneja errores 401 (token inválido/expirado)
apiInventario.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      // Redirigir al login
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);
```

---

## 📱 Servicios de Autenticación (`src/services/authService.js`)

```javascript
import { apiAuth } from './api';

export const authService = {
  // Login
  async login(email, password) {
    try {
      const response = await apiAuth.post('/login', { email, password });
      
      // Guardar token y datos del usuario
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('username', response.data.username);
      localStorage.setItem('role', response.data.role);
      localStorage.setItem('permisos', JSON.stringify(response.data.permisos));
      
      return response.data;
    } catch (error) {
      throw error.response?.data || 'Error en el login';
    }
  },

  // Logout
  logout() {
    localStorage.clear();
    window.location.href = '/login';
  },

  // Verificar si está autenticado
  isAuthenticated() {
    return !!localStorage.getItem('token');
  },

  // Obtener usuario actual
  getCurrentUser() {
    return {
      username: localStorage.getItem('username'),
      role: localStorage.getItem('role'),
      permisos: JSON.parse(localStorage.getItem('permisos') || '[]')
    };
  },

  // Verificar si tiene un permiso
  hasPermission(permiso) {
    const permisos = JSON.parse(localStorage.getItem('permisos') || '[]');
    return permisos.includes(permiso);
  },

  // Verificar si tiene un rol
  hasRole(role) {
    return localStorage.getItem('role') === role;
  }
};
```

---

## 📦 Servicios de Inventario (`src/services/inventarioService.js`)

```javascript
import { apiInventario } from './api';

export const paqueteService = {
  // Obtener todos los paquetes
  async getAll() {
    const response = await apiInventario.get('/paquetes');
    return response.data;
  },

  // Obtener paquete por código
  async getByCodigo(codigo) {
    const response = await apiInventario.get(`/paquetes/${codigo}`);
    return response.data;
  },

  // Crear paquete
  async create(paqueteData) {
    const response = await apiInventario.post('/paquetes', paqueteData);
    return response.data;
  },

  // Actualizar paquete
  async update(codigo, paqueteData) {
    const response = await apiInventario.put(`/paquetes/${codigo}`, paqueteData);
    return response.data;
  },

  // Eliminar paquete
  async delete(codigo) {
    const response = await apiInventario.delete(`/paquetes/${codigo}`);
    return response.data;
  }
};

export const empleadoService = {
  async getAll() {
    const response = await apiInventario.get('/empleados');
    return response.data;
  },
  
  async getById(id) {
    const response = await apiInventario.get(`/empleados/${id}`);
    return response.data;
  },
  
  async create(empleadoData) {
    const response = await apiInventario.post('/empleados', empleadoData);
    return response.data;
  }
};

export const novedadService = {
  async getAll() {
    const response = await apiInventario.get('/novedades');
    return response.data;
  },
  
  async create(novedadData) {
    const response = await apiInventario.post('/novedades', novedadData);
    return response.data;
  }
};
```

---

## 🛡️ Componente de Ruta Protegida (React)

```javascript
import { Navigate } from 'react-router-dom';
import { authService } from '../services/authService';

export function ProtectedRoute({ children, requiredPermission, requiredRole }) {
  if (!authService.isAuthenticated()) {
    return <Navigate to="/login" />;
  }

  if (requiredRole && !authService.hasRole(requiredRole)) {
    return <Navigate to="/unauthorized" />;
  }

  if (requiredPermission && !authService.hasPermission(requiredPermission)) {
    return <Navigate to="/unauthorized" />;
  }

  return children;
}

// Uso en Router
<Route 
  path="/paquetes" 
  element={
    <ProtectedRoute requiredPermission="VER_PAQUETES">
      <PaquetesPage />
    </ProtectedRoute>
  } 
/>
```

---

## 📄 Ejemplo de Componente Completo (React)

```javascript
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../services/authService';

function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      await authService.login(email, password);
      navigate('/dashboard');
    } catch (err) {
      setError(err.message || 'Error al iniciar sesión');
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <h2>Iniciar Sesión</h2>
      
      {error && <div className="error">{error}</div>}
      
      <input
        type="email"
        placeholder="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      
      <input
        type="password"
        placeholder="Contraseña"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      
      <button type="submit" disabled={loading}>
        {loading ? 'Iniciando sesión...' : 'Ingresar'}
      </button>
    </form>
  );
}

export default LoginPage;
```

```javascript
import React, { useEffect, useState } from 'react';
import { paqueteService } from '../services/inventarioService';
import { authService } from '../services/authService';

function PaquetesPage() {
  const [paquetes, setPaquetes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadPaquetes();
  }, []);

  const loadPaquetes = async () => {
    try {
      const data = await paqueteService.getAll();
      setPaquetes(data);
    } catch (err) {
      setError('Error al cargar paquetes');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    authService.logout();
  };

  if (loading) return <div>Cargando...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div>
      <div className="header">
        <h1>Paquetes</h1>
        <button onClick={handleLogout}>Cerrar Sesión</button>
      </div>

      {authService.hasPermission('CREAR_PAQUETES') && (
        <button onClick={() => navigate('/paquetes/nuevo')}>
          Nuevo Paquete
        </button>
      )}

      <table>
        <thead>
          <tr>
            <th>Código</th>
            <th>Destinatario</th>
            <th>Estado</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {paquetes.map(paquete => (
            <tr key={paquete.codigo}>
              <td>{paquete.codigo}</td>
              <td>{paquete.destinatario}</td>
              <td>{paquete.estado}</td>
              <td>
                <button onClick={() => navigate(`/paquetes/${paquete.codigo}`)}>
                  Ver
                </button>
                
                {authService.hasPermission('EDITAR_PAQUETES') && (
                  <button onClick={() => navigate(`/paquetes/${paquete.codigo}/editar`)}>
                    Editar
                  </button>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default PaquetesPage;
```

---

## ⚠️ Manejo de Errores Comunes

### 401 Unauthorized
```javascript
// Token inválido o expirado
if (error.response?.status === 401) {
  authService.logout(); // Redirige al login
}
```

### 403 Forbidden
```javascript
// Usuario sin permisos
if (error.response?.status === 403) {
  alert('No tienes permisos para realizar esta acción');
}
```

### Network Error
```javascript
// Servidor caído
if (error.message === 'Network Error') {
  alert('No se puede conectar con el servidor');
}
```

---

## 🧪 Testing en Desarrollo

### Sin Backend (Mock)
```javascript
// Crear un mock del token para desarrollo
const mockToken = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...';
localStorage.setItem('token', mockToken);
```

### Con Backend Local
```bash
# 1. Iniciar Login Service
cd login-service
mvn spring-boot:run

# 2. Iniciar Inventario Service
cd inventario-service
mvn spring-boot:run

# 3. Iniciar Frontend
npm start
```

---

## 📊 Códigos de Estado HTTP

| Código | Significado | Acción Frontend |
|--------|-------------|-----------------|
| 200 | OK | Mostrar datos |
| 201 | Created | Redirigir/Actualizar lista |
| 400 | Bad Request | Mostrar error de validación |
| 401 | Unauthorized | Redirigir a login |
| 403 | Forbidden | Mostrar mensaje de permisos |
| 404 | Not Found | Mostrar "No encontrado" |
| 500 | Server Error | Mostrar error genérico |

---

## ✅ Checklist de Implementación

- [ ] Instalar Axios
- [ ] Crear archivo `api.js` con configuración base
- [ ] Crear `authService.js` con login/logout
- [ ] Crear servicios para cada entidad (paquetes, empleados, etc.)
- [ ] Implementar interceptor de Axios para agregar token
- [ ] Implementar interceptor para manejar 401
- [ ] Crear componente de Login
- [ ] Crear componente de Rutas Protegidas
- [ ] Implementar logout en todas las páginas
- [ ] Manejar expiración del token
- [ ] Mostrar/ocultar elementos según permisos

---

## 📞 Contacto

Para dudas sobre los endpoints del backend, contactar al equipo de Backend.

**Endpoints de Documentación:**
- Login Service: http://localhost:8081/swagger-ui/index.html
- Inventario Service: http://localhost:8080/swagger-ui/index.html
