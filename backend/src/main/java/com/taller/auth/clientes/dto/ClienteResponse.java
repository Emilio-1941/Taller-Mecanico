package com.taller.auth.clientes.dto;
import com.taller.auth.clientes.model.Cliente;
import java.time.LocalDate;
public record ClienteResponse(Long id,String nombreCompleto,String contactoAlternativo,Integer edad,LocalDate fechaNacimiento,String telefonoPersonal,String telefonoTrabajo,String emailPersonal,String emailTrabajo,String calle,String colonia,String municipio,String estado,String codigoPostal,Long tallerId,boolean tieneFoto){
 public static ClienteResponse from(Cliente c){return new ClienteResponse(c.getId(),c.getNombreCompleto(),c.getContactoAlternativo(),c.getEdad(),c.getFechaNacimiento(),c.getTelefonoPersonal(),c.getTelefonoTrabajo(),c.getEmailPersonal(),c.getEmailTrabajo(),c.getCalle(),c.getColonia(),c.getMunicipio(),c.getEstado(),c.getCodigoPostal(),c.getTallerId(),c.hasFoto());}
}
