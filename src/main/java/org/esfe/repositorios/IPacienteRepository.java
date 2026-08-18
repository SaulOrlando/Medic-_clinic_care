package org.esfe.repositorios;

import org.esfe.modelos.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IPacienteRepository extends JpaRepository<Paciente, Integer> {

    Optional<Paciente> findByCodigoExpediente(String codigoExpediente);

    Optional<Paciente> findByDocumentoIdentidad(String documentoIdentidad);

    boolean existsByCodigoExpediente(String codigoExpediente);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    List<Paciente> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCase(String nombres, String apellidos);
}
