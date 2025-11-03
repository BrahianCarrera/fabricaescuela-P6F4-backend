package com.fabricaescuela.controllers;

import com.fabricaescuela.models.dto.HistorialUbicacionRequest;
import com.fabricaescuela.models.dto.HistorialUbicacionResponse;
import com.fabricaescuela.service.HistorialUbicacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/paquetes/{codigoPaquete}/ubicaciones")
@Tag(name = "Historial de Ubicaciones", description = "API para registrar y consultar ubicaciones de paquetes")
public class HistorialUbicacionController {

    private final HistorialUbicacionService historialUbicacionService;

    public HistorialUbicacionController(HistorialUbicacionService historialUbicacionService) {
        this.historialUbicacionService = historialUbicacionService;
    }

    @Operation(
        summary = "Registrar una nueva ubicación",
        description = """
            Registra una nueva ubicación geográfica para el paquete en su ruta de entrega.
            
            **Parámetros de ruta:**
            - codigoPaquete (string): Código único del paquete
            
            **Campos del body:**
            - ubicacion (string): Descripción de la ubicación actual (max 255 caracteres)
            
            **Ejemplo de payload (JSON):**
            ```json
            {
              "ubicacion": "Bodega Central Bogotá - Calle 100 #15-20"
            }
            ```
            
            **Respuestas:**
            - 200: Ubicación registrada exitosamente
            - 400: Datos inválidos
            - 404: Paquete no encontrado
            - 401: No autenticado
            """
    )
    @PostMapping
    public ResponseEntity<HistorialUbicacionResponse> registrarUbicacion(@PathVariable String codigoPaquete,
                                                                         @Valid @RequestBody HistorialUbicacionRequest request) {
        return ResponseEntity.ok(historialUbicacionService.registrarUbicacion(codigoPaquete, request));
    }

    @Operation(summary = "Consultar historial de ubicaciones",
            description = "Retorna la lista de ubicaciones del paquete ordenada de la más reciente a la más antigua")
    @GetMapping
    public ResponseEntity<List<HistorialUbicacionResponse>> consultarHistorial(@PathVariable String codigoPaquete) {
        return ResponseEntity.ok(historialUbicacionService.consultarUbicacionesPorCodigo(codigoPaquete));
    }

    @Operation(summary = "Obtener última ubicación registrada",
            description = "Devuelve la ubicación más reciente registrada para el paquete")
    @GetMapping("/ultima")
    public ResponseEntity<HistorialUbicacionResponse> obtenerUltima(@PathVariable String codigoPaquete) {
        return historialUbicacionService.obtenerUltimaUbicacion(codigoPaquete)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
