package com.fabricaescuela.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricaescuela.models.entity.Novedad;
import com.fabricaescuela.service.NovedadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/novedades")
@Tag(name = "Novedades", description = "API para gestión de novedades de paquetes")
public class NovedadController {

    private final NovedadService novedadService;

    public NovedadController(NovedadService novedadService) {
        this.novedadService = novedadService;
    }

    @Operation(summary = "Obtener novedades por ID de paquete")
    @GetMapping("/paquete/{idPaquete}")
    public ResponseEntity<List<Novedad>> obtenerNovedadesPorPaquete(@PathVariable Integer idPaquete) {
        return ResponseEntity.ok(novedadService.findByIdPaqueteId(idPaquete));
    }
}