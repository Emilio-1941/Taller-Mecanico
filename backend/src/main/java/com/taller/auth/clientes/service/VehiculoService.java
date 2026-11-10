package com.taller.auth.clientes.service;
import com.taller.auth.clientes.dto.*;
import com.taller.auth.clientes.model.*;
import com.taller.auth.clientes.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Year;
import java.util.*;
import java.util.regex.Pattern;
@Service public class VehiculoService {
 private static final Pattern VIN=Pattern.compile("^[A-HJ-NPR-Z0-9]{17}$"),PLACA=Pattern.compile("^[A-Z0-9]{5,8}$");
 private final VehiculoRepository repo; private final ClienteRepository clientes;
 public VehiculoService(VehiculoRepository r,ClienteRepository c){repo=r;clientes=c;}
 private String trim(String s){return s==null||s.isBlank()?null:s.trim();}
 // Un vehiculo (VIN, placa o numero de serie) solo puede existir una vez y pertenecer a un solo cliente.
 // No se revela quien es el otro cliente: puede ser de otro taller.
 private void verificarNoRegistrado(String campo,Optional<Vehiculo> existente,Long clienteId){
  if(existente.isEmpty())return;
  if(clienteId.equals(existente.get().getClienteId()))throw new ClienteService.Duplicate("Este vehiculo ya esta registrado para este cliente (mismo "+campo+"). No se puede registrar dos veces.");
  throw new ClienteService.Duplicate("ALERTA: este vehiculo ya esta asociado a otro cliente (mismo "+campo+"). Un vehiculo solo puede pertenecer a un cliente.");
 }
 @Transactional public VehiculoResponse create(VehiculoForm f,Long tallerIdToken){
  if(tallerIdToken==null)throw new ClienteService.OtroTaller("Tu usuario no tiene taller asignado.");
  Cliente c=clientes.findById(f.clienteId()).orElseThrow(()->new NoSuchElementException("Cliente no encontrado."));
  if(c.getEstatusId()==Estatus.ELIMINADO)throw new NoSuchElementException("Cliente no encontrado.");
  if(!tallerIdToken.equals(c.getTallerId()))throw new ClienteService.OtroTaller("No puedes registrar vehiculos para clientes de otro taller.");
  // Precondicion: el cliente debe estar activo.
  if(!c.isActivo())throw new ClienteService.Conflicto("El cliente debe estar activo para registrar un vehiculo.");
  String vin=f.vin().trim().toUpperCase(Locale.ROOT);
  String placa=f.placa().replaceAll("[\\s-]","").toUpperCase(Locale.ROOT);
  String numeroSerie=f.numeroSerie().trim().toUpperCase(Locale.ROOT);
  if(!VIN.matcher(vin).matches())throw new IllegalArgumentException("VIN invalido: 17 caracteres, letras mayusculas y numeros, sin I, O ni Q.");
  if(!PLACA.matcher(placa).matches())throw new IllegalArgumentException("Placa invalida: 5 a 8 letras o numeros (sin espacios ni guiones).");
  int max=Year.now().getValue()+1;
  if(f.anio()<1980||f.anio()>max)throw new IllegalArgumentException("El anio debe estar entre 1980 y "+max+".");
  verificarNoRegistrado("VIN",repo.findByVin(vin),c.getId());
  verificarNoRegistrado("placa",repo.findByPlaca(placa),c.getId());
  verificarNoRegistrado("numero de serie",repo.findByNumeroSerieIgnoreCase(numeroSerie),c.getId());
  return VehiculoResponse.from(repo.saveAndFlush(new Vehiculo(c.getId(),vin,placa,f.marca().trim(),f.modelo().trim(),f.anio(),trim(f.version()),f.color().trim(),f.kilometraje(),numeroSerie)));
 }
}
