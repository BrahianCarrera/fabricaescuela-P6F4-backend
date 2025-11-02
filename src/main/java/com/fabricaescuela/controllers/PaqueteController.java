package com.fabricaescuela.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fabricaescuela.models.dto.PaqueteDireccionUpdateRequest;
import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.service.PaqueteService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import java.util.List;

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
    public ResponseEntity<PaqueteResponseDto> consultarPorCodigo(@PathVariable String codigo) {
        return paqueteService.consultarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Consultar paquete en ruta por código",
        description = "Devuelve la información del paquete únicamente si su estado actual es en ruta")
    @GetMapping("/en-ruta/{codigo}")
    public ResponseEntity<PaqueteResponseDto> consultarEnRuta(@PathVariable String codigo) {
    return paqueteService.consultarEnRutaPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Actualizar dirección de un paquete en ruta",
        description = "Permite modificar los campos de dirección cuando el paquete está en ruta")
    @PutMapping("/en-ruta/{codigo}/direccion")
    public ResponseEntity<PaqueteResponseDto> actualizarDireccion(
            @PathVariable String codigo,
            @Valid @RequestBody PaqueteDireccionUpdateRequest request) {
        return ResponseEntity.ok(paqueteService.actualizarDireccion(codigo, request));
    }

    @Operation(summary = "Verificar estado del controlador")
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("✅ El controlador de Paquetes está activo!");
    }
}