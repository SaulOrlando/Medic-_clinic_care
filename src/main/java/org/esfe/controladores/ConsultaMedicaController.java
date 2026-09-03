package org.esfe.controladores;

import org.esfe.modelos.Cita;
import org.esfe.modelos.ConsultaMedica;
import org.esfe.modelos.Medico;
import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.EstadoCita;
import org.esfe.servicios.interfaces.ICitaService;
import org.esfe.servicios.interfaces.IConsultaMedicaService;
import org.esfe.servicios.interfaces.IMedicoService;
import jakarta.validation.ConstraintViolationException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/consultas")
@PreAuthorize("hasAnyRole('MEDICO', 'ADMINISTRADOR')")
public class ConsultaMedicaController {

    private final IConsultaMedicaService consultaMedicaService;
    private final ICitaService citaService;
    private final IMedicoService medicoService;

    public ConsultaMedicaController(IConsultaMedicaService consultaMedicaService,
                                     ICitaService citaService,
                                     IMedicoService medicoService) {
        this.consultaMedicaService = consultaMedicaService;
        this.citaService = citaService;
        this.medicoService = medicoService;
    }

    @GetMapping
    public String listar(@ModelAttribute("usuario") Usuario usuario,
                         @RequestParam(value = "busqueda", required = false) String busqueda,
                         Model model) {
        List<ConsultaMedica> consultas = consultasDelMedico(usuario, busqueda);
        model.addAttribute("activePage", "consultas");
        model.addAttribute("consultas", consultas);
        model.addAttribute("totalConsultas", consultas.size());
        model.addAttribute("citasPendientes", citasPendientes(usuario));
        model.addAttribute("busqueda", busqueda);
        return "consultas";
    }

    private List<ConsultaMedica> consultasDelMedico(Usuario usuario, String busqueda) {
        if (usuario != null && RolEs(usuario, "MEDICO")) {
            return medicoService.buscarPorUsuario(usuario.getIdUsuario())
                    .map(medico -> consultaMedicaService.buscarPorMedicoYBusqueda(medico.getIdMedico(), busqueda))
                    .orElseGet(ArrayList::new);
        }
        return consultaMedicaService.buscarPorBusqueda(busqueda);
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable Integer id, Model model,
                      RedirectAttributes redirectAttributes) {
        return consultaMedicaService.obtenerPorId(id)
                .map(consulta -> {
                    model.addAttribute("activePage", "consultas");
                    model.addAttribute("consulta", consulta);
                    model.addAttribute("cita", consulta.getCita());
                    model.addAttribute("recetas", consulta.getRecetasDetalle());
                    return "consultas-detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Consulta no encontrada.");
                    return "redirect:/consultas";
                });
    }

    private List<Cita> citasPendientes(Usuario usuario) {
        List<Cita> pendientes = new ArrayList<>();
        List<EstadoCita> estados = List.of(EstadoCita.PROGRAMADA, EstadoCita.REAGENDADA);

        Integer idMedico = null;
        if (usuario != null && RolEs(usuario, "MEDICO")) {
            idMedico = medicoService.buscarPorUsuario(usuario.getIdUsuario())
                    .map(Medico::getIdMedico).orElse(null);
        }

        for (EstadoCita estado : estados) {
            List<Cita> citas = idMedico != null
                    ? citaService.obtenerPorMedicoYEstado(idMedico, estado)
                    : citaService.obtenerPorEstado(estado);
            for (Cita cita : citas) {
                if (cita.getConsultaMedica() == null) {
                    pendientes.add(cita);
                }
            }
        }
        return pendientes;
    }

    private boolean RolEs(Usuario usuario, String rol) {
        return usuario.getRol() != null && rol.equals(usuario.getRol().name());
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
                          @RequestParam(value = "emitirReceta", defaultValue = "false") boolean emitirReceta,
                          RedirectAttributes redirectAttributes) {
        try {
            ConsultaMedica guardada = consultaMedicaService.registrarConsulta(consulta);
            if (emitirReceta) {
                return "redirect:/recetas/consulta/" + guardada.getIdConsulta() + "/agregar";
            }
            redirectAttributes.addFlashAttribute("exito", "Consulta registrada correctamente. La cita ha sido marcada como atendida.");
            return "redirect:/consultas";
        } catch (IllegalArgumentException | IllegalStateException | ConstraintViolationException ex) {
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
            existente.setPlanTratamiento(consulta.getPlanTratamiento());

            consultaMedicaService.guardar(existente);
            redirectAttributes.addFlashAttribute("exito", "Consulta actualizada correctamente.");
            return "redirect:/consultas";
        } catch (IllegalArgumentException | IllegalStateException | ConstraintViolationException ex) {
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
