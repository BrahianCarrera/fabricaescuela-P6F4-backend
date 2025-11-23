package com.fabricaescuela.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricaescuela.models.entity.Novedad;
import com.fabricaescuela.service.NovedadService;
import com.fabricaescuela.service.PaqueteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/novedades")
@Tag(name = "Novedades", description = "API para gestión de novedades de paquetes")
public class NovedadController {

    private final NovedadService novedadService;
    private final PaqueteService paqueteService;

    public NovedadController(NovedadService novedadService, PaqueteService paqueteService) {
        this.novedadService = novedadService;
        this.paqueteService = paqueteService;
    }

    @Operation(
        summary = "Registrar nueva novedad", 
        description = """
            Crea una nueva novedad asociada a un paquete en tránsito.
            
            **⚠️ VALIDACIONES AUTOMÁTICAS:**
            1. El paquete debe existir en el sistema
            2. El paquete debe estar en estado EN_TRANSITO
            3. Todos los campos son obligatorios y deben cumplir con las longitudes máximas
            
            **Campos requeridos:**
            - idPaquete (objeto): {id: numero_entero} - ID del paquete
            - tipoNovedad (string): Tipo de incidencia (max 30 caracteres)
            - descripcion (string): Descripción detallada (max 255 caracteres)
            - fechaHora (date): Fecha en formato ISO (YYYY-MM-DD)
            
            **Ejemplo de payload correcto:**
            ```json
            {
              "idPaquete": {
                "id": 1
              },
              "tipoNovedad": "Retraso en entrega",
              "descripcion": "Demora por condiciones climáticas adversas en la ruta",
              "fechaHora": "2025-11-17"
            }
            ```
            
            **Respuestas:**
            - 201 CREATED: Novedad registrada exitosamente
            - 400 BAD REQUEST: Paquete no está en tránsito o datos inválidos
            - 404 NOT FOUND: Paquete no existe
            - 500 INTERNAL SERVER ERROR: Error del servidor
            """
    )
    @PostMapping
    public ResponseEntity<?> registrarNovedad(@Valid @RequestBody Novedad novedad) {
        try {
            // Obtener el ID del paquete
            Integer idPaquete = novedad.getIdPaquete().getId();
            
            // ✅ VALIDACIÓN 1: Verificar que el paquete existe
            if (!paqueteService.findById(idPaquete).isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Paquete no encontrado");
                error.put("detalle", "No existe un paquete con ID: " + idPaquete);
                error.put("sugerencia", "Verifique el ID del paquete. Puede buscar paquetes en /api/paquetes/buscar");
                error.put("idPaquete", idPaquete);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            // ✅ VALIDACIÓN 2: Verificar que el paquete está en tránsito
            if (!paqueteService.isPaqueteEnTransito(idPaquete)) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "El paquete no está en tránsito");
                error.put("detalle", "Solo se pueden registrar novedades para paquetes en estado EN_TRANSITO");
                error.put("idPaquete", idPaquete);
                error.put("sugerencia", "Consulte el estado actual del paquete en /api/paquetes/" + idPaquete);
                error.put("ayuda", "Para ver todos los paquetes en tránsito: GET /api/paquetes/en-transito");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            
            // ✅ Si pasa las validaciones, registrar la novedad
            Novedad nuevaNovedad = novedadService.registrarNovedad(novedad);
            
            // ⭐ MENSAJE DE CONFIRMACIÓN EXITOSA ⭐
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "✅ Novedad registrada exitosamente");
            response.put("idNovedad", nuevaNovedad.getId());
            response.put("idPaquete", idPaquete);
            response.put("tipoNovedad", nuevaNovedad.getTipoNovedad());
            response.put("fechaHora", nuevaNovedad.getFechaHora());
            response.put("novedad", nuevaNovedad);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Datos inválidos");
            error.put("detalle", e.getMessage());
            return ResponseEntity.badRequest().body(error);
            
        } catch (NullPointerException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Datos incompletos");
            error.put("detalle", "El campo idPaquete es obligatorio y debe contener {id: numero}");
            return ResponseEntity.badRequest().body(error);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al registrar la novedad");
            error.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Obtener todas las novedades", description = "Retorna el listado completo de novedades")
    @GetMapping
    public ResponseEntity<?> obtenerTodasLasNovedades() {
        try {
            List<Novedad> novedades = novedadService.obtenerTodasLasNovedades();
            
            if (novedades.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "No hay novedades registradas");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Novedades encontradas");
            response.put("cantidad", novedades.size());
            response.put("novedades", novedades);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener novedades");
            error.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Obtener novedad por ID", description = "Retorna una novedad específica por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerNovedadPorId(@PathVariable("id") Integer id) {
        Optional<Novedad> novedadOpt = novedadService.obtenerNovedadPorId(id);
        
        if (novedadOpt.isPresent()) {
            return ResponseEntity.ok(novedadOpt.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Novedad no encontrada");
            error.put("id", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(
        summary = "Obtener novedades por ID de paquete", 
        description = "Retorna todas las novedades asociadas a un paquete específico (historial de incidencias)"
    )
    @GetMapping("/paquete/{idPaquete}")
    public ResponseEntity<?> obtenerNovedadesPorPaquete(@PathVariable Integer idPaquete) {
        try {
            // Verificar que el paquete existe
            if (!paqueteService.findById(idPaquete).isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Paquete no encontrado");
                error.put("detalle", "No existe un paquete con ID: " + idPaquete);
                error.put("idPaquete", idPaquete);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            List<Novedad> novedades = novedadService.findByIdPaqueteId(idPaquete);
            
            if (novedades.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "El paquete no tiene novedades registradas");
                response.put("idPaquete", idPaquete);
                response.put("info", "Este paquete no ha tenido incidencias hasta el momento");
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Historial de novedades del paquete");
            response.put("idPaquete", idPaquete);
            response.put("cantidadNovedades", novedades.size());
            response.put("novedades", novedades);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener novedades del paquete");
            error.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Actualizar novedad", description = "Actualiza los datos de una novedad existente")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarNovedad(
        @PathVariable("id") Integer id, 
        @Valid @RequestBody Novedad novedad
    ) {
        try {
            Novedad novedadActualizada = novedadService.actualizarNovedad(id, novedad);
            
            if (novedadActualizada != null) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "Novedad actualizada exitosamente");
                response.put("novedad", novedadActualizada);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Novedad no encontrada");
                error.put("id", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al actualizar novedad");
            error.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Eliminar novedad", description = "Elimina una novedad por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarNovedad(@PathVariable("id") Integer id) {
        try {
            if (!novedadService.obtenerNovedadPorId(id).isPresent()) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Novedad no encontrada");
                error.put("id", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            
            novedadService.eliminarNovedad(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Novedad eliminada exitosamente");
            response.put("id", id);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al eliminar novedad");
            error.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}