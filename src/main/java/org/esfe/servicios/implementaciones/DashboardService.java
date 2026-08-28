package org.esfe.servicios.implementaciones;

import org.esfe.modelos.Cita;
import org.esfe.modelos.Medico;
import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.EstadoCita;
import org.esfe.modelos.enums.RolUsuario;
import org.esfe.repositorios.ICitaRepository;
import org.esfe.repositorios.IMedicoRepository;
import org.esfe.servicios.interfaces.IDashboardService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardService implements IDashboardService {

    private static final Set<EstadoCita> ESTADOS_VIGENTES = EnumSet.of(
            EstadoCita.PROGRAMADA, EstadoCita.REAGENDADA, EstadoCita.ATENDIDA
    );

    private static final Set<EstadoCita> ESTADOS_AGENDADOS = EnumSet.of(
            EstadoCita.PROGRAMADA, EstadoCita.REAGENDADA
    );

    private final ICitaRepository citaRepository;
    private final IMedicoRepository medicoRepository;

    public DashboardService(ICitaRepository citaRepository, IMedicoRepository medicoRepository) {
        this.citaRepository = citaRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    public long contarCitasHoy(Usuario usuario) {
        List<Cita> citas = citasEnRango(usuario, inicioDelDia(), finDelDia());
        return citas.stream()
                .filter(c -> ESTADOS_VIGENTES.contains(c.getEstado()))
                .count();
    }

    @Override
    public long contarPacientesRecientes(Usuario usuario) {
        List<Cita> citas = citasEnRango(usuario, inicioDeLaSemana(), finDelDia());
        return citas.stream()
                .map(c -> c.getPaciente().getIdPaciente())
                .distinct()
                .count();
    }

    @Override
    public long contarInformesPendientes(Usuario usuario) {
        List<Cita> citas = citasEnRango(usuario, inicioDelDia(), finDelDia());
        return citas.stream()
                .filter(c -> ESTADOS_VIGENTES.contains(c.getEstado()))
                .filter(c -> c.getConsultaMedica() == null)
                .count();
    }

    @Override
    public List<Long> contarVolumenSemanal(Usuario usuario) {
        LocalDateTime inicio = inicioDeLaSemana();
        List<Cita> citas = citasEnRango(usuario, inicio, inicio.plusDays(7));
        Map<DayOfWeek, Long> porDia = citas.stream()
                .collect(Collectors.groupingBy(c -> c.getFechaHora().getDayOfWeek(), Collectors.counting()));

        List<Long> volumen = new ArrayList<>(5);
        for (DayOfWeek dia : List.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)) {
            volumen.add(porDia.getOrDefault(dia, 0L));
        }
        return volumen;
    }

    @Override
    public List<Cita> obtenerProximasCitas(Usuario usuario, int limite) {
        LocalDateTime ahora = LocalDateTime.now();
        List<Cita> citas = citasEnRango(usuario, ahora, ahora.plusDays(30));
        return citas.stream()
                .filter(c -> ESTADOS_AGENDADOS.contains(c.getEstado()))
                .sorted(Comparator.comparing(Cita::getFechaHora))
                .limit(limite)
                .collect(Collectors.toList());
    }

    private List<Cita> citasEnRango(Usuario usuario, LocalDateTime inicio, LocalDateTime fin) {
        Optional<Integer> idMedico = idMedicoDelUsuario(usuario);
        if (esMedicoSinRegistro(usuario, idMedico)) {
            return List.of();
        }
        return idMedico
                .map(id -> citaRepository.findByMedicoIdMedicoAndFechaHoraBetween(id, inicio, fin))
                .orElseGet(() -> citaRepository.findByFechaHoraBetween(inicio, fin));
    }

    private Optional<Integer> idMedicoDelUsuario(Usuario usuario) {
        if (usuario == null || usuario.getRol() != RolUsuario.MEDICO) {
            return Optional.empty();
        }
        return medicoRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .map(Medico::getIdMedico);
    }

    private boolean esMedicoSinRegistro(Usuario usuario, Optional<Integer> idMedico) {
        return usuario != null
                && usuario.getRol() == RolUsuario.MEDICO
                && idMedico.isEmpty();
    }

    private LocalDateTime inicioDelDia() {
        return LocalDate.now().atStartOfDay();
    }

    private LocalDateTime finDelDia() {
        return LocalDate.now().plusDays(1).atStartOfDay();
    }

    private LocalDateTime inicioDeLaSemana() {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay();
    }
}