package com.taller.auth.clientes.model;
import jakarta.persistence.*;
import java.time.LocalDate;
@Entity @Table(name="clientes") public class Cliente {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(name="nombre_completo",nullable=false) private String nombreCompleto;
 @Column(name="contacto_alternativo") private String contactoAlternativo;
 @Column(nullable=false) private Integer edad;
 @Column(name="fecha_nacimiento",nullable=false) private LocalDate fechaNacimiento;
 @Column(name="telefono_personal",nullable=false) private String telefonoPersonal;
 @Column(name="telefono_trabajo",nullable=false) private String telefonoTrabajo;
 @Column(name="email_personal",nullable=false) private String emailPersonal;
 @Column(name="email_trabajo") private String emailTrabajo;
 @Lob @Column(name="foto") private byte[] foto;
 @Column(nullable=false) private String calle;
 @Column(nullable=false) private String colonia;
 @Column(nullable=false) private String municipio;
 @Column(nullable=false) private String estado;
 @Column(name="codigo_postal",nullable=false) private String codigoPostal;
 @Column(name="taller_id",nullable=false) private Long tallerId;
 protected Cliente(){}
 public Cliente(String n,String ca,Integer e,LocalDate f,String tp,String tt,String ep,String et,byte[] foto,String c,String co,String m,String es,String cp,Long ti){this.nombreCompleto=n;this.contactoAlternativo=ca;this.edad=e;this.fechaNacimiento=f;this.telefonoPersonal=tp;this.telefonoTrabajo=tt;this.emailPersonal=ep;this.emailTrabajo=et;this.foto=foto;this.calle=c;this.colonia=co;this.municipio=m;this.estado=es;this.codigoPostal=cp;this.tallerId=ti;}
 public Long getId(){return id;} public byte[] getFoto(){return foto;} public boolean hasFoto(){return foto!=null&&foto.length>0;} public String getNombreCompleto(){return nombreCompleto;} public String getContactoAlternativo(){return contactoAlternativo;} public Integer getEdad(){return edad;} public LocalDate getFechaNacimiento(){return fechaNacimiento;} public String getTelefonoPersonal(){return telefonoPersonal;} public String getTelefonoTrabajo(){return telefonoTrabajo;} public String getEmailPersonal(){return emailPersonal;} public String getEmailTrabajo(){return emailTrabajo;} public String getCalle(){return calle;} public String getColonia(){return colonia;} public String getMunicipio(){return municipio;} public String getEstado(){return estado;} public String getCodigoPostal(){return codigoPostal;} public Long getTallerId(){return tallerId;}
}
