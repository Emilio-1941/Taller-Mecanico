package com.taller.auth.clientes.service;
import com.taller.auth.clientes.dto.*;
import com.taller.auth.clientes.model.Cliente;
import com.taller.auth.clientes.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.*;
@Service public class ClienteService {
 private final ClienteRepository repo; public ClienteService(ClienteRepository r){repo=r;}
 @Transactional(isolation=Isolation.SERIALIZABLE) public ClienteResponse create(ClienteForm f){
  String ep=normEmail(f.getEmailPersonal()),et=normEmail(f.getEmailTrabajo()),tp=normPhone(f.getTelefonoPersonal()),tt=normPhone(f.getTelefonoTrabajo());
  if(repo.existsByEmailPersonalIgnoreCase(ep)||repo.existsByEmailTrabajoIgnoreCase(ep)||(et!=null&&(repo.existsByEmailPersonalIgnoreCase(et)||repo.existsByEmailTrabajoIgnoreCase(et)))||repo.existsByTelefonoPersonal(tp)||repo.existsByTelefonoTrabajo(tp)||repo.existsByTelefonoPersonal(tt)||repo.existsByTelefonoTrabajo(tt))throw new Duplicate("Correo o telefono ya registrado.");
  if(Period.between(f.getFechaNacimiento(),LocalDate.now()).getYears()!=f.getEdad())throw new IllegalArgumentException("Edad y fecha de nacimiento no coinciden.");
  byte[] foto=readPhoto(f.getFoto());
  return ClienteResponse.from(repo.saveAndFlush(new Cliente(f.getNombreCompleto().trim(),trim(f.getContactoAlternativo()),f.getEdad(),f.getFechaNacimiento(),tp,tt,ep,et,foto,f.getCalle().trim(),f.getColonia().trim(),f.getMunicipio().trim(),f.getEstado().trim(),f.getCodigoPostal().trim(),f.getTallerId())));
 }
 @Transactional(readOnly=true) public List<ClienteResponse> list(){return repo.findAll().stream().map(ClienteResponse::from).toList();}
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
}
