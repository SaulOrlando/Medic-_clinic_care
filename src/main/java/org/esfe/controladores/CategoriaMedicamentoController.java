package org.esfe.controladores;

import org.esfe.modelos.CategoriaMedicamento;
import org.esfe.servicios.interfaces.ICategoriaMedicamentoService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categorias-medicamentos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'MEDICO')")
public class CategoriaMedicamentoController {

    private final ICategoriaMedicamentoService categoriaMedicamentoService;

    public CategoriaMedicamentoController(ICategoriaMedicamentoService categoriaMedicamentoService) {
        this.categoriaMedicamentoService = categoriaMedicamentoService;
    }

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
        model.addAttribute("activePage", "categorias");
        return "categorias-medicamentos";
    }

    @GetMapping("/nuevo")
    public String mostrarNuevo(Model model) {
        model.addAttribute("activePage", "categorias");
        model.addAttribute("categoria", new CategoriaMedicamento());
        model.addAttribute("modo", "crear");
        return "categorias-form";
    }

    @PostMapping
    public String crearCategoria(@Valid @ModelAttribute CategoriaMedicamento categoria,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categorias-form";
        }
        try {
            categoriaMedicamentoService.crearCategoria(categoria);
            redirectAttributes.addFlashAttribute("exito", "Categoría creada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "categorias-form";
        }
        return "redirect:/categorias-medicamentos";
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        CategoriaMedicamento categoria = categoriaMedicamentoService.obtenerPorId(id)
                .orElse(null);
        if (categoria == null) {
            redirectAttributes.addFlashAttribute("error", "Categoría no encontrada.");
            return "redirect:/categorias-medicamentos";
        }
        model.addAttribute("activePage", "categorias");
        model.addAttribute("categoria", categoria);
        model.addAttribute("modo", "editar");
        return "categorias-form";
    }

    @PostMapping("/{id}")
    public String editarCategoria(@PathVariable Integer id,
                                  @Valid @ModelAttribute CategoriaMedicamento categoria,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "categorias-form";
        }
        try {
            categoria.setIdCategoria(id);
            categoriaMedicamentoService.editarCategoria(categoria);
            redirectAttributes.addFlashAttribute("exito", "Categoría actualizada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "categorias-form";
        }
        return "redirect:/categorias-medicamentos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Boolean resultado = categoriaMedicamentoService.eliminarCategoria(id);
        if (Boolean.TRUE.equals(resultado)) {
            redirectAttributes.addFlashAttribute("exito", "Categoría eliminada correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar una categoría que tiene medicamentos asociados.");
        }
        return "redirect:/categorias-medicamentos";
    }
}
