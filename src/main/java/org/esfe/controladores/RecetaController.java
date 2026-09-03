package org.esfe.controladores;

import org.esfe.modelos.ConsultaMedica;
import org.esfe.modelos.Medicamento;
import org.esfe.modelos.Medico;
import org.esfe.modelos.RecetaDetalle;
import org.esfe.modelos.Usuario;
import org.esfe.servicios.interfaces.IConsultaMedicaService;
import org.esfe.servicios.interfaces.IMedicamentoService;
import org.esfe.servicios.interfaces.IMedicoService;
import org.esfe.servicios.interfaces.IRecetaDetalleService;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/recetas")
@PreAuthorize("hasAnyRole('MEDICO', 'RECEPCIONISTA', 'ADMINISTRADOR')")
public class RecetaController {

    private final IRecetaDetalleService recetaDetalleService;
    private final IConsultaMedicaService consultaMedicaService;
    private final IMedicamentoService medicamentoService;
    private final IMedicoService medicoService;

    public RecetaController(IRecetaDetalleService recetaDetalleService,
                             IConsultaMedicaService consultaMedicaService,
                             IMedicamentoService medicamentoService,
                             IMedicoService medicoService) {
        this.recetaDetalleService = recetaDetalleService;
        this.consultaMedicaService = consultaMedicaService;
        this.medicamentoService = medicamentoService;
        this.medicoService = medicoService;
    }

    @GetMapping
    public String listar(Model model) {
        List<RecetaDetalle> recetas = recetaDetalleService.obtenerTodos();
        Map<ConsultaMedica, List<RecetaDetalle>> agrupadas = agruparPorConsulta(recetas);
        model.addAttribute("activePage", "recetas");
        model.addAttribute("agrupadas", agrupadas);
        model.addAttribute("totalRecetas", recetas.size());
        return "recetas";
    }

    @GetMapping("/nueva")
    @PreAuthorize("hasRole('MEDICO')")
    public String nueva(@ModelAttribute("usuario") Usuario usuario,
                        @RequestParam(value = "idConsulta", required = false) Integer idConsulta,
                        Model model) {
        model.addAttribute("activePage", "recetas");
        model.addAttribute("medicamentos", medicamentoService.obtenerTodos());
        model.addAttribute("modo", "crear");

        if (idConsulta != null) {
            return consultaMedicaService.obtenerPorId(idConsulta)
                    .map(consulta -> {
                        model.addAttribute("consulta", consulta);
                        model.addAttribute("receta", new RecetaDetalle());
                        return "recetas-form";
                    })
                    .orElseGet(() -> {
                        List<ConsultaMedica> consultas = consultasDelMedico(usuario);
                        model.addAttribute("consultas", consultas);
                        model.addAttribute("sinConsultas", consultas.isEmpty());
                        return "recetas-form";
                    });
        }

        List<ConsultaMedica> consultas = consultasDelMedico(usuario);
        model.addAttribute("consultas", consultas);
        model.addAttribute("sinConsultas", consultas.isEmpty());
        return "recetas-form";
    }

    private List<ConsultaMedica> consultasDelMedico(Usuario usuario) {
        if (usuario == null) {
            return new ArrayList<>();
        }
        return medicoService.buscarPorUsuario(usuario.getIdUsuario())
                .map(medico -> consultaMedicaService.buscarPorMedico(medico.getIdMedico()))
                .orElseGet(ArrayList::new);
    }

    @PostMapping("/guardar")
    @PreAuthorize("hasRole('MEDICO')")
    public String guardar(@RequestParam(value = "idConsulta") Integer idConsulta,
                          @RequestParam(value = "medIds", required = false) List<Integer> medIds,
                          @RequestParam(value = "medDosis", required = false) List<String> medDosis,
                          @RequestParam(value = "medCantidad", required = false) List<Integer> medCantidad,
                          @RequestParam(value = "medFrecuencia", required = false) List<String> medFrecuencia,
                          @RequestParam(value = "medDuracion", required = false) List<String> medDuracion,
                          @RequestParam(value = "medIndicaciones", required = false) List<String> medIndicaciones,
                          RedirectAttributes redirectAttributes) {
        try {
            ConsultaMedica consulta = consultaMedicaService.obtenerPorId(idConsulta)
                    .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada con id: " + idConsulta));

            if (medIds == null || medIds.isEmpty()) {
                throw new IllegalArgumentException("Debe seleccionar al menos un medicamento.");
            }

            int guardados = 0;
            for (int i = 0; i < medIds.size(); i++) {
                Integer idMed = medIds.get(i);
                if (idMed == null) continue;

                Medicamento medicamento = medicamentoService.obtenerPorId(idMed)
                        .orElseThrow(() -> new IllegalArgumentException("Medicamento no encontrado con id: " + idMed));

                RecetaDetalle detalle = new RecetaDetalle();
                detalle.setConsulta(consulta);
                detalle.setMedicamento(medicamento);
                detalle.setDosis(medDosis != null && i < medDosis.size() ? medDosis.get(i) : null);
                detalle.setCantidad(medCantidad != null && i < medCantidad.size() ? medCantidad.get(i) : null);
                detalle.setFrecuencia(medFrecuencia != null && i < medFrecuencia.size() ? medFrecuencia.get(i) : null);
                detalle.setDuracion(medDuracion != null && i < medDuracion.size() ? medDuracion.get(i) : null);
                detalle.setIndicaciones(medIndicaciones != null && i < medIndicaciones.size() ? medIndicaciones.get(i) : null);
                recetaDetalleService.guardar(detalle);
                guardados++;
            }

            redirectAttributes.addFlashAttribute("exito",
                    guardados + " medicamento(s) agregado(s) a la receta correctamente.");
            return "redirect:/recetas/consulta/" + idConsulta;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/recetas/nueva?idConsulta=" + idConsulta;
        }
    }

    @GetMapping("/consulta/{idConsulta}")
    public String verPorConsulta(@PathVariable Integer idConsulta,
                                  @ModelAttribute("usuario") Usuario usuario,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        return consultaMedicaService.obtenerPorId(idConsulta)
                .map(consulta -> {
                    List<RecetaDetalle> detalles = recetaDetalleService.buscarPorConsulta(idConsulta);
                    model.addAttribute("activePage", "recetas");
                    model.addAttribute("consulta", consulta);
                    model.addAttribute("recetas", detalles);
                    model.addAttribute("totalDetalles", detalles.size());
                    return "recetas-detalle";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Consulta no encontrada.");
                    return "redirect:/recetas";
                });
    }

    @GetMapping("/consulta/{idConsulta}/agregar")
    @PreAuthorize("hasRole('MEDICO')")
    public String agregarMedicamento(@PathVariable Integer idConsulta, Model model,
                                     RedirectAttributes redirectAttributes) {
        return consultaMedicaService.obtenerPorId(idConsulta)
                .map(consulta -> {
                    model.addAttribute("activePage", "recetas");
                    model.addAttribute("receta", new RecetaDetalle());
                    model.addAttribute("consulta", consulta);
                    model.addAttribute("medicamentos", medicamentoService.obtenerTodos());
                    model.addAttribute("modo", "crear");
                    return "recetas-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Consulta no encontrada.");
                    return "redirect:/recetas";
                });
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasRole('MEDICO')")
    public String editar(@PathVariable Integer id, Model model,
                          RedirectAttributes redirectAttributes) {
        return recetaDetalleService.obtenerPorId(id)
                .map(receta -> {
                    model.addAttribute("activePage", "recetas");
                    model.addAttribute("receta", receta);
                    model.addAttribute("consulta", receta.getConsulta());
                    model.addAttribute("medicamentos", medicamentoService.obtenerTodos());
                    model.addAttribute("modo", "editar");
                    return "recetas-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Receta no encontrada.");
                    return "redirect:/recetas";
                });
    }

    @PostMapping("/{id}/editar")
    @PreAuthorize("hasRole('MEDICO')")
    public String editarGuardar(@PathVariable Integer id,
                                 @ModelAttribute("receta") RecetaDetalle receta,
                                 RedirectAttributes redirectAttributes) {
        try {
            RecetaDetalle existente = recetaDetalleService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada."));
            existente.setMedicamento(receta.getMedicamento());
            existente.setCantidad(receta.getCantidad());
            existente.setIndicaciones(receta.getIndicaciones());
            recetaDetalleService.guardar(existente);
            redirectAttributes.addFlashAttribute("exito", "Medicamento actualizado correctamente.");
            return "redirect:/recetas/consulta/" + existente.getConsulta().getIdConsulta();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/recetas/" + id + "/editar";
        }
    }

    @PostMapping("/{id}/dispensar")
    @PreAuthorize("hasAnyRole('RECEPCIONISTA', 'ADMINISTRADOR')")
    public String dispensar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            recetaDetalleService.dispensar(id);
            redirectAttributes.addFlashAttribute("exito", "Receta dispensada. Stock actualizado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/recetas";
    }

    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasRole('MEDICO')")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            RecetaDetalle receta = recetaDetalleService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada."));
            Integer idConsulta = receta.getConsulta().getIdConsulta();
            recetaDetalleService.eliminar(id);
            redirectAttributes.addFlashAttribute("exito", "Medicamento eliminado de la receta.");
            return "redirect:/recetas/consulta/" + idConsulta;
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/recetas";
        }
    }

    private Map<ConsultaMedica, List<RecetaDetalle>> agruparPorConsulta(List<RecetaDetalle> recetas) {
        Map<ConsultaMedica, List<RecetaDetalle>> resultado = new LinkedHashMap<>();
        for (RecetaDetalle detalle : recetas) {
            ConsultaMedica consulta = detalle.getConsulta();
            resultado.computeIfAbsent(consulta, k -> new ArrayList<>()).add(detalle);
        }
        return resultado;
    }
}
