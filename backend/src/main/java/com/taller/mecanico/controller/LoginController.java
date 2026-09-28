package com.taller.mecanico.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class LoginController {
    private static final String URL = "jdbc:mysql://localhost:3306/mecanico?serverTimezone=UTC";
    private static final String DB_USER = "mecanico";
    private static final String DB_PASSWORD = "mecanico";
    private static final Set<String> ROLES = Set.of("Administrador", "Gerente", "Cliente");

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        if (request == null || blank(request.usuario()) || blank(request.contrasena())
                || request.rol() == null || !ROLES.contains(request.rol())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Datos incompletos o rol invalido."));
        }

        String sql = "SELECT id, usuario, rol FROM usuarios WHERE usuario = ? AND contrasena = ? AND rol = ?";
        try (var connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, request.usuario().trim());
            statement.setString(2, request.contrasena());
            statement.setString(3, request.rol());
            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    return ResponseEntity.ok(Map.of("ok", true, "mensaje", "Inicio de sesion exitoso.",
                            "id", result.getLong("id"), "usuario", result.getString("usuario"),
                            "rol", result.getString("rol")));
                }
            }
            return ResponseEntity.status(401).body(Map.of("ok", false, "mensaje", "Usuario, contrasena o rol incorrectos."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("ok", false, "mensaje", "No se pudo conectar con la base de datos."));
        }
    }

    @PostMapping("/registro")
    public ResponseEntity<Map<String, Object>> registro(@RequestBody LoginRequest request) {
        if (request == null || blank(request.usuario()) || blank(request.contrasena())
                || request.rol() == null || !ROLES.contains(request.rol())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Revisa los datos y el rol."));
        }
        try (var connection = DriverManager.getConnection(URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO usuarios (usuario, contrasena, rol) VALUES (?, ?, ?)")) {
            statement.setString(1, request.usuario().trim());
            statement.setString(2, request.contrasena());
            statement.setString(3, request.rol());
            statement.executeUpdate();
            return ResponseEntity.ok(Map.of("ok", true, "mensaje", "Cuenta creada. Ya puedes iniciar sesion."));
        } catch (Exception e) {
            return ResponseEntity.status(409).body(Map.of("ok", false, "mensaje", "No se pudo crear la cuenta; verifica que el usuario no exista."));
        }
    }

    @PostMapping("/recuperar")
    public ResponseEntity<Map<String, Object>> recuperar(@RequestBody RecoveryRequest request) {
        if (request == null || blank(request.usuario())) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "mensaje", "Ingresa tu usuario."));
        }
        return ResponseEntity.ok(Map.of("ok", true,
                "mensaje", "Solicita al administrador del taller que restablezca tu contrasena."));
    }

    private static boolean blank(String value) {
        return value == null || value.isBlank();
    }

    public record LoginRequest(String usuario, String contrasena, String rol) {}
    public record RecoveryRequest(String usuario) {}
}
