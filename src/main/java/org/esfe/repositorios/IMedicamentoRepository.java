package org.esfe.repositorios;

import org.esfe.modelos.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IMedicamentoRepository extends JpaRepository<Medicamento, Integer> {

    List<Medicamento> findByCategoriaIdCategoria(Integer idCategoria);

    List<Medicamento> findByNombreComercialContainingIgnoreCase(String nombreComercial);

    Optional<Medicamento> findByNombreComercial(String nombreComercial);

    List<Medicamento> findByFechaVencimientoBefore(LocalDate fecha);

    @Query("SELECT m FROM Medicamento m WHERE m.stockDisponible <= :cantidad")
    List<Medicamento> findStockBajo(@Param("cantidad") Integer cantidad);
}
