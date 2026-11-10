package com.taller.auth.clientes.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
@Entity @Table(name="talleres_info")
public class Taller {
 @Id private Long id;
 private String nombre;
 public Long getId(){return id;} public String getNombre(){return nombre;}
}
