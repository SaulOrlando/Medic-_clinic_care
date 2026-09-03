package org.esfe.repositorios;

import org.esfe.modelos.CategoriaMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICategoriaMedicamentoRepository extends JpaRepository<CategoriaMedicamento, Integer> {

    Optional<CategoriaMedicamento> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<CategoriaMedicamento> findAllByOrderByNombreAsc();
}
