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

@RestController
@RequestMapping("/api/team")
public class TeamController {

    @GetMapping("/getAllTeam")
    public ResponseEntity<List<String>> getAllTeams() {
        // Respuesta temporal
        return ResponseEntity.ok(List.of("Team A", "Team B", "Team C"));
    }

    @PostMapping("/createTeam")
    public ResponseEntity<String> createTeam(@RequestBody String teamName) {
        // Respuesta temporal
        return ResponseEntity.ok("✅ Team " + teamName + " creado con éxito (stub)");
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable int id) {
        // Respuesta temporal
        return ResponseEntity.ok("✅ Team con id " + id + " eliminado (stub)");
    }

    @PutMapping("update")
    public ResponseEntity<String> updateTeam(@RequestBody String teamName) {
        // Respuesta temporal
        return ResponseEntity.ok("✅ Team actualizado: " + teamName + " (stub)");
    }
}

