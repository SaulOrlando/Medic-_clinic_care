package org.esfe.controladores;

import org.esfe.modelos.CategoriaMedicamento;
import org.esfe.modelos.Medicamento;
import org.esfe.servicios.interfaces.IMedicamentoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medicamentos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'MEDICO', 'ENCARGADO_INVENTARIO')")
public class MedicamentoController {

    private final IMedicamentoService medicamentoService;

    public MedicamentoController(IMedicamentoService medicamentoService) {
        this.medicamentoService = medicamentoService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Medicamento> medicamentos = medicamentoService.obtenerTodos();
        List<Medicamento> stockBajo = medicamentoService.obtenerConStockBajo(10);
        List<Medicamento> vencidos = medicamentoService.obtenerVencidosAntesDe(LocalDate.now());
        List<CategoriaMedicamento> categorias = medicamentoService.obtenerTodos().stream()
                .map(m -> m.getCategoria())
                .distinct()
                .collect(Collectors.toList());

        model.addAttribute("activePage", "medicamentos");
        model.addAttribute("medicamentos", medicamentos);
        model.addAttribute("totalMedicamentos", medicamentos.size());
        model.addAttribute("stockBajo", stockBajo.size());
        model.addAttribute("vencidos", vencidos.size());
        model.addAttribute("totalCategorias", categorias.size());
        return "medicamentos";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("activePage", "medicamentos");
        model.addAttribute("medicamento", new Medicamento());
        model.addAttribute("categorias", medicamentoService.obtenerTodos().stream()
                .map(m -> m.getCategoria())
                .distinct()
                .collect(Collectors.toList()));
        return "medicamentos-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("medicamento") Medicamento medicamento,
                           RedirectAttributes redirectAttributes) {
        try {
            medicamentoService.crearMedicamento(medicamento);
            redirectAttributes.addFlashAttribute("exito", "Medicamento registrado correctamente.");
            return "redirect:/medicamentos";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/medicamentos";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return medicamentoService.obtenerPorId(id)
                .map(med -> {
                    model.addAttribute("activePage", "medicamentos");
                    model.addAttribute("medicamento", med);
                    model.addAttribute("categorias", medicamentoService.obtenerTodos().stream()
                            .map(m -> m.getCategoria())
                            .distinct()
                            .collect(Collectors.toList()));
                    return "medicamentos-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Medicamento no encontrado.");
                    return "redirect:/medicamentos";
                });
    }

    @PostMapping("/{id}/editar")
    public String editarGuardar(@PathVariable Integer id,
                                  @ModelAttribute("medicamento") Medicamento medicamento,
                                  RedirectAttributes redirectAttributes) {
        try {
            medicamento.setIdMedicamento(id);
            medicamentoService.editarMedicamento(medicamento);
            redirectAttributes.addFlashAttribute("exito", "Medicamento actualizado correctamente.");
            return "redirect:/medicamentos";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/medicamentos";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Boolean resultado = medicamentoService.eliminarMedicamento(id);
        if (Boolean.TRUE.equals(resultado)) {
            redirectAttributes.addFlashAttribute("exito", "Medicamento eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar un medicamento con registros asociados.");
        }
        return "redirect:/medicamentos";
    }
}
