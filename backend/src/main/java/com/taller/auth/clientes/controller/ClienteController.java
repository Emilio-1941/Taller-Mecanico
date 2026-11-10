package com.taller.auth.clientes.controller;
import com.taller.auth.clientes.dto.*;
import com.taller.auth.clientes.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.data.domain.Page;
import java.util.*;
@RestController @RequestMapping("/api/clientes") @CrossOrigin(origins={"http://localhost:5173","http://127.0.0.1:5173"})
public class ClienteController {
 private final ClienteService service; public ClienteController(ClienteService s){service=s;}
 private Long tallerDelToken(HttpServletRequest req){Object v=req.getAttribute("taller_id");return v==null?null:(Long)v;}
 @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<?> create(@Valid @ModelAttribute ClienteForm f,HttpServletRequest req){Long t=tallerDelToken(req);if(t==null)return ResponseEntity.status(403).body(Map.of("mensaje","Tu usuario no tiene taller asignado."));f.setTallerId(t);return ResponseEntity.status(201).body(Map.of("ok",true,"mensaje","Cliente registrado correctamente.","cliente",service.create(f)));}
 @GetMapping("/{id}/foto") public ResponseEntity<byte[]> photo(@PathVariable Long id,HttpServletRequest req){service.verificarMismoTaller(id,tallerDelToken(req));var foto=service.photo(id);return ResponseEntity.ok().contentType(MediaType.parseMediaType(foto.contentType())).header(HttpHeaders.CONTENT_DISPOSITION,"inline").header(HttpHeaders.CACHE_CONTROL,"private, max-age=3600").body(foto.data());}
 @GetMapping public Page<ClienteResponse> list(@RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="asc") String orden,@RequestParam(name="ordenar_por",defaultValue="nombre_completo") String ordenarPor,@RequestParam(name="incluir_suspendidos",defaultValue="false") boolean incluirSuspendidos,HttpServletRequest req){
  Long tallerId=tallerDelToken(req);
  if(tallerId==null)return Page.empty();
  if(pagina<0)throw new IllegalArgumentException("Pagina invalida.");
  if(!Set.of("asc","desc").contains(orden.toLowerCase(Locale.ROOT)))throw new IllegalArgumentException("Orden debe ser asc o desc.");
  if(!Set.of("nombre_completo","fecha_registro").contains(ordenarPor))throw new IllegalArgumentException("Campo de ordenamiento invalido.");
  return service.list(tallerId,pagina,orden.equalsIgnoreCase("asc"),ordenarPor,incluirSuspendidos);
 }
 @GetMapping("/talleres") public List<TallerOption> talleres(){return service.talleres();}
 @GetMapping("/{id}") public ClienteResponse get(@PathVariable("id") Long id,HttpServletRequest req){service.verificarMismoTaller(id,tallerDelToken(req));return service.get(id);}
 @PutMapping(path="/{id}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ClienteResponse update(@PathVariable("id") Long id,@Valid @ModelAttribute ClienteForm form,HttpServletRequest req){service.verificarMismoTaller(id,tallerDelToken(req));return service.update(id,form);}
 @PatchMapping("/{id}/suspender") public Map<String,Object> suspend(@PathVariable("id") Long id,HttpServletRequest req){service.verificarMismoTaller(id,tallerDelToken(req));return Map.of("ok",true,"mensaje","Cliente suspendido.","cliente",service.suspend(id));}
 @ExceptionHandler(ClienteService.Duplicate.class) public ResponseEntity<?> duplicate(RuntimeException e){return ResponseEntity.status(409).body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(ClienteService.OtroTaller.class) public ResponseEntity<?> otroTaller(RuntimeException e){return ResponseEntity.status(403).body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<?> constraint(DataIntegrityViolationException e){return ResponseEntity.status(409).body(Map.of("mensaje","Correo o telefono duplicado, o taller_id invalido."));}
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<?> invalid(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<?> invalidForm(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("mensaje","Revisa los formatos de los campos."));}
 @ExceptionHandler(NoSuchElementException.class) public ResponseEntity<?> notFound(RuntimeException e){return ResponseEntity.status(404).body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(MaxUploadSizeExceededException.class) public ResponseEntity<?> tooLarge(){return ResponseEntity.status(413).body(Map.of("mensaje","Foto maxima: 7 MB."));}
}
