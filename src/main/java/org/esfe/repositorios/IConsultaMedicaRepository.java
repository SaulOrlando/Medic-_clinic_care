package org.esfe.repositorios;

import org.esfe.modelos.ConsultaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface IConsultaMedicaRepository extends JpaRepository<ConsultaMedica, Integer> {

    Optional<ConsultaMedica> findByCitaIdCita(Integer idCita);

    List<ConsultaMedica> findByFechaConsultaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<ConsultaMedica> findByDiagnosticoContainingIgnoreCase(String diagnostico);

    boolean existsByCitaIdCita(Integer idCita);

    @Query("SELECT c FROM ConsultaMedica c WHERE c.cita.medico.idMedico = :idMedico ORDER BY c.fechaConsulta DESC")
    List<ConsultaMedica> findByMedico(@Param("idMedico") Integer idMedico);

    @Query("SELECT c FROM ConsultaMedica c WHERE c.cita.paciente.idPaciente = :idPaciente ORDER BY c.fechaConsulta DESC")
    List<ConsultaMedica> findByPaciente(@Param("idPaciente") Integer idPaciente);
}
