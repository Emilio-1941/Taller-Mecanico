package com.taller.auth.codigopostal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/codigos-postales")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class CodigoPostalController {
    private final JdbcTemplate jdbc;

    public CodigoPostalController(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @GetMapping("/{cp}")
    public ResponseEntity<?> buscar(@PathVariable("cp") String cp) {
        if (!cp.matches("[0-9]{5}")) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El código postal debe tener 5 dígitos."));
        }

        List<RegistroPostal> registros = jdbc.query(
            "SELECT colonia, municipio, estado FROM codigos_postales WHERE codigo_postal = ? ORDER BY colonia",
            (rs, row) -> new RegistroPostal(rs.getString("colonia"), rs.getString("municipio"), rs.getString("estado")),
            cp
        );
        if (registros.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Código postal no encontrado."));
        }

        RegistroPostal primero = registros.get(0);
        List<String> colonias = registros.stream().map(RegistroPostal::colonia)
            .filter(Objects::nonNull).map(String::trim).filter(nombre -> !nombre.isEmpty()).distinct().toList();
        return ResponseEntity.ok(new CodigoPostalResponse(cp, primero.estado(), primero.municipio(), colonias));
    }

    private record RegistroPostal(String colonia, String municipio, String estado) {}
    public record CodigoPostalResponse(String codigoPostal, String estado, String municipio, List<String> colonias) {}
}
