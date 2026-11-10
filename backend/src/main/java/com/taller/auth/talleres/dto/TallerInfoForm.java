package com.taller.auth.talleres.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class TallerInfoForm {
    @NotBlank @Size(max = 160) private String nombre;
    @NotBlank @Size(max = 160) private String calle;
    @NotBlank @Size(max = 120) private String colonia;
    @NotBlank @Size(max = 120) private String municipio;
    @NotBlank @Size(max = 120) private String estado;
    @NotBlank @Pattern(regexp = "^[0-9]{5}$") private String codigoPostal;
    @NotBlank @Size(max = 180) private String razonSocial;
    @NotBlank @Pattern(regexp = "^\\+?[0-9][0-9\\s().-]{6,19}$") private String telefono;
    @NotBlank @Pattern(regexp = "^(?:[A-ZÑ&]{3}|[A-ZÑ&]{4})[0-9]{6}[A-Z0-9]{3}$") private String rfc;
    @NotBlank @Email @Size(max = 254) private String email;
    @NotBlank @Pattern(regexp = "^\\+?[0-9][0-9\\s().-]{6,19}$") private String numeroContacto;
    private MultipartFile foto;

    public String getNombre() { return nombre; }
    public void setNombre(String v) { nombre = v; }
    public String getCalle() { return calle; }
    public void setCalle(String v) { calle = v; }
    public String getColonia() { return colonia; }
    public void setColonia(String v) { colonia = v; }
    public String getMunicipio() { return municipio; }
    public void setMunicipio(String v) { municipio = v; }
    public String getEstado() { return estado; }
    public void setEstado(String v) { estado = v; }
    public String getCodigoPostal() { return codigoPostal; }
    public void setCodigoPostal(String v) { codigoPostal = v; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String v) { razonSocial = v; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String v) { telefono = v; }
    public String getRfc() { return rfc; }
    public void setRfc(String v) { rfc = v; }
    public String getEmail() { return email; }
    public void setEmail(String v) { email = v; }
    public String getNumeroContacto() { return numeroContacto; }
    public void setNumeroContacto(String v) { numeroContacto = v; }
    public MultipartFile getFoto() { return foto; }
    public void setFoto(MultipartFile v) { foto = v; }
}
