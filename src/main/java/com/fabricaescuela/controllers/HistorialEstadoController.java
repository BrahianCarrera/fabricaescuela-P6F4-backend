package com.fabricaescuela.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricaescuela.models.entity.HistorialEstado;
import com.fabricaescuela.service.HistorialEstadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/historial-estados")
@Tag(name = "Historial de Estados", description = "API para gestión de historiales de estados de los paquetes")
public class HistorialEstadoController {

    private final HistorialEstadoService historialEstadoService;

    public HistorialEstadoController(HistorialEstadoService historialEstadoService) {
        this.historialEstadoService = historialEstadoService;
    }

    @Operation(summary = "Obtener todos los historiales de estados")
    @GetMapping
    public ResponseEntity<List<HistorialEstado>> obtenerTodos() {
        return ResponseEntity.ok(historialEstadoService.findAll());
    }

    @Operation(summary = "Obtener historial por ID")
    @GetMapping("/{id}")
    public ResponseEntity<HistorialEstado> obtenerPorId(@PathVariable Integer id) {
        return historialEstadoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Obtener historiales por paquete")
    @GetMapping("/paquete/{idPaquete}")
    public ResponseEntity<List<HistorialEstado>> obtenerPorIdPaquete(@PathVariable Integer idPaquete) {
        return ResponseEntity.ok(historialEstadoService.findByIdPaquete(idPaquete));
    }

    @Operation(summary = "Crear un nuevo historial de estado")
    @PostMapping
    public ResponseEntity<HistorialEstado> crear(@RequestBody HistorialEstado historialEstado) {
        return ResponseEntity.ok(historialEstadoService.save(historialEstado));
    }

    @Operation(summary = "Actualizar un historial existente")
    @PutMapping("/{id}")
    public ResponseEntity<HistorialEstado> actualizar(@PathVariable Integer id, @RequestBody HistorialEstado historialEstado) {
        return historialEstadoService.findById(id)
                .map(historialExistente -> {
                    historialEstado.setId(id);
                    return ResponseEntity.ok(historialEstadoService.save(historialEstado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Eliminar historial")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        return historialEstadoService.findById(id)
                .map(historial -> {
                    historialEstadoService.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
