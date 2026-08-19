package org.esfe.repositorios;

import org.esfe.modelos.HistorialCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHistorialCitaRepository extends JpaRepository<HistorialCita, Integer> {

    List<HistorialCita> findByCitaIdCita(Integer idCita);

    List<HistorialCita> findByUsuarioIdUsuario(Integer idUsuario);
}
