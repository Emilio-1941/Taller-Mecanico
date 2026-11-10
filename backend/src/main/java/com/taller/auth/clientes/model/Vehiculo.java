package com.taller.auth.clientes.model;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name="vehiculos",uniqueConstraints={@UniqueConstraint(name="uk_vehiculos_vin",columnNames="vin"),@UniqueConstraint(name="uk_vehiculos_placa",columnNames="placa"),@UniqueConstraint(name="uk_vehiculos_numero_serie",columnNames="numero_serie")}) public class Vehiculo {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="cliente_id",nullable=false) private Long clienteId;
 @Column(nullable=false,length=17) private String vin;
 @Column(nullable=false,length=8) private String placa;
 @Column(nullable=false,length=60) private String marca;
 @Column(nullable=false,length=60) private String modelo;
 @Column(nullable=false) private Integer anio;
 @Column(name="version",length=80) private String version;
 @Column(nullable=false,length=40) private String color;
 @Column(nullable=false) private Integer kilometraje;
 @Column(name="numero_serie",nullable=false,length=40) private String numeroSerie;
 @Column(name="estatus_id",nullable=false) private Long estatusId=Estatus.ACTIVO;
 @Column(name="creado_en",insertable=false,updatable=false) private LocalDateTime creadoEn;
 protected Vehiculo(){}
 public Vehiculo(Long clienteId,String vin,String placa,String marca,String modelo,Integer anio,String version,String color,Integer kilometraje,String numeroSerie){this.clienteId=clienteId;this.vin=vin;this.placa=placa;this.marca=marca;this.modelo=modelo;this.anio=anio;this.version=version;this.color=color;this.kilometraje=kilometraje;this.numeroSerie=numeroSerie;}
 public Long getId(){return id;} public Long getClienteId(){return clienteId;} public String getVin(){return vin;} public String getPlaca(){return placa;} public String getMarca(){return marca;} public String getModelo(){return modelo;} public Integer getAnio(){return anio;} public String getVersion(){return version;} public String getColor(){return color;} public Integer getKilometraje(){return kilometraje;} public String getNumeroSerie(){return numeroSerie;} public Long getEstatusId(){return estatusId;} public LocalDateTime getCreadoEn(){return creadoEn;}
}
