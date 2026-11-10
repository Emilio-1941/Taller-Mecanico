package com.taller.auth.talleres.model;

import jakarta.persistence.*;

@Entity
@Table(name = "talleres_info", uniqueConstraints = {
    @UniqueConstraint(name = "uk_talleres_info_rfc", columnNames = "rfc"),
    @UniqueConstraint(name = "uk_talleres_info_email", columnNames = "email")
})
public class TallerInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 160) private String nombre;
    @Column(nullable = false, length = 160) private String calle;
    @Column(nullable = false, length = 120) private String colonia;
    @Column(nullable = false, length = 120) private String municipio;
    @Column(nullable = false, length = 120) private String estado;
    @Column(name = "codigo_postal", nullable = false, length = 5) private String codigoPostal;
    @Column(name = "razon_social", nullable = false, length = 180) private String razonSocial;
    @Column(nullable = false, length = 24) private String telefono;
    @Column(nullable = false, length = 13) private String rfc;
    @Column(nullable = false, length = 254) private String email;
    @Column(name = "numero_contacto", nullable = false, length = 24) private String numeroContacto;
    @Lob @Column(name = "foto") private byte[] foto;

    protected TallerInfo() {}

    public TallerInfo(String nombre, String calle, String colonia, String municipio, String estado,
                      String codigoPostal, String razonSocial, String telefono, String rfc,
                      String email, String numeroContacto, byte[] foto) {
        this.nombre = nombre;
        this.calle = calle;
        this.colonia = colonia;
        this.municipio = municipio;
        this.estado = estado;
        this.codigoPostal = codigoPostal;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.rfc = rfc;
        this.email = email;
        this.numeroContacto = numeroContacto;
        this.foto = foto;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCalle() { return calle; }
    public String getColonia() { return colonia; }
    public String getMunicipio() { return municipio; }
    public String getEstado() { return estado; }
    public String getCodigoPostal() { return codigoPostal; }
    public String getRazonSocial() { return razonSocial; }
    public String getTelefono() { return telefono; }
    public String getRfc() { return rfc; }
    public String getEmail() { return email; }
    public String getNumeroContacto() { return numeroContacto; }
    public byte[] getFoto() { return foto; }
    public boolean hasFoto() { return foto != null && foto.length > 0; }
}
