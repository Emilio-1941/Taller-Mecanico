package com.taller.auth.clientes.dto;
import com.taller.auth.clientes.model.Vehiculo;
public record VehiculoResponse(Long id,Long clienteId,String vin,String placa,String marca,String modelo,Integer anio,String version,String color,Integer kilometraje,String numeroSerie,Long estatusId){
 public static VehiculoResponse from(Vehiculo v){return new VehiculoResponse(v.getId(),v.getClienteId(),v.getVin(),v.getPlaca(),v.getMarca(),v.getModelo(),v.getAnio(),v.getVersion(),v.getColor(),v.getKilometraje(),v.getNumeroSerie(),v.getEstatusId());}
}
