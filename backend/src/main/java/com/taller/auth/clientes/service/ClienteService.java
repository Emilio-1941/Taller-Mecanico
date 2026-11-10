package com.taller.auth.clientes.service;
import com.taller.auth.clientes.dto.*;
import com.taller.auth.clientes.model.Cliente;
import com.taller.auth.clientes.model.Estatus;
import com.taller.auth.clientes.repository.ClienteRepository;
import com.taller.auth.clientes.repository.TallerRepository;
import com.taller.auth.clientes.model.Taller;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
@Service public class ClienteService {
 private final ClienteRepository repo; private final TallerRepository tallerRepo; public ClienteService(ClienteRepository r,TallerRepository t){repo=r;tallerRepo=t;}
 // Busca un cliente que no esté eliminado (los eliminados se tratan como inexistentes).
 private Cliente buscar(Long id){Cliente c=repo.findById(id).orElseThrow(()->new NoSuchElementException("Cliente no encontrado."));if(c.getEstatusId()==Estatus.ELIMINADO)throw new NoSuchElementException("Cliente no encontrado.");return c;}
 @Transactional(isolation=Isolation.SERIALIZABLE) public ClienteResponse create(ClienteForm f){
  String ep=normEmail(f.getEmailPersonal()),et=normEmail(f.getEmailTrabajo()),tp=normPhone(f.getTelefonoPersonal()),tt=normPhone(f.getTelefonoTrabajo());
  if(repo.existsByEmailPersonalIgnoreCase(ep)||repo.existsByEmailTrabajoIgnoreCase(ep)||(et!=null&&(repo.existsByEmailPersonalIgnoreCase(et)||repo.existsByEmailTrabajoIgnoreCase(et)))||repo.existsByTelefonoPersonal(tp)||repo.existsByTelefonoTrabajo(tp)||repo.existsByTelefonoPersonal(tt)||repo.existsByTelefonoTrabajo(tt))throw new Duplicate("Correo o telefono ya registrado.");
  if(Period.between(f.getFechaNacimiento(),LocalDate.now()).getYears()!=f.getEdad())throw new IllegalArgumentException("Edad y fecha de nacimiento no coinciden.");
  byte[] foto=readPhoto(f.getFoto());
  return ClienteResponse.from(repo.saveAndFlush(new Cliente(f.getNombreCompleto().trim(),trim(f.getContactoAlternativo()),f.getEdad(),f.getFechaNacimiento(),tp,tt,ep,et,foto,f.getCalle().trim(),f.getColonia().trim(),f.getMunicipio().trim(),f.getEstado().trim(),f.getCodigoPostal().trim(),f.getTallerId())));
 }
 @Transactional(readOnly=true) public Page<ClienteResponse> list(Long tallerId,int pagina,boolean asc,String campo,boolean incluirSuspendidos){Sort sort=Sort.by(asc?Sort.Direction.ASC:Sort.Direction.DESC,campo.equals("fecha_registro")?"fechaRegistro":"nombreCompleto");return repo.buscar(tallerId,incluirSuspendidos,PageRequest.of(pagina,10,sort)).map(ClienteResponse::from);}
 @Transactional(readOnly=true) public ClienteResponse get(Long id){return ClienteResponse.from(buscar(id));}
 @Transactional(readOnly=true) public List<TallerOption> talleres(){return tallerRepo.findAll(Sort.by("nombre")).stream().map(TallerOption::from).toList();}
 @Transactional(readOnly=true) public void verificarMismoTaller(Long clienteId,Long tallerIdDelToken){
  if(tallerIdDelToken==null)throw new OtroTaller("Tu usuario no tiene taller asignado.");
  Cliente c=repo.findById(clienteId).orElseThrow(()->new NoSuchElementException("Cliente no encontrado."));
  if(!tallerIdDelToken.equals(c.getTallerId()))throw new OtroTaller("No puedes acceder a clientes de otro taller.");
 }
 @Transactional(isolation=Isolation.SERIALIZABLE) public ClienteResponse update(Long id,ClienteForm f){Cliente c=buscar(id);String ep=normEmail(f.getEmailPersonal()),et=normEmail(f.getEmailTrabajo()),tp=normPhone(f.getTelefonoPersonal()),tt=normPhone(f.getTelefonoTrabajo());if(repo.existsByEmailPersonalIgnoreCaseAndIdNot(ep,id)||repo.existsByEmailTrabajoIgnoreCaseAndIdNot(ep,id)||(et!=null&&(repo.existsByEmailPersonalIgnoreCaseAndIdNot(et,id)||repo.existsByEmailTrabajoIgnoreCaseAndIdNot(et,id)))||repo.existsByTelefonoPersonalAndIdNot(tp,id)||repo.existsByTelefonoTrabajoAndIdNot(tp,id)||repo.existsByTelefonoPersonalAndIdNot(tt,id)||repo.existsByTelefonoTrabajoAndIdNot(tt,id))throw new Duplicate("Correo o telefono ya registrado.");if(Period.between(f.getFechaNacimiento(),LocalDate.now()).getYears()!=f.getEdad())throw new IllegalArgumentException("Edad y fecha de nacimiento no coinciden.");if(!tallerRepo.existsById(f.getTallerId()))throw new IllegalArgumentException("El taller seleccionado no existe.");c.update(f.getNombreCompleto().trim(),trim(f.getContactoAlternativo()),f.getEdad(),f.getFechaNacimiento(),tp,tt,ep,et,readPhoto(f.getFoto()),f.getCalle().trim(),f.getColonia().trim(),f.getMunicipio().trim(),f.getEstado().trim(),f.getCodigoPostal().trim(),f.getTallerId());return ClienteResponse.from(repo.saveAndFlush(c));}
 @Transactional public ClienteResponse suspend(Long id){Cliente c=buscar(id);if(c.getEstatusId()==Estatus.SUSPENDIDO)throw new Conflicto("El cliente ya esta suspendido.");c.suspender();return ClienteResponse.from(repo.saveAndFlush(c));}
 // Soft delete: solo cambia estatus_id a 5, nunca borra la fila.
 @Transactional public ClienteResponse eliminar(Long id){Cliente c=repo.findById(id).orElseThrow(()->new NoSuchElementException("Cliente no encontrado."));if(c.getEstatusId()==Estatus.ELIMINADO)throw new Conflicto("El cliente ya esta eliminado.");c.eliminar();return ClienteResponse.from(repo.saveAndFlush(c));}
 @Transactional(readOnly=true) public FotoCliente photo(Long id){
  Cliente cliente=repo.findById(id).orElseThrow(()->new NoSuchElementException("Cliente no encontrado."));
  byte[] data=cliente.getFoto();
  if(data==null||data.length==0)throw new NoSuchElementException("El cliente no tiene foto.");
  String type;
  if(data.length>=8&&(data[0]&255)==137&&data[1]==80&&data[2]==78&&data[3]==71&&data[4]==13&&data[5]==10&&data[6]==26&&data[7]==10)type="image/png";
  else if(data.length>=3&&(data[0]&255)==255&&(data[1]&255)==216&&(data[2]&255)==255)type="image/jpeg";
  else if(data.length>=12&&new String(data,0,4,StandardCharsets.US_ASCII).equals("RIFF")&&new String(data,8,4,StandardCharsets.US_ASCII).equals("WEBP"))type="image/webp";
  else throw new IllegalArgumentException("Formato de foto no reconocido.");
  return new FotoCliente(data,type);
 }
 public record FotoCliente(byte[] data,String contentType){}
 private byte[] readPhoto(MultipartFile f){if(f==null||f.isEmpty())return null;if(f.getSize()>7L*1024*1024)throw new IllegalArgumentException("Foto maxima: 7 MB.");try{byte[] b=f.getBytes();boolean jpg=b.length>2&&(b[0]&255)==255&&(b[1]&255)==216&&(b[2]&255)==255;boolean png=b.length>=8&&(b[0]&255)==137&&b[1]==80&&b[2]==78&&b[3]==71&&b[4]==13&&b[5]==10&&b[6]==26&&b[7]==10;boolean webp=b.length>=12&&new String(b,0,4,StandardCharsets.US_ASCII).equals("RIFF")&&new String(b,8,4,StandardCharsets.US_ASCII).equals("WEBP");if(!jpg&&!png&&!webp)throw new IllegalArgumentException("Foto debe ser JPG, PNG o WEBP.");return b;}catch(IOException e){throw new IllegalArgumentException("No se pudo leer la foto.",e);}}
 private String normEmail(String s){return s==null||s.isBlank()?null:s.trim().toLowerCase(Locale.ROOT);} private String normPhone(String s){return s.replaceAll("[\\s().-]","");} private String trim(String s){return s==null||s.isBlank()?null:s.trim();}
 public static class Duplicate extends RuntimeException{public Duplicate(String m){super(m);}}
 // Hereda de Duplicate para que el controlador actual ya la responda como 409.
 public static class Conflicto extends Duplicate{public Conflicto(String m){super(m);}}
 public static class OtroTaller extends RuntimeException{public OtroTaller(String m){super(m);}}
}
