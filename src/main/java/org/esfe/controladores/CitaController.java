package org.esfe.controladores;

import org.esfe.modelos.Cita;
import org.esfe.modelos.HistorialCita;
import org.esfe.modelos.Medico;
import org.esfe.modelos.Paciente;
import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.EstadoCita;
import org.esfe.modelos.enums.RolUsuario;
import org.esfe.servicios.interfaces.ICitaService;
import org.esfe.servicios.interfaces.IHistorialCitaService;
import org.esfe.servicios.interfaces.IMedicoService;
import org.esfe.servicios.interfaces.IPacienteService;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@PreAuthorize("hasAnyRole('ADMINISTRADOR','MEDICO','RECEPCIONISTA')")
public class CitaController {

    private static final DateTimeFormatter MES_ES = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es"));

    private final ICitaService citaService;
    private final IMedicoService medicoService;
    private final IPacienteService pacienteService;
    private final IHistorialCitaService historialCitaService;
    private final IUsuarioService usuarioService;

    public CitaController(ICitaService citaService,
                          IMedicoService medicoService,
                          IPacienteService pacienteService,
                          IHistorialCitaService historialCitaService,
                          IUsuarioService usuarioService) {
        this.citaService = citaService;
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
        this.historialCitaService = historialCitaService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/citas")
    public String agenda(
            @ModelAttribute("usuario") Usuario usuario,
            @RequestParam(name = "vista", required = false) String vistaParam,
            @RequestParam(name = "semana", required = false) String semanaParam,
            @RequestParam(name = "mes", required = false) String mesParam,
            @RequestParam(name = "medico", required = false) Integer medicoParam,
            Model model) {

        boolean vistaMes = "mes".equalsIgnoreCase(vistaParam);
        boolean usuarioMedico = esMedico(usuario);

        // El médico solo puede ver su propia agenda: se fuerza su id de médico.
        Integer idMedico = usuarioMedico ? idMedicoDelUsuario(usuario) : medicoParam;

        List<Medico> medicos = medicoService.obtenerTodos();
        Medico filtro = idMedico != null
                ? medicos.stream().filter(m -> m.getIdMedico().equals(idMedico)).findFirst().orElse(null)
                : null;

        model.addAttribute("activePage", "citas");
        model.addAttribute("medicos", medicos);
        model.addAttribute("pacientes", pacienteService.obtenerTodos());
        model.addAttribute("idMedicoFiltro", idMedico);
        model.addAttribute("filtroMedico", filtro);
        model.addAttribute("vista", vistaMes ? "mes" : "semana");
        model.addAttribute("esMedico", usuarioMedico);

        if (vistaMes) {
            cargarVistaMes(mesParam, idMedico, model);
        } else {
            cargarVistaSemana(semanaParam, idMedico, model);
        }
        return "citas";
    }

    private void cargarVistaSemana(String semanaParam, Integer idMedico, Model model) {
        LocalDate lunesBase = LocalDate.now().with(DayOfWeek.MONDAY);
        if (semanaParam != null && !semanaParam.isBlank()) {
            try {
                lunesBase = LocalDate.parse(semanaParam).with(DayOfWeek.MONDAY);
            } catch (Exception ignorada) {
                // Se mantiene la semana actual si el parámetro es inválido.
            }
        }

        LocalDate domingo = lunesBase.plusDays(6);
        LocalDateTime inicio = lunesBase.atStartOfDay();
        LocalDateTime fin = domingo.atTime(LocalTime.MAX);

        List<Cita> citas = idMedico != null
                ? citaService.obtenerAgendaMedico(idMedico, inicio, fin)
                : citaService.obtenerPorRangoFechas(inicio, fin);
        Map<LocalDate, List<Cita>> citasPorDia = agruparPorDia(citas);

        model.addAttribute("citasPorDia", citasPorDia);
        model.addAttribute("citasSemana", citas);
        model.addAttribute("lunes", lunesBase);
        model.addAttribute("domingo", domingo);
        model.addAttribute("semanaActual", LocalDate.now().with(DayOfWeek.MONDAY).equals(lunesBase));
        model.addAttribute("etiquetaSemana", formatearRango(lunesBase, domingo));
    }

    private void cargarVistaMes(String mesParam, Integer idMedico, Model model) {
        YearMonth anioMes = YearMonth.now();
        if (mesParam != null && !mesParam.isBlank()) {
            try {
                anioMes = YearMonth.parse(mesParam);
            } catch (Exception ignorada) {
                // Se mantiene el mes actual si el parámetro es inválido.
            }
        }

        LocalDate inicioMes = anioMes.atDay(1);
        LocalDate finMes = anioMes.atEndOfMonth();
        LocalDateTime inicio = inicioMes.atStartOfDay();
        LocalDateTime fin = finMes.atTime(LocalTime.MAX);

        List<Cita> citas = idMedico != null
                ? citaService.obtenerAgendaMedico(idMedico, inicio, fin)
                : citaService.obtenerPorRangoFechas(inicio, fin);
        Map<LocalDate, List<Cita>> citasPorDia = agruparPorDia(citas);

        LocalDate inicioGrilla = inicioMes.with(DayOfWeek.MONDAY);
        List<List<LocalDate>> semanas = new ArrayList<>();
        LocalDate cursor = inicioGrilla;
        for (int s = 0; s < 6; s++) {
            List<LocalDate> semana = new ArrayList<>();
            for (int d = 0; d < 7; d++) {
                semana.add(cursor);
                cursor = cursor.plusDays(1);
            }
            semanas.add(semana);
        }

        model.addAttribute("semanasMes", semanas);
        model.addAttribute("citasPorDiaMes", citasPorDia);
        model.addAttribute("mesBase", anioMes);
        model.addAttribute("mesActual", YearMonth.now().equals(anioMes));
        model.addAttribute("etiquetaMes", capitalizar(inicioMes.format(MES_ES)));
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) {
            return texto;
        }
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1);
    }

    @PostMapping("/citas/guardar")
    public String programar(@RequestParam("idPaciente") Integer idPaciente,
                            @RequestParam("idMedico") Integer idMedico,
                            @RequestParam("fechaHora") String fechaHora,
                            @RequestParam("duracionMinutos") Integer duracionMinutos,
                            @ModelAttribute("usuario") Usuario usuario,
                            RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime fecha = LocalDateTime.parse(fechaHora);
            Paciente paciente = pacienteService.obtenerPorId(idPaciente)
                    .orElseThrow(() -> new IllegalArgumentException("Seleccione un paciente válido."));

            // El médico siempre agenda para sí mismo; no puede elegir a otro médico.
            Medico medico;
            if (esMedico(usuario)) {
                medico = medicoService.buscarPorUsuario(usuario.getIdUsuario())
                        .orElseThrow(() -> new IllegalArgumentException("No se encontró su perfil de médico."));
            } else {
                medico = medicoService.obtenerPorId(idMedico)
                        .orElseThrow(() -> new IllegalArgumentException("Seleccione un médico válido."));
            }

            Cita cita = new Cita();
            cita.setPaciente(paciente);
            cita.setMedico(medico);
            cita.setUsuarioGestor(usuario);
            cita.setFechaHora(fecha);
            cita.setDuracionMinutos(duracionMinutos);

            citaService.programarCita(cita);
            redirectAttributes.addFlashAttribute("exito", "Cita programada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/citas";
    }

    @PostMapping("/citas/{id}/cancelar")
    public String cancelar(@PathVariable("id") Integer idCita,
                           @RequestParam("motivo") String motivo,
                           @ModelAttribute("usuario") Usuario usuario,
                           RedirectAttributes redirectAttributes) {
        try {
            Cita cita = citaService.obtenerPorId(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("La cita no existe."));
            validarAccesoMedico(cita, usuario);
            EstadoCita estadoAnterior = cita.getEstado();
            Cita actualizada = citaService.cancelarCita(idCita, motivo);
            registrarHistorial(actualizada, estadoAnterior, EstadoCita.CANCELADA, motivo, usuario);
            redirectAttributes.addFlashAttribute("exito", "Cita cancelada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/citas";
    }

    @PostMapping("/citas/{id}/reagendar")
    public String reagendar(@PathVariable("id") Integer idCita,
                            @RequestParam("nuevaFechaHora") String nuevaFechaHora,
                            @RequestParam("motivo") String motivo,
                            @ModelAttribute("usuario") Usuario usuario,
                            RedirectAttributes redirectAttributes) {
        try {
            Cita cita = citaService.obtenerPorId(idCita)
                    .orElseThrow(() -> new IllegalArgumentException("La cita no existe."));
            validarAccesoMedico(cita, usuario);
            EstadoCita estadoAnterior = cita.getEstado();
            LocalDateTime fecha = LocalDateTime.parse(nuevaFechaHora);
            Cita actualizada = citaService.reagendarCita(idCita, fecha, motivo);
            registrarHistorial(actualizada, estadoAnterior, EstadoCita.REAGENDADA, motivo, usuario);
            redirectAttributes.addFlashAttribute("exito", "Cita reprogramada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/citas";
    }

    private boolean esMedico(Usuario usuario) {
        return usuario != null && usuario.getRol() == RolUsuario.MEDICO;
    }

    private Integer idMedicoDelUsuario(Usuario usuario) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            return null;
        }
        return medicoService.buscarPorUsuario(usuario.getIdUsuario())
                .map(Medico::getIdMedico)
                .orElse(null);
    }

    private void validarAccesoMedico(Cita cita, Usuario usuario) {
        if (!esMedico(usuario)) {
            return;
        }
        Medico medicoActual = medicoService.buscarPorUsuario(usuario.getIdUsuario()).orElse(null);
        if (medicoActual == null || cita == null || cita.getMedico() == null
                || !cita.getMedico().getIdMedico().equals(medicoActual.getIdMedico())) {
            throw new IllegalStateException("Solo puede gestionar sus propias citas.");
        }
    }

    private void registrarHistorial(Cita cita,
                                    EstadoCita estadoAnterior,
                                    EstadoCita estadoNuevo,
                                    String motivo,
                                    Usuario usuario) {
        if (usuario == null || cita == null || cita.getIdCita() == null) {
            return;
        }
        HistorialCita historial = new HistorialCita();
        historial.setCita(cita);
        historial.setUsuario(usuario);
        historial.setEstadoAnterior(estadoAnterior);
        historial.setEstadoNuevo(estadoNuevo);
        historial.setMotivo(motivo);
        historialCitaService.guardar(historial);
    }

    private Map<LocalDate, List<Cita>> agruparPorDia(List<Cita> citas) {
        Map<LocalDate, List<Cita>> resultado = new HashMap<>();
        for (Cita cita : citas) {
            LocalDate dia = cita.getFechaHora().toLocalDate();
            resultado.computeIfAbsent(dia, k -> new ArrayList<>()).add(cita);
        }
        for (List<Cita> lista : resultado.values()) {
            lista.sort(Comparator.comparing(Cita::getFechaHora));
        }
        return resultado;
    }

    private String formatearRango(LocalDate lunes, LocalDate domingo) {
        DateTimeFormatter mes = DateTimeFormatter.ofPattern("MMM yyyy", new Locale("es"));
        return lunes.getDayOfMonth() + " " + lunes.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es"))
                + " - " + domingo.getDayOfMonth() + " " + domingo.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es"))
                + ", " + domingo.getYear();
    }
}
