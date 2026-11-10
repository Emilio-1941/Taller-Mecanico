package com.taller.auth.clientes.controller;
import com.taller.auth.clientes.service.VehiculoListadoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
// Solo ADMINISTRADOR y RECEPCIONISTA (lo valida ClienteRoleInterceptor). Siempre filtra por el taller del token.
@RestController @RequestMapping("/api/vehiculos") @CrossOrigin(origins={"http://localhost:5173","http://127.0.0.1:5173","https://easygoing-integrity-production-8671.up.railway.app"})
public class VehiculoListaController {
 private final VehiculoListadoService service; public VehiculoListaController(VehiculoListadoService s){service=s;}
 @GetMapping public VehiculoListadoService.Pagina listar(@RequestParam(name="pagina",defaultValue="0") int pagina,@RequestParam(name="orden",defaultValue="asc") String orden,@RequestParam(name="ordenar_por",defaultValue="cliente") String ordenarPor,HttpServletRequest req){
  Object t=req.getAttribute("taller_id");
  if(!Set.of("asc","desc").contains(orden.toLowerCase(Locale.ROOT)))throw new IllegalArgumentException("Orden debe ser asc o desc.");
  return service.listar(t==null?null:(Long)t,pagina,ordenarPor,orden.equalsIgnoreCase("asc"));
 }
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<?> invalid(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("mensaje",e.getMessage()));}
}
