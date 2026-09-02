package org.esfe.repositorios;

import org.esfe.modelos.ConsultaMedica;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"cita", "cita.paciente", "cita.medico", "cita.medico.usuario"})
    @Query("SELECT c FROM ConsultaMedica c WHERE c.cita.medico.idMedico = :idMedico ORDER BY c.fechaConsulta DESC")
    List<ConsultaMedica> findByMedico(@Param("idMedico") Integer idMedico);

    @EntityGraph(attributePaths = {"cita", "cita.paciente", "cita.medico", "cita.medico.usuario"})
    @Query("SELECT c FROM ConsultaMedica c WHERE c.cita.paciente.idPaciente = :idPaciente ORDER BY c.fechaConsulta DESC")
    List<ConsultaMedica> findByPaciente(@Param("idPaciente") Integer idPaciente);

    @EntityGraph(attributePaths = {"cita", "cita.paciente", "cita.medico", "cita.medico.usuario"})
    @Query("SELECT c FROM ConsultaMedica c ORDER BY c.fechaConsulta DESC")
    List<ConsultaMedica> findAllWithDetails();

    @EntityGraph(attributePaths = {"cita", "cita.paciente", "cita.medico", "cita.medico.usuario"})
    @Query("SELECT c FROM ConsultaMedica c WHERE c.cita.medico.idMedico = :idMedico AND (LOWER(c.cita.paciente.nombres) LIKE LOWER(CONCAT('%',:busqueda,'%')) OR LOWER(c.cita.paciente.apellidos) LIKE LOWER(CONCAT('%',:busqueda,'%')) OR LOWER(c.cita.paciente.codigoExpediente) LIKE LOWER(CONCAT('%',:busqueda,'%')) OR LOWER(c.motivoConsulta) LIKE LOWER(CONCAT('%',:busqueda,'%')) OR LOWER(c.diagnostico) LIKE LOWER(CONCAT('%',:busqueda,'%'))) ORDER BY c.fechaConsulta DESC")
    List<ConsultaMedica> buscarPorMedicoYBusqueda(@Param("idMedico") Integer idMedico, @Param("busqueda") String busqueda);

    @EntityGraph(attributePaths = {"cita", "cita.paciente", "cita.medico", "cita.medico.usuario"})
    @Query("SELECT c FROM ConsultaMedica c WHERE (LOWER(c.cita.paciente.nombres) LIKE LOWER(CONCAT('%',:busqueda,'%')) OR LOWER(c.cita.paciente.apellidos) LIKE LOWER(CONCAT('%',:busqueda,'%')) OR LOWER(c.cita.paciente.codigoExpediente) LIKE LOWER(CONCAT('%',:busqueda,'%')) OR LOWER(c.motivoConsulta) LIKE LOWER(CONCAT('%',:busqueda,'%')) OR LOWER(c.diagnostico) LIKE LOWER(CONCAT('%',:busqueda,'%'))) ORDER BY c.fechaConsulta DESC")
    List<ConsultaMedica> buscarPorBusqueda(@Param("busqueda") String busqueda);
}
