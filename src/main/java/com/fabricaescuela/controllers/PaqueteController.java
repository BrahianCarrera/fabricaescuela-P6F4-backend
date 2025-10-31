package com.fabricaescuela.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.service.PaqueteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/paquetes")
@Tag(name = "Paquetes", description = "API para gestión de paquetes")
public class PaqueteController {

    private final PaqueteService paqueteService;

    public PaqueteController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    @Operation(summary = "Consultar paquete por código", 
              description = "Devuelve información detallada del paquete incluyendo estado actual, historial y novedades")
    @GetMapping("/{codigo}")
    public ResponseEntity<PaqueteResponseDto> consultarPorCodigo(@PathVariable String codigo) {
        return paqueteService.consultarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Verificar estado del controlador")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("✅ El controlador de Paquetes está activo!");
    }
}