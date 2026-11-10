package com.taller.auth.clientes.dto;
import com.taller.auth.clientes.model.Taller;
public record TallerOption(Long id,String nombre){ public static TallerOption from(Taller t){return new TallerOption(t.getId(),t.getNombre());} }
