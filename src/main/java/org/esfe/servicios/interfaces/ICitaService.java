package org.esfe.servicios.interfaces;

import org.esfe.modelos.Cita;
import org.esfe.modelos.enums.EstadoCita;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ICitaService {

    List<Cita> obtenerTodos();

    Optional<Cita> obtenerPorId(Integer idCita);

    List<Cita> obtenerPorPaciente(Integer idPaciente);

    List<Cita> obtenerPorMedico(Integer idMedico);

    List<Cita> obtenerPorEstado(EstadoCita estado);

    List<Cita> obtenerPorMedicoYEstado(Integer idMedico, EstadoCita estado);

    List<Cita> obtenerPorPacienteYEstado(Integer idPaciente, EstadoCita estado);

    List<Cita> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);

    List<Cita> obtenerAgendaMedico(Integer idMedico, LocalDateTime inicio, LocalDateTime fin);

    Boolean verificarDisponibilidad(Cita cita);

    Cita programarCita(Cita cita);

    Cita reagendarCita(Integer idCita, LocalDateTime nuevaFechaHora, String motivo);

    Cita cancelarCita(Integer idCita, String motivo);

    Boolean eliminarCita(Integer idCita);

    Boolean tieneConsultaAsociada(Integer idCita);
}
