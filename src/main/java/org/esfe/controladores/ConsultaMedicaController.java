package org.esfe.controladores;

import org.esfe.modelos.Cita;
import org.esfe.modelos.ConsultaMedica;
import org.esfe.modelos.enums.EstadoCita;
import org.esfe.servicios.interfaces.ICitaService;
import org.esfe.servicios.interfaces.IConsultaMedicaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/consultas")
@PreAuthorize("hasAnyRole('MEDICO', 'ADMINISTRADOR')")
public class ConsultaMedicaController {

    private final IConsultaMedicaService consultaMedicaService;
    private final ICitaService citaService;

    public ConsultaMedicaController(IConsultaMedicaService consultaMedicaService,
                                     ICitaService citaService) {
        this.consultaMedicaService = consultaMedicaService;
        this.citaService = citaService;
    }

    @GetMapping
    public String listar(Model model) {
        List<ConsultaMedica> consultas = consultaMedicaService.obtenerTodos();
        model.addAttribute("activePage", "consultas");
        model.addAttribute("consultas", consultas);
        model.addAttribute("totalConsultas", consultas.size());
        return "consultas";
    }

    @GetMapping("/nueva")
    public String nueva(@RequestParam("idCita") Integer idCita, Model model,
                         RedirectAttributes redirectAttributes) {
        Optional<Cita> citaOpt = citaService.obtenerPorId(idCita);
        if (citaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cita no encontrada.");
            return "redirect:/consultas";
        }

        Cita cita = citaOpt.get();

        if (cita.getConsultaMedica() != null) {
            redirectAttributes.addFlashAttribute("error", "Esta cita ya tiene una consulta registrada.");
            return "redirect:/consultas";
        }

        if (!EstadoCita.PROGRAMADA.equals(cita.getEstado())
                && !EstadoCita.REAGENDADA.equals(cita.getEstado())) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede iniciar una consulta para una cita en estado " + cita.getEstado() + ".");
            return "redirect:/consultas";
        }

        ConsultaMedica consulta = new ConsultaMedica();
        consulta.setCita(cita);

        model.addAttribute("activePage", "consultas");
        model.addAttribute("consulta", consulta);
        model.addAttribute("cita", cita);
        model.addAttribute("modo", "crear");
        return "consultas-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("consulta") ConsultaMedica consulta,
                           RedirectAttributes redirectAttributes) {
        try {
            consultaMedicaService.registrarConsulta(consulta);
            redirectAttributes.addFlashAttribute("exito", "Consulta registrada correctamente. La cita ha sido marcada como atendida.");
            return "redirect:/consultas";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            if (consulta.getCita() != null && consulta.getCita().getIdCita() != null) {
                return "redirect:/consultas/nueva?idCita=" + consulta.getCita().getIdCita();
            }
            return "redirect:/consultas";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model,
                          RedirectAttributes redirectAttributes) {
        return consultaMedicaService.obtenerPorId(id)
                .map(consulta -> {
                    model.addAttribute("activePage", "consultas");
                    model.addAttribute("consulta", consulta);
                    model.addAttribute("cita", consulta.getCita());
                    model.addAttribute("modo", "editar");
                    return "consultas-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Consulta no encontrada.");
                    return "redirect:/consultas";
                });
    }

    @PostMapping("/{id}/editar")
    public String editarGuardar(@PathVariable Integer id,
                                 @ModelAttribute("consulta") ConsultaMedica consulta,
                                 RedirectAttributes redirectAttributes) {
        try {
            ConsultaMedica existente = consultaMedicaService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada."));

            existente.setMotivoConsulta(consulta.getMotivoConsulta());
            existente.setSintomatologia(consulta.getSintomatologia());
            existente.setDiagnostico(consulta.getDiagnostico());

            consultaMedicaService.guardar(existente);
            redirectAttributes.addFlashAttribute("exito", "Consulta actualizada correctamente.");
            return "redirect:/consultas";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/consultas/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            consultaMedicaService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Consulta eliminada correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/consultas";
    }
}
