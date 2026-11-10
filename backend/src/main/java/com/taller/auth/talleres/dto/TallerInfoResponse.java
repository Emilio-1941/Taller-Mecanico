package com.taller.auth.talleres.dto;

import com.taller.auth.talleres.model.TallerInfo;

public record TallerInfoResponse(Long id, String nombre, String calle, String colonia, String municipio,
                                 String estado, String codigoPostal, String razonSocial, String telefono,
                                 String rfc, String email, String numeroContacto, boolean tieneFoto) {
    public static TallerInfoResponse from(TallerInfo t) {
        return new TallerInfoResponse(t.getId(), t.getNombre(), t.getCalle(), t.getColonia(), t.getMunicipio(),
            t.getEstado(), t.getCodigoPostal(), t.getRazonSocial(), t.getTelefono(), t.getRfc(), t.getEmail(),
            t.getNumeroContacto(), t.hasFoto());
    }
}
