package com.taller.auth.clientes.repository;
import com.taller.auth.clientes.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClienteRepository extends JpaRepository<Cliente,Long>{
 boolean existsByEmailPersonalIgnoreCase(String v); boolean existsByEmailTrabajoIgnoreCase(String v);
 boolean existsByTelefonoPersonal(String v); boolean existsByTelefonoTrabajo(String v);
}
