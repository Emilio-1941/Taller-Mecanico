package com.taller.auth.clientes.repository;
import com.taller.auth.clientes.model.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface VehiculoRepository extends JpaRepository<Vehiculo,Long>{
 boolean existsByVin(String vin);
 boolean existsByNumeroSerieIgnoreCase(String numeroSerie);
 boolean existsByPlaca(String placa);
 Optional<Vehiculo> findByVin(String vin);
 Optional<Vehiculo> findByPlaca(String placa);
 Optional<Vehiculo> findByNumeroSerieIgnoreCase(String numeroSerie);
}
