package com.taller.auth.clientes.dto;
import jakarta.validation.constraints.*;
public record VehiculoForm(
 @NotNull @Positive Long clienteId,
 @NotBlank @Size(max=40) String vin,
 @NotBlank @Size(max=30) String placa,
 @NotBlank @Size(max=60) String marca,
 @NotBlank @Size(max=60) String modelo,
 @NotNull Integer anio,
 @Size(max=80) String version,
 @NotBlank @Size(max=40) String color,
 @NotNull @Min(0) Integer kilometraje,
 @NotBlank @Size(max=40) String numeroSerie){}
