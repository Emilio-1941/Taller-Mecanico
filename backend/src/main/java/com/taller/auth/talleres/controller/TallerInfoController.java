package com.taller.auth.talleres.controller;

import com.taller.auth.talleres.dto.TallerInfoForm;
import com.taller.auth.talleres.dto.TallerInfoResponse;
import com.taller.auth.talleres.service.TallerInfoService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/talleres-info")
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class TallerInfoController {
    private final TallerInfoService service;
    public TallerInfoController(TallerInfoService service) { this.service = service; }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> create(@Valid @ModelAttribute TallerInfoForm form) {
        return ResponseEntity.status(201).body(Map.of("ok", true, "mensaje", "Taller registrado correctamente.",
            "taller", service.create(form)));
    }

    @GetMapping public List<TallerInfoResponse> list() { return service.list(); }

    @GetMapping("/{id}/foto")
    public ResponseEntity<byte[]> photo(@PathVariable("id") Long id) {
        var photo = service.photo(id);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(photo.contentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
            .header(HttpHeaders.CACHE_CONTROL, "private, max-age=3600").body(photo.data());
    }

    @ExceptionHandler(TallerInfoService.Duplicate.class)
    public ResponseEntity<?> duplicate(RuntimeException e) { return ResponseEntity.status(409).body(Map.of("mensaje", e.getMessage())); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> constraint() { return ResponseEntity.status(409).body(Map.of("mensaje", "El RFC o correo ya está registrado.")); }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> invalid(RuntimeException e) { return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage())); }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> invalidForm() { return ResponseEntity.badRequest().body(Map.of("mensaje", "Revisa RFC, correo y teléfonos.")); }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(RuntimeException e) { return ResponseEntity.status(404).body(Map.of("mensaje", e.getMessage())); }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> tooLarge() { return ResponseEntity.status(413).body(Map.of("mensaje", "La foto no puede superar 15 MB.")); }
}
