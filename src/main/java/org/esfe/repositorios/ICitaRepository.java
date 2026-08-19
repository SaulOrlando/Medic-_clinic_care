package org.esfe.repositorios;

import org.esfe.modelos.Cita;
import org.esfe.modelos.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ICitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByPacienteIdPaciente(Integer idPaciente);

    List<Cita> findByMedicoIdMedico(Integer idMedico);

    List<Cita> findByUsuarioGestorIdUsuario(Integer idUsuario);

    List<Cita> findByEstado(EstadoCita estado);

    List<Cita> findByMedicoIdMedicoAndEstado(Integer idMedico, EstadoCita estado);

    List<Cita> findByPacienteIdPacienteAndEstado(Integer idPaciente, EstadoCita estado);

    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Cita> findByMedicoIdMedicoAndFechaHoraBetween(Integer idMedico, LocalDateTime inicio, LocalDateTime fin);

    boolean existsByMedicoIdMedicoAndFechaHoraAndEstadoNot(Integer idMedico, LocalDateTime fechaHora, EstadoCita estado);
}
