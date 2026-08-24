package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Cita;
import org.esfe.modelos.Medico;
import org.esfe.modelos.enums.EstadoCita;
import org.esfe.repositorios.ICitaRepository;
import org.esfe.repositorios.IConsultaMedicaRepository;
import org.esfe.repositorios.IMedicoRepository;
import org.esfe.repositorios.IPacienteRepository;
import org.esfe.repositorios.IUsuarioRepository;
import org.esfe.servicios.interfaces.ICitaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaService implements ICitaService {

    private static final EstadoCita ESTADO_QUE_NO_BLOQUEA = EstadoCita.CANCELADA;

    private final ICitaRepository citaRepository;
    private final IMedicoRepository medicoRepository;
    private final IPacienteRepository pacienteRepository;
    private final IUsuarioRepository usuarioRepository;
    private final IConsultaMedicaRepository consultaMedicaRepository;

    public CitaService(ICitaRepository citaRepository,
            IMedicoRepository medicoRepository,
            IPacienteRepository pacienteRepository,
            IUsuarioRepository usuarioRepository,
            IConsultaMedicaRepository consultaMedicaRepository) {
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.consultaMedicaRepository = consultaMedicaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerTodos() {
        return citaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cita> obtenerPorId(Integer idCita) {
        return citaRepository.findById(idCita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorPaciente(Integer idPaciente) {
        return citaRepository.findByPacienteIdPaciente(idPaciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorMedico(Integer idMedico) {
        return citaRepository.findByMedicoIdMedico(idMedico);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorEstado(EstadoCita estado) {
        return citaRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorMedicoYEstado(Integer idMedico, EstadoCita estado) {
        return citaRepository.findByMedicoIdMedicoAndEstado(idMedico, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorPacienteYEstado(Integer idPaciente, EstadoCita estado) {
        return citaRepository.findByPacienteIdPacienteAndEstado(idPaciente, estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        validarRangoFechas(inicio, fin);
        return citaRepository.findByFechaHoraBetween(inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cita> obtenerAgendaMedico(Integer idMedico, LocalDateTime inicio, LocalDateTime fin) {
        validarRangoFechas(inicio, fin);
        return citaRepository.findByMedicoIdMedicoAndFechaHoraBetween(idMedico, inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean verificarDisponibilidad(Cita cita) {
        if (cita == null || cita.getMedico() == null || cita.getMedico().getIdMedico() == null) {
            return Boolean.FALSE;
        }
        Medico medico = medicoRepository.findById(cita.getMedico().getIdMedico())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Médico no encontrado con id: " + cita.getMedico().getIdMedico()));
        if (!cita.estaDisponible(medico, cita.getFechaHora(), cita.getDuracionMinutos())) {
            return Boolean.FALSE;
        }
        return !existeConflictoHorario(medico.getIdMedico(), cita.getFechaHora(), cita.getIdCita());
    }

    @Override
    @Transactional
    public Cita programarCita(Cita cita) {
        if (cita == null) {
            throw new IllegalArgumentException("Los datos de la cita son obligatorios.");
        }
        if (cita.getIdCita() != null) {
            throw new IllegalArgumentException("Para programar una cita no debe enviarse el identificador.");
        }
        if (cita.getPaciente() == null || cita.getPaciente().getIdPaciente() == null
                || !pacienteRepository.existsById(cita.getPaciente().getIdPaciente())) {
            throw new IllegalArgumentException("La cita debe estar asociada a un paciente válido.");
        }
        if (cita.getMedico() == null || cita.getMedico().getIdMedico() == null
                || !medicoRepository.existsById(cita.getMedico().getIdMedico())) {
            throw new IllegalArgumentException("La cita debe estar asociada a un médico válido.");
        }
        if (cita.getUsuarioGestor() == null || cita.getUsuarioGestor().getIdUsuario() == null
                || !usuarioRepository.existsById(cita.getUsuarioGestor().getIdUsuario())) {
            throw new IllegalArgumentException("La cita debe estar asociada a un usuario gestor válido.");
        }
        Medico medico = medicoRepository.findById(cita.getMedico().getIdMedico()).orElseThrow();
        if (!cita.estaDisponible(medico, cita.getFechaHora(), cita.getDuracionMinutos())) {
            throw new IllegalStateException(
                    "El médico no está disponible o la fecha/hora y duración de la cita son inválidas.");
        }
        if (existeConflictoHorario(medico.getIdMedico(), cita.getFechaHora(), cita.getIdCita())) {
            throw new IllegalStateException("El médico ya tiene una cita programada en esa fecha y hora.");
        }
        cita.programarCita();
        return citaRepository.save(cita);
    }

    @Override
    @Transactional
    public Cita reagendarCita(Integer idCita, LocalDateTime nuevaFechaHora, String motivo) {
        if (nuevaFechaHora == null) {
            throw new IllegalArgumentException("La nueva fecha y hora es obligatoria.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo del reagendamiento es obligatorio.");
        }
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + idCita));
        if (EstadoCita.CANCELADA.equals(cita.getEstado()) || EstadoCita.ATENDIDA.equals(cita.getEstado())
                || EstadoCita.NO_ASISTIO.equals(cita.getEstado())) {
            throw new IllegalStateException(
                    "No se puede reagendar una cita en estado " + cita.getEstado() + ".");
        }
        if (!cita.estaDisponible(cita.getMedico(), nuevaFechaHora, cita.getDuracionMinutos())) {
            throw new IllegalStateException("La nueva fecha/hora de la cita es inválida.");
        }
        if (existeConflictoHorario(cita.getMedico().getIdMedico(), nuevaFechaHora, cita.getIdCita())) {
            throw new IllegalStateException("El médico ya tiene una cita programada en la nueva fecha y hora.");
        }
        cita.reagendarCita(nuevaFechaHora, motivo);
        return citaRepository.save(cita);
    }

    @Override
    @Transactional
    public Cita cancelarCita(Integer idCita, String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("El motivo de la cancelación es obligatorio.");
        }
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + idCita));
        if (EstadoCita.ATENDIDA.equals(cita.getEstado()) || EstadoCita.NO_ASISTIO.equals(cita.getEstado())) {
            throw new IllegalStateException(
                    "No se puede cancelar una cita en estado " + cita.getEstado() + ".");
        }
        cita.cancelarCita(motivo);
        return citaRepository.save(cita);
    }

    @Override
    @Transactional
    public Boolean eliminarCita(Integer idCita) {
        if (idCita == null || !citaRepository.existsById(idCita)) {
            return Boolean.FALSE;
        }
        if (tieneConsultaAsociada(idCita)) {
            return Boolean.FALSE;
        }
        citaRepository.deleteById(idCita);
        return Boolean.TRUE;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean tieneConsultaAsociada(Integer idCita) {
        if (idCita == null) {
            return Boolean.FALSE;
        }
        return consultaMedicaRepository.existsByCitaIdCita(idCita);
    }

    private boolean existeConflictoHorario(Integer idMedico, LocalDateTime fechaHora, Integer idCitaExcluida) {
        return citaRepository
                .findByMedicoIdMedicoAndFechaHoraBetween(idMedico, fechaHora, fechaHora)
                .stream()
                .filter(otra -> idCitaExcluida == null || !idCitaExcluida.equals(otra.getIdCita()))
                .anyMatch(otra -> !ESTADO_QUE_NO_BLOQUEA.equals(otra.getEstado()));
    }

    private void validarRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null || fin == null || inicio.isAfter(fin)) {
            throw new IllegalArgumentException("El rango de fechas es inválido.");
        }
    }
}
