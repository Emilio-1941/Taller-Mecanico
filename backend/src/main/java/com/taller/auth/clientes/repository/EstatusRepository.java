package com.taller.auth.clientes.repository;
import com.taller.auth.clientes.model.Estatus;
import org.springframework.data.jpa.repository.JpaRepository;
public interface EstatusRepository extends JpaRepository<Estatus,Long>{
 boolean existsByValorIgnoreCase(String valor);
 boolean existsByValorIgnoreCaseAndIdNot(String valor,Long id);
}
