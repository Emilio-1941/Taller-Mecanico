package com.taller.auth.clientes.controller;
import com.taller.auth.clientes.dto.*;
import com.taller.auth.clientes.service.ClienteService;
import com.taller.auth.clientes.service.VehiculoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;
// Solo ADMINISTRADOR y RECEPCIONISTA: lo valida ClienteRoleInterceptor (rutas /api/vehiculos).
@RestController @RequestMapping("/api/vehiculos")
public class VehiculoController {
 private final VehiculoService service; public VehiculoController(VehiculoService s){service=s;}
 @PostMapping public ResponseEntity<?> create(@Valid @RequestBody VehiculoForm f,HttpServletRequest req){Object t=req.getAttribute("taller_id");return ResponseEntity.status(201).body(Map.of("ok",true,"mensaje","Vehiculo registrado correctamente.","vehiculo",service.create(f,t==null?null:(Long)t)));}
 @ExceptionHandler(ClienteService.Duplicate.class) public ResponseEntity<?> conflicto(RuntimeException e){return ResponseEntity.status(409).body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(ClienteService.OtroTaller.class) public ResponseEntity<?> otroTaller(RuntimeException e){return ResponseEntity.status(403).body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<?> constraint(){return ResponseEntity.status(409).body(Map.of("mensaje","VIN o placa duplicados."));}
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<?> invalid(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<?> invalidForm(){return ResponseEntity.badRequest().body(Map.of("mensaje","Revisa los campos del vehiculo."));}
 @ExceptionHandler(NoSuchElementException.class) public ResponseEntity<?> notFound(RuntimeException e){return ResponseEntity.status(404).body(Map.of("mensaje",e.getMessage()));}
}
