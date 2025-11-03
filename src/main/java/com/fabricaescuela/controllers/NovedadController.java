package com.fabricaescuela.controllers;

import java.util.List;

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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/novedades")
@Tag(name = "Novedades", description = "API para gestión de novedades de paquetes")
public class NovedadController {

    private final NovedadService novedadService;

    public NovedadController(NovedadService novedadService) {
        this.novedadService = novedadService;
    }

    @Operation(
        summary = "Registrar nueva novedad", 
        description = """
            Crea una nueva novedad asociada a un paquete en el sistema.
            
            **Campos requeridos:**
            - idPaquete (objeto): {id: numero_entero}
            - tipoNovedad (string): Tipo de incidencia (max 30 caracteres)
            - descripcion (string): Descripción detallada (max 255 caracteres)
            - fechaHora (date): Fecha en formato ISO (YYYY-MM-DD)
            
            **Ejemplo de payload (JSON):**
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
            
            **Respuestas:**
            - 201: Novedad creada exitosamente
            - 400: Datos inválidos
            - 401: No autenticado
            - 500: Error interno del servidor
            """
    )
    @PostMapping
    public ResponseEntity<Novedad> registrarNovedad(@Valid @RequestBody Novedad novedad) {
        try {
            Novedad nuevaNovedad = novedadService.registrarNovedad(novedad);
            return new ResponseEntity<>(nuevaNovedad, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener todas las novedades", description = "Retorna el listado completo de novedades")
    @GetMapping
    public ResponseEntity<List<Novedad>> obtenerTodasLasNovedades() {
        try {
            List<Novedad> novedades = novedadService.obtenerTodasLasNovedades();
            if (novedades.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(novedades, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Obtener novedad por ID", description = "Retorna una novedad específica por su identificador")
    @GetMapping("/{id}")
    public ResponseEntity<Novedad> obtenerNovedadPorId(@PathVariable("id") Integer id) {
        return novedadService.obtenerNovedadPorId(id)
            .map(novedad -> new ResponseEntity<>(novedad, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Obtener novedades por ID de paquete", description = "Retorna todas las novedades asociadas a un paquete")
    @GetMapping("/paquete/{idPaquete}")
    public ResponseEntity<List<Novedad>> obtenerNovedadesPorPaquete(@PathVariable Integer idPaquete) {
        try {
            List<Novedad> novedades = novedadService.findByIdPaqueteId(idPaquete);
            if (novedades.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(novedades, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Actualizar novedad", description = "Actualiza los datos de una novedad existente")
    @PutMapping("/{id}")
    public ResponseEntity<Novedad> actualizarNovedad(@PathVariable("id") Integer id, @Valid @RequestBody Novedad novedad) {
        try {
            Novedad novedadActualizada = novedadService.actualizarNovedad(id, novedad);
            if (novedadActualizada != null) {
                return new ResponseEntity<>(novedadActualizada, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "Eliminar novedad", description = "Elimina una novedad por su ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> eliminarNovedad(@PathVariable("id") Integer id) {
        try {
            novedadService.eliminarNovedad(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}