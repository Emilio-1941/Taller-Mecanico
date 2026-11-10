package com.taller.auth.clientes.model;
import jakarta.persistence.*;
@Entity @Table(name="estatus") public class Estatus {
 public static final long ACTIVO=1, SUSPENDIDO=2, CANCELADO=3, ACTUALIZADO=4, ELIMINADO=5;
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=40) private String valor;
 @Column(nullable=false) private String descripcion;
 protected Estatus(){}
 public Estatus(String valor,String descripcion){this.valor=valor;this.descripcion=descripcion;}
 public Long getId(){return id;} public String getValor(){return valor;} public String getDescripcion(){return descripcion;}
 public void update(String valor,String descripcion){this.valor=valor;this.descripcion=descripcion;}
}
