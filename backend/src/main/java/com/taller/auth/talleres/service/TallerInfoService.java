package com.taller.auth.talleres.service;

import com.taller.auth.talleres.dto.TallerInfoForm;
import com.taller.auth.talleres.dto.TallerInfoResponse;
import com.taller.auth.talleres.model.TallerInfo;
import com.taller.auth.talleres.repository.TallerInfoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@Service
public class TallerInfoService {
    private static final long MAX_PHOTO_SIZE = 15L * 1024 * 1024;
    private final TallerInfoRepository repository;

    public TallerInfoService(TallerInfoRepository repository) { this.repository = repository; }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public TallerInfoResponse create(TallerInfoForm form) {
        String rfc = form.getRfc().trim().toUpperCase(Locale.ROOT);
        String email = form.getEmail().trim().toLowerCase(Locale.ROOT);
        if (repository.existsByRfcIgnoreCaseOrEmailIgnoreCase(rfc, email)) {
            throw new Duplicate("El RFC o correo ya está registrado.");
        }
        byte[] foto = readPhoto(form.getFoto());
        TallerInfo taller = new TallerInfo(form.getNombre().trim(), form.getCalle().trim(), form.getColonia().trim(),
            form.getMunicipio().trim(), form.getEstado().trim(), form.getCodigoPostal().trim(),
            form.getRazonSocial().trim(), form.getTelefono().trim(), rfc, email,
            form.getNumeroContacto().trim(), foto);
        return TallerInfoResponse.from(repository.saveAndFlush(taller));
    }

    @Transactional(readOnly = true)
    public List<TallerInfoResponse> list() {
        return repository.findAll().stream().map(TallerInfoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FotoTaller photo(Long id) {
        byte[] data = repository.findById(id).orElseThrow(() -> new NoSuchElementException("Taller no encontrado.")).getFoto();
        if (data == null || data.length == 0) throw new NoSuchElementException("El taller no tiene foto.");
        String type;
        if (data.length >= 8 && (data[0] & 255) == 137 && data[1] == 80 && data[2] == 78 && data[3] == 71
            && data[4] == 13 && data[5] == 10 && data[6] == 26 && data[7] == 10) type = "image/png";
        else if (data.length >= 3 && (data[0] & 255) == 255 && (data[1] & 255) == 216 && (data[2] & 255) == 255) type = "image/jpeg";
        else if (data.length >= 12 && new String(data, 0, 4, StandardCharsets.US_ASCII).equals("RIFF")
            && new String(data, 8, 4, StandardCharsets.US_ASCII).equals("WEBP")) type = "image/webp";
        else throw new IllegalArgumentException("Formato de foto no reconocido.");
        return new FotoTaller(data, type);
    }

    private byte[] readPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        if (file.getSize() > MAX_PHOTO_SIZE) throw new IllegalArgumentException("La foto no puede superar 15 MB.");
        try {
            byte[] data = file.getBytes();
            boolean jpg = data.length >= 3 && (data[0] & 255) == 255 && (data[1] & 255) == 216 && (data[2] & 255) == 255;
            boolean png = data.length >= 8 && (data[0] & 255) == 137 && data[1] == 80 && data[2] == 78 && data[3] == 71
                && data[4] == 13 && data[5] == 10 && data[6] == 26 && data[7] == 10;
            boolean webp = data.length >= 12 && new String(data, 0, 4, StandardCharsets.US_ASCII).equals("RIFF")
                && new String(data, 8, 4, StandardCharsets.US_ASCII).equals("WEBP");
            if (!jpg && !png && !webp) throw new IllegalArgumentException("La foto debe ser JPG, PNG o WEBP.");
            return data;
        } catch (IOException e) {
            throw new IllegalArgumentException("No se pudo leer la foto.", e);
        }
    }

    public record FotoTaller(byte[] data, String contentType) {}
    public static class Duplicate extends RuntimeException { public Duplicate(String message) { super(message); } }
}
