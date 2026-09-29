package com.taller.auth.clientes.controller;
import com.taller.auth.clientes.dto.*;
import com.taller.auth.clientes.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import java.util.*;
@RestController @RequestMapping("/api/clientes") @CrossOrigin(origins={"http://localhost:5173","http://127.0.0.1:5173"})
public class ClienteController {
 private final ClienteService service; public ClienteController(ClienteService s){service=s;}
 @PostMapping(consumes=MediaType.MULTIPART_FORM_DATA_VALUE) public ResponseEntity<?> create(@Valid @ModelAttribute ClienteForm f){return ResponseEntity.status(201).body(Map.of("ok",true,"mensaje","Cliente registrado correctamente.","cliente",service.create(f)));}
 @GetMapping("/{id}/foto") public ResponseEntity<byte[]> photo(@PathVariable Long id){var foto=service.photo(id);return ResponseEntity.ok().contentType(MediaType.parseMediaType(foto.contentType())).header(HttpHeaders.CONTENT_DISPOSITION,"inline").header(HttpHeaders.CACHE_CONTROL,"private, max-age=3600").body(foto.data());}
 @GetMapping public List<ClienteResponse> list(){return service.list();}
 @ExceptionHandler(ClienteService.Duplicate.class) public ResponseEntity<?> duplicate(RuntimeException e){return ResponseEntity.status(409).body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(DataIntegrityViolationException.class) public ResponseEntity<?> constraint(DataIntegrityViolationException e){return ResponseEntity.status(409).body(Map.of("mensaje","Correo o telefono duplicado, o taller_id invalido."));}
 @ExceptionHandler(IllegalArgumentException.class) public ResponseEntity<?> invalid(RuntimeException e){return ResponseEntity.badRequest().body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<?> invalidForm(MethodArgumentNotValidException e){return ResponseEntity.badRequest().body(Map.of("mensaje","Revisa los formatos de los campos."));}
 @ExceptionHandler(NoSuchElementException.class) public ResponseEntity<?> notFound(RuntimeException e){return ResponseEntity.status(404).body(Map.of("mensaje",e.getMessage()));}
 @ExceptionHandler(MaxUploadSizeExceededException.class) public ResponseEntity<?> tooLarge(){return ResponseEntity.status(413).body(Map.of("mensaje","Foto maxima: 7 MB."));}
}
