package com.taller.auth.clientes.dto;
import jakarta.validation.constraints.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDate;
public class ClienteForm {
 @NotBlank @Size(max=160) private String nombreCompleto;
 @Size(max=120) private String contactoAlternativo;
 @NotNull @Min(0) @Max(130) private Integer edad;
 @NotNull @Past private LocalDate fechaNacimiento;
 @NotBlank @Pattern(regexp="^\\+?[0-9][0-9\\s().-]{6,19}$") private String telefonoPersonal;
 @NotBlank @Pattern(regexp="^\\+?[0-9][0-9\\s().-]{6,19}$") private String telefonoTrabajo;
 @NotBlank @Email @Size(max=254) private String emailPersonal;
 @Email @Size(max=254) private String emailTrabajo;
 @NotBlank private String calle; @NotBlank private String colonia; @NotBlank private String municipio; @NotBlank private String estado;
 @NotBlank @Pattern(regexp="^[0-9]{5}$") private String codigoPostal;
 @NotNull @Positive private Long tallerId;
 private MultipartFile foto;
 public String getNombreCompleto(){return nombreCompleto;} public void setNombreCompleto(String v){nombreCompleto=v;}
 public String getContactoAlternativo(){return contactoAlternativo;} public void setContactoAlternativo(String v){contactoAlternativo=v;}
 public Integer getEdad(){return edad;} public void setEdad(Integer v){edad=v;}
 public LocalDate getFechaNacimiento(){return fechaNacimiento;} public void setFechaNacimiento(LocalDate v){fechaNacimiento=v;}
 public String getTelefonoPersonal(){return telefonoPersonal;} public void setTelefonoPersonal(String v){telefonoPersonal=v;}
 public String getTelefonoTrabajo(){return telefonoTrabajo;} public void setTelefonoTrabajo(String v){telefonoTrabajo=v;}
 public String getEmailPersonal(){return emailPersonal;} public void setEmailPersonal(String v){emailPersonal=v;}
 public String getEmailTrabajo(){return emailTrabajo;} public void setEmailTrabajo(String v){emailTrabajo=v;}
 public String getCalle(){return calle;} public void setCalle(String v){calle=v;} public String getColonia(){return colonia;} public void setColonia(String v){colonia=v;}
 public String getMunicipio(){return municipio;} public void setMunicipio(String v){municipio=v;} public String getEstado(){return estado;} public void setEstado(String v){estado=v;}
 public String getCodigoPostal(){return codigoPostal;} public void setCodigoPostal(String v){codigoPostal=v;} public Long getTallerId(){return tallerId;} public void setTallerId(Long v){tallerId=v;}
 public MultipartFile getFoto(){return foto;} public void setFoto(MultipartFile v){foto=v;}
}
