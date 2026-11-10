package com.taller.auth.clientes.repository;
import com.taller.auth.clientes.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface ClienteRepository extends JpaRepository<Cliente,Long>{
 boolean existsByEmailPersonalIgnoreCase(String v); boolean existsByEmailTrabajoIgnoreCase(String v);
 boolean existsByTelefonoPersonal(String v); boolean existsByTelefonoTrabajo(String v);
 boolean existsByEmailPersonalIgnoreCaseAndIdNot(String v,Long id); boolean existsByEmailTrabajoIgnoreCaseAndIdNot(String v,Long id);
 boolean existsByTelefonoPersonalAndIdNot(String v,Long id); boolean existsByTelefonoTrabajoAndIdNot(String v,Long id);
 // Por defecto solo estatus 1 (activo). Con incluirOtros=true entran todos menos 5 (eliminado).
 @Query("select c from Cliente c where (:tallerId is null or c.tallerId = :tallerId) and c.estatusId <> 5 and (:incluirOtros = true or c.estatusId = 1)")
 Page<Cliente> buscar(@Param("tallerId") Long tallerId,@Param("incluirOtros") boolean incluirOtros,Pageable pageable);
}
