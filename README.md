<<<<<<< HEAD
# Fabrica Escuela – Proyecto 6 (Feature 4)

**CourierSync** es un sistema web para optimizar procesos logísticos de transporte y distribución.  

Este repositorio corresponde a la **Feature 4: Control de Inventario en Tránsito**, cuyo objetivo es monitorear los paquetes en todas las etapas del transporte, minimizando pérdidas y errores.

## 🚀 Tecnologías utilizadas
- Java 21  
- Spring Boot 3  
- Maven  
- PostgreSQL  
- Git & GitHub  

## 🔀 Estrategia de ramas
Para el manejo colaborativo del código utilizamos un flujo simplificado basado en Git Flow:

- **main** → rama estable con el código listo para entrega/demos.  
- **develop** → rama de integración donde se fusionan las historias de usuario.  
- **hu/...** → ramas individuales para cada historia de usuario.  
  - Ejemplo: `hu/actualizacion-estado-paquete`, `hu/consulta-envio-cliente`.

👉 Cada integrante debe trabajar en su propia rama `hu/...` y luego abrir un Pull Request hacia `develop`.  
👉 Al finalizar el sprint, `develop` se integra en `main`.  

## 👥 Equipo
Este módulo es desarrollado en el marco de la **Fábrica Escuela 2025-2**.  
Integrantes del equipo:  
- Juan David Villota  
- Oswald Gutierrez  
-   

---

=======
# 🚚 fabricaescuela-P6F4-2025 – CourierSync  

[![CI/CD Pipeline](https://github.com/JUAN-VILLOTA/fabricaescuela-P6F4-2025/actions/workflows/build.yml/badge.svg)](https://github.com/JUAN-VILLOTA/fabricaescuela-P6F4-2025/actions/workflows/build.yml) 

CourierSync es un sistema web para optimizar los procesos logísticos de transporte y distribución.  
Este repositorio corresponde a la **Feature 4: Control de Inventario en Tránsito** dentro del proyecto de la Fábrica Escuela.  

---

## ⚙️ Tecnologías utilizadas  
- **Java 21**  
- **Spring Boot 3.5.5** (framework principal)  
- **Maven** (gestor de dependencias y construcción)  
- **JPA/Hibernate** (persistencia de datos)  
- **PostgreSQL** (base de datos)  
- **Swagger/OpenAPI** (documentación y prueba de endpoints REST)  
- **Spring Security** (seguridad y autenticación)  
- **JWT** (JSON Web Tokens para autenticación)  

---

## 🌳 Flujo de ramas (Git Flow)  

Este proyecto se organiza siguiendo un esquema de **Git Flow**, con las siguientes ramas principales:  

- **`main`** → Rama estable. Solo contiene versiones probadas y listas para liberar.  
- **`develop`** → Rama de integración. Aquí se van uniendo las funcionalidades que ya están en desarrollo.  
- **`hu/...`** → Ramas de funcionalidad (feature branches). Cada historia de usuario (HU) se desarrolla en su propia rama.  

Ejemplo de ramas HU creadas hasta ahora:  
- `hu/actualizacion-estado-paquete`  
- `hu/consulta-envio-cliente`  

Para nuevas historias de usuario, se crea una rama con el prefijo `hu/` seguido de una descripción clara de la HU.  

---

## 📌 Estado actual del proyecto  

En esta primera fase se cuenta con:  
- Proyecto base en Spring Boot estructurado por capas (controllers, services, repository, entity).  
- Implementación inicial de historias de usuario priorizadas en el Sprint 1.  
- Exposición de endpoints mediante **Swagger**.  
- Sistema de autenticación JWT integrado con microservicio de login.  

---

## 👥 Equipo  

- Juan David Villota Cordoba
- Oswal Gutierrez
>>>>>>> 827abe2041487061ff059499cfe2e139530994ab
