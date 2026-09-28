package com.taller.auth;

import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class LoginController {
    private static final Set<String> ROLES = Set.of("ADMINISTRADOR", "GERENTE", "CLIENTE");
    private final JdbcTemplate jdbc;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public LoginController(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        if (request == null || blank(request.usuario()) || blank(request.contrasena())
                || request.rol() == null || !ROLES.contains(normalize(request.rol()))) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Datos incompletos o rol invalido."));
        }
        try {
            var users = jdbc.query("SELECT id, nombre, rol, password_hash FROM usuarios WHERE nombre = ? OR email = ? LIMIT 1",
                    (rs, row) -> new User(rs.getLong("id"), rs.getString("nombre"), rs.getString("rol"), rs.getString("password_hash")),
                    request.usuario().trim(), request.usuario().trim());
            if (users.isEmpty()) return rejected();
            User user = users.get(0);
            if (!user.rol().equalsIgnoreCase(normalize(request.rol())) || !encoder.matches(request.contrasena(), user.hash())) {
                return rejected();
            }
            return ResponseEntity.ok(Map.of("ok", true, "mensaje", "Inicio de sesion exitoso.",
                    "id", user.id(), "usuario", user.nombre(), "rol", user.rol()));
        } catch (DataAccessException e) {
            return ResponseEntity.internalServerError().body(Map.of("ok", false, "mensaje", "No se pudo consultar la base de datos."));
        }
    }

    private static ResponseEntity<Map<String, Object>> rejected() {
        return ResponseEntity.status(401).body(Map.of("ok", false, "mensaje", "Usuario, contrasena o rol incorrectos."));
    }
    private static String normalize(String value) { return value.trim().toUpperCase(Locale.ROOT); }
    private static boolean blank(String value) { return value == null || value.isBlank(); }

    private record User(long id, String nombre, String rol, String hash) {}
    public record LoginRequest(String usuario, String contrasena, String rol) {}
}
