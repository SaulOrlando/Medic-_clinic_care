package org.esfe.repositorios;

import org.esfe.modelos.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMedicoRepository extends JpaRepository<Medico, Integer> {

    Optional<Medico> findByNumeroLicencia(String numeroLicencia);

    Optional<Medico> findByUsuarioIdUsuario(Integer idUsuario);

    List<Medico> findByEspecialidad(String especialidad);

    List<Medico> findByDisponible(Boolean disponible);

    boolean existsByNumeroLicencia(String numeroLicencia);

    boolean existsByUsuarioIdUsuario(Integer idUsuario);
}
