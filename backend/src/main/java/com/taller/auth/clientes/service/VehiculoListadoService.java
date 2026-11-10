package com.taller.auth.clientes.service;
import com.taller.auth.clientes.dto.VehiculoListItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service public class VehiculoListadoService {
 private static final Map<String,String> COLUMNAS=Map.of("cliente","c.nombre_completo","placa","v.placa","marca","v.marca","anio","v.anio","fecha_registro","v.creado_en");
 private static final int TAMANO=10;
 private final JdbcTemplate jdbc; public VehiculoListadoService(JdbcTemplate j){jdbc=j;}
 public record Pagina(List<VehiculoListItem> content,int totalPages,long totalElements,int pagina){}
 @Transactional(readOnly=true) public Pagina listar(Long tallerId,int pagina,String ordenarPor,boolean asc){
  if(pagina<0)throw new IllegalArgumentException("Pagina invalida.");
  String col=COLUMNAS.get(ordenarPor);
  if(col==null)throw new IllegalArgumentException("Campo de ordenamiento invalido.");
  if(tallerId==null)return new Pagina(List.of(),0,0,pagina);
  Long total=jdbc.queryForObject("SELECT COUNT(*) FROM vehiculos v JOIN clientes c ON c.id=v.cliente_id WHERE c.taller_id=?",Long.class,tallerId);
  long totalElementos=total==null?0:total;
  String sql="SELECT v.id,v.cliente_id,c.nombre_completo,v.vin,v.placa,v.marca,v.modelo,v.anio,v.version,v.color,v.kilometraje,v.numero_serie,e.valor AS estatus FROM vehiculos v JOIN clientes c ON c.id=v.cliente_id JOIN estatus e ON e.id=v.estatus_id WHERE c.taller_id=? ORDER BY "+col+(asc?" ASC":" DESC")+", v.id ASC LIMIT ? OFFSET ?";
  List<VehiculoListItem> filas=jdbc.query(sql,(rs,i)->new VehiculoListItem(rs.getLong("id"),rs.getLong("cliente_id"),rs.getString("nombre_completo"),rs.getString("vin"),rs.getString("placa"),rs.getString("marca"),rs.getString("modelo"),rs.getInt("anio"),rs.getString("version"),rs.getString("color"),rs.getInt("kilometraje"),rs.getString("numero_serie"),rs.getString("estatus")),tallerId,TAMANO,pagina*TAMANO);
  int paginas=(int)((totalElementos+TAMANO-1)/TAMANO);
  return new Pagina(filas,paginas,totalElementos,pagina);
 }
}
