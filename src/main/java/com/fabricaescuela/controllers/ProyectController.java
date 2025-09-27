package com.fabricaescuela.controllers;

import com.fabricaescuela.models.dto.PaqueteResponseDto;
import com.fabricaescuela.service.PaqueteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/paquetes")
public class ProyectController {

    private final PaqueteService paqueteService;

    // Inyección por constructor
    public ProyectController(PaqueteService paqueteService) {
        this.paqueteService = paqueteService;
    }

    /**
     * GET /api/paquetes/{codigo}
     * Endpoint de consulta pública (cliente): devuelve información real del envío,
     * incluyendo estado actual, historial, novedades y ubicaciones.
     */
    @GetMapping("/{codigo}")
    public ResponseEntity<PaqueteResponseDto> consultarPorCodigo(@PathVariable String codigo) {
        Optional<PaqueteResponseDto> paqueteOpt = paqueteService.consultarPorCodigo(codigo);

        return paqueteOpt
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/paquetes/ping
     * Endpoint de prueba para confirmar que el controlador funciona.
     */
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("✅ El controlador de Paquetes está activo!");
    }
}
