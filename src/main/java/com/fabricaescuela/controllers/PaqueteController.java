package com.fabricaescuela.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fabricaescuela.models.dto.PaqueteDireccionUpdateRequest;
import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.models.entity.Paquete;
import com.fabricaescuela.service.PaqueteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/paquetes")
@Tag(name = "Paquetes", description = "API para gestión de paquetes")
public class PaqueteController {

    private final PaqueteService paqueteService;

    public PaqueteController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    @Operation(summary = "Consultar todos los paquetes",
            description = "Retorna la lista de paquetes con su estado actual")
    @GetMapping
    public ResponseEntity<List<PaqueteResponseDto>> obtenerTodos() {
        return ResponseEntity.ok(paqueteService.obtenerTodos());
    }

    @Operation(summary = "Consultar paquete por código",
            description = "Devuelve información detallada del paquete incluyendo estado actual, historial y novedades")
    @GetMapping("/{codigo}")
    public ResponseEntity<?> consultarPorCodigo(@PathVariable String codigo) {
        Optional<PaqueteResponseDto> paqueteOpt = paqueteService.consultarPorCodigo(codigo);
        
        if (paqueteOpt.isPresent()) {
            return ResponseEntity.ok(paqueteOpt.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Paquete no encontrado");
            error.put("detalle", "No existe un paquete con el código: " + codigo);
            error.put("codigo", codigo);
            error.put("sugerencia", "Verifique el código del paquete e intente nuevamente");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(summary = "Consultar paquete en ruta por código",
        description = "Devuelve la información del paquete únicamente si su estado actual es en ruta")
    @GetMapping("/en-ruta/{codigo}")
    public ResponseEntity<?> consultarEnRuta(@PathVariable String codigo) {
        Optional<PaqueteResponseDto> paqueteOpt = paqueteService.consultarEnRutaPorCodigo(codigo);
        
        if (paqueteOpt.isPresent()) {
            return ResponseEntity.ok(paqueteOpt.get());
        } else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Paquete no encontrado o no está en ruta");
            error.put("detalle", "El paquete con código '" + codigo + "' no existe o no está en estado EN_RUTA");
            error.put("codigo", codigo);
            error.put("sugerencia", "Verifique que el paquete exista y esté en estado 'En Ruta'");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @Operation(
        summary = "Buscar paquetes por múltiples criterios",
        description = """
            Permite buscar paquetes usando diferentes criterios de búsqueda.
            Todos los parámetros son opcionales y se pueden combinar.
            
            **Ejemplos de uso:**
            - Buscar por código: `/api/paquetes/buscar?codigoPaquete=PKG-12345`
            - Buscar por fecha: `/api/paquetes/buscar?fechaRegistro=2025-11-17`
            - Buscar en tránsito: `/api/paquetes/buscar?nombreEstado=EN_TRANSITO`
            - Combinar criterios: `/api/paquetes/buscar?fechaRegistro=2025-11-17&nombreEstado=EN_TRANSITO`
            
            **Estados válidos:**
            - REGISTRADO
            - EN_TRANSITO
            - EN_BODEGA
            - EN_REPARTO
            - ENTREGADO
            - CANCELADO
            - DEVUELTO
            
            **Respuestas:**
            - 200: Lista de paquetes encontrados
            - 204: No se encontraron paquetes
            - 400: Parámetros inválidos
            - 401: No autenticado
            """
    )
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPaquetes(
        @Parameter(description = "Código del paquete (número de guía)", example = "PKG-2025-001234")
        @RequestParam(required = false) String codigoPaquete,
        
        @Parameter(description = "Fecha de registro en formato YYYY-MM-DD", example = "2025-11-17")
        @RequestParam(required = false) 
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaRegistro,
        
        @Parameter(description = "Estado del paquete", example = "EN_TRANSITO")
        @RequestParam(required = false) String nombreEstado
    ) {
        try {
            // Validar que se proporcione al menos un criterio
            if (codigoPaquete == null && fechaRegistro == null && nombreEstado == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Debe proporcionar al menos un criterio de búsqueda");
                error.put("parametrosDisponibles", List.of("codigoPaquete", "fechaRegistro", "nombreEstado"));
                error.put("ejemplo", "/api/paquetes/buscar?fechaRegistro=2025-11-17");
                return ResponseEntity.badRequest().body(error);
            }
            
            List<Paquete> paquetes = paqueteService.buscarPorCriterios(
                codigoPaquete, 
                fechaRegistro, 
                nombreEstado
            );
            
            if (paquetes.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "No se encontraron paquetes con los criterios especificados");
                response.put("criteriosBusqueda", Map.of(
                    "codigoPaquete", codigoPaquete != null ? codigoPaquete : "No especificado",
                    "fechaRegistro", fechaRegistro != null ? fechaRegistro.toString() : "No especificado",
                    "nombreEstado", nombreEstado != null ? nombreEstado : "No especificado"
                ));
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
            }
            
            // Convertir entidades a DTOs
            List<PaqueteResponseDto> paquetesDto = paquetes.stream()
                .map(p -> PaqueteResponseDto.builder()
                    .id(p.getId())
                    .codigoPaquete(p.getCodigoPaquete())
                    .remitente(p.getRemitente())
                    .destinatario(p.getDestinatario())
                    .destino(p.getDestino())
                    .estadoActual(p.getIdEstadoActual() != null ? p.getIdEstadoActual().getNombreEstado() : null)
                    .build())
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Paquetes encontrados");
            response.put("cantidad", paquetesDto.size());
            response.put("paquetes", paquetesDto);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al buscar paquetes");
            error.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(
        summary = "Obtener paquetes en tránsito",
        description = """
            Retorna todos los paquetes que están actualmente en estado EN_TRANSITO.
            Este endpoint es útil para ver qué paquetes están activos y en movimiento.
            
            **Estados considerados "en tránsito":**
            - EN_TRANSITO
            
            **Respuestas:**
            - 200: Lista de paquetes en tránsito
            - 204: No hay paquetes en tránsito actualmente
            - 401: No autenticado
            """
    )
    @GetMapping("/en-transito")
    public ResponseEntity<?> obtenerPaquetesEnTransito() {
        try {
            List<Paquete> paquetes = paqueteService.findPaquetesEnTransito();
            
            if (paquetes.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("mensaje", "No hay paquetes en tránsito en este momento");
                response.put("cantidad", 0);
                response.put("paquetes", List.of());
                return ResponseEntity.ok(response);
            }
            
            // Convertir entidades a DTOs
            List<PaqueteResponseDto> paquetesDto = paquetes.stream()
                .map(p -> PaqueteResponseDto.builder()
                    .id(p.getId())
                    .codigoPaquete(p.getCodigoPaquete())
                    .remitente(p.getRemitente())
                    .destinatario(p.getDestinatario())
                    .destino(p.getDestino())
                    .estadoActual(p.getIdEstadoActual() != null ? p.getIdEstadoActual().getNombreEstado() : null)
                    .build())
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("mensaje", "Paquetes en tránsito encontrados");
            response.put("cantidad", paquetesDto.size());
            response.put("paquetes", paquetesDto);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener paquetes en tránsito");
            error.put("detalle", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "Actualizar dirección de destino de un paquete",
            description = "Permite modificar la dirección de destino de un paquete existente")
    @PutMapping("/{codigo}/direccion")
    public ResponseEntity<PaqueteResponseDto> actualizarDireccionDestino(
            @PathVariable String codigo,
            @Valid @RequestBody PaqueteDireccionUpdateRequest request) {
        return ResponseEntity.ok(paqueteService.actualizarDireccion(codigo, request));
    }

    @Operation(summary = "Ping",
            description = "Endpoint de prueba para verificar que el servicio está activo")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}