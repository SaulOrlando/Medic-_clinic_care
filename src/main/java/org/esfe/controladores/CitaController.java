package org.esfe.controladores;

import org.esfe.modelos.Cita;
import org.esfe.modelos.Medico;
import org.esfe.modelos.Paciente;
import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.EstadoCita;
import org.esfe.servicios.interfaces.ICitaService;
import org.esfe.servicios.interfaces.IMedicoService;
import org.esfe.servicios.interfaces.IPacienteService;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/citas")
@PreAuthorize("hasAnyRole('RECEPCIONISTA', 'MEDICO', 'ADMINISTRADOR')")
public class CitaController {

    private final ICitaService citaService;
    private final IPacienteService pacienteService;
    private final IMedicoService medicoService;
    private final IUsuarioService usuarioService;

    public CitaController(ICitaService citaService,
                          IPacienteService pacienteService,
                          IMedicoService medicoService,
                          IUsuarioService usuarioService) {
        this.citaService = citaService;
        this.pacienteService = pacienteService;
        this.medicoService = medicoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String estado,
                         @RequestParam(required = false) Integer idMedico,
                         Model model, Authentication authentication) {
        List<Cita> citas;

        boolean esMedico = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MEDICO"));

        if (esMedico && idMedico == null) {
            Optional<Medico> medicoOpt = medicoService.buscarPorUsuario(obtenerIdUsuario(authentication));
            if (medicoOpt.isPresent()) {
                idMedico = medicoOpt.get().getIdMedico();
            }
        }

        if (idMedico != null && estado != null && !estado.isEmpty()) {
            try {
                EstadoCita estadoEnum = EstadoCita.valueOf(estado);
                citas = citaService.obtenerPorMedicoYEstado(idMedico, estadoEnum);
            } catch (IllegalArgumentException e) {
                citas = citaService.obtenerPorMedico(idMedico);
            }
        } else if (idMedico != null) {
            citas = citaService.obtenerPorMedico(idMedico);
        } else if (estado != null && !estado.isEmpty()) {
            try {
                EstadoCita estadoEnum = EstadoCita.valueOf(estado);
                citas = citaService.obtenerPorEstado(estadoEnum);
            } catch (IllegalArgumentException e) {
                citas = citaService.obtenerTodos();
            }
        } else {
            citas = citaService.obtenerTodos();
        }

        model.addAttribute("activePage", "citas");
        model.addAttribute("citas", citas);
        model.addAttribute("totalCitas", citas.size());
        model.addAttribute("filtroEstado", estado);
        model.addAttribute("filtroMedico", idMedico);
        model.addAttribute("estadosCita", EstadoCita.values());
        return "citas";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("activePage", "citas");
        model.addAttribute("cita", new Cita());
        model.addAttribute("pacientes", pacienteService.obtenerTodos());
        model.addAttribute("medicos", medicoService.obtenerTodos());
        model.addAttribute("modo", "crear");
        return "citas-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("cita") Cita cita,
                          @RequestParam("idPaciente") Integer idPaciente,
                          @RequestParam("idMedico") Integer idMedico,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {
        try {
            Paciente paciente = pacienteService.obtenerPorId(idPaciente)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado."));
            Medico medico = medicoService.obtenerPorId(idMedico)
                    .orElseThrow(() -> new IllegalArgumentException("Medico no encontrado."));

            Integer idUsuario = obtenerIdUsuario(authentication);
            Usuario usuarioGestor = usuarioService.obtenerPorId(idUsuario)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario gestor no encontrado."));

            cita.setPaciente(paciente);
            cita.setMedico(medico);
            cita.setUsuarioGestor(usuarioGestor);

            citaService.programarCita(cita);
            redirectAttributes.addFlashAttribute("exito", "Cita programada correctamente.");
            return "redirect:/citas";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/citas/nueva";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model,
                         RedirectAttributes redirectAttributes) {
        return citaService.obtenerPorId(id)
                .map(cita -> {
                    model.addAttribute("activePage", "citas");
                    model.addAttribute("cita", cita);
                    model.addAttribute("pacientes", pacienteService.obtenerTodos());
                    model.addAttribute("medicos", medicoService.obtenerTodos());
                    model.addAttribute("modo", "editar");
                    return "citas-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Cita no encontrada.");
                    return "redirect:/citas";
                });
    }

    @PostMapping("/{id}/editar")
    public String editarGuardar(@PathVariable Integer id,
                                @ModelAttribute("cita") Cita citaForm,
                                @RequestParam("idPaciente") Integer idPaciente,
                                @RequestParam("idMedico") Integer idMedico,
                                RedirectAttributes redirectAttributes) {
        try {
            Cita existente = citaService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada."));

            Paciente paciente = pacienteService.obtenerPorId(idPaciente)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado."));
            Medico medico = medicoService.obtenerPorId(idMedico)
                    .orElseThrow(() -> new IllegalArgumentException("Medico no encontrado."));

            existente.setPaciente(paciente);
            existente.setMedico(medico);
            existente.setFechaHora(citaForm.getFechaHora());
            existente.setDuracionMinutos(citaForm.getDuracionMinutos());

            citaService.programarCita(existente);
            redirectAttributes.addFlashAttribute("exito", "Cita actualizada correctamente.");
            return "redirect:/citas";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/citas/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Integer id,
                           @RequestParam(value = "motivo", defaultValue = "Cancelacion solicitada por usuario") String motivo,
                           RedirectAttributes redirectAttributes) {
        try {
            citaService.cancelarCita(id, motivo);
            redirectAttributes.addFlashAttribute("exito", "Cita cancelada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/citas";
    }

    @PostMapping("/{id}/reagendar")
    public String reagendar(@PathVariable Integer id,
                            @RequestParam("nuevaFecha") String nuevaFecha,
                            @RequestParam(value = "motivo", defaultValue = "Reagendamiento solicitado") String motivo,
                            RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime nuevaFechaHora = LocalDateTime.parse(nuevaFecha);
            citaService.reagendarCita(id, nuevaFechaHora, motivo);
            redirectAttributes.addFlashAttribute("exito", "Cita reagendada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/citas";
    }

    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Boolean eliminada = citaService.eliminarCita(id);
            if (eliminada) {
                redirectAttributes.addFlashAttribute("exito", "Cita eliminada correctamente.");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede eliminar la cita. Puede tener una consulta asociada.");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/citas";
    }

    private Integer obtenerIdUsuario(Authentication authentication) {
        String correo = authentication.getName();
        return usuarioService.buscarPorCorreo(correo)
                .map(Usuario::getIdUsuario)
                .orElse(null);
    }
}
