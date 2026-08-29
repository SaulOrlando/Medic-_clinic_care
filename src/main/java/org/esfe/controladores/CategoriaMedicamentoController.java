package org.esfe.controladores;

import org.esfe.modelos.CategoriaMedicamento;
import org.esfe.servicios.interfaces.ICategoriaMedicamentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias-medicamentos")
public class CategoriaMedicamentoController {

    private final ICategoriaMedicamentoService categoriaMedicamentoService;

    public CategoriaMedicamentoController(ICategoriaMedicamentoService categoriaMedicamentoService) {
        this.categoriaMedicamentoService = categoriaMedicamentoService;
    }

    @GetMapping
    public String listarCategorias(Model model) {
        model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
        model.addAttribute("activePage", "categorias");
        model.addAttribute("categoria", new CategoriaMedicamento());
        return "categorias-medicamentos";
    }

    @GetMapping("/nuevo")
    public String mostrarNuevo(Model model) {
        model.addAttribute("activePage", "categorias");
        model.addAttribute("categoria", new CategoriaMedicamento());
        model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
        return "categorias-medicamentos";
    }

    @PostMapping
    public String crearCategoria(@ModelAttribute CategoriaMedicamento categoria, Model model) {
        try {
            categoriaMedicamentoService.crearCategoria(categoria);
            model.addAttribute("exito", "Categoría creada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("activePage", "categorias");
        model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
        return "categorias-medicamentos";
    }

    @GetMapping("/{id}/editar")
    public String mostrarEditar(@PathVariable Integer id, Model model) {
        CategoriaMedicamento categoria = categoriaMedicamentoService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));
        model.addAttribute("activePage", "categorias");
        model.addAttribute("categoria", categoria);
        model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
        return "categorias-medicamentos";
    }

    @PostMapping("/{id}")
    public String editarCategoria(@PathVariable Integer id, @ModelAttribute CategoriaMedicamento categoria, Model model) {
        try {
            categoria.setIdCategoria(id);
            categoriaMedicamentoService.editarCategoria(categoria);
            model.addAttribute("exito", "Categoría actualizada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("activePage", "categorias");
        model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
        return "categorias-medicamentos";
    }

    @GetMapping("/{id}/eliminar")
    public String eliminarCategoria(@PathVariable Integer id, Model model) {
        Boolean resultado = categoriaMedicamentoService.eliminarCategoria(id);
        if (resultado) {
            model.addAttribute("exito", "Categoría eliminada correctamente.");
        } else {
            model.addAttribute("error", "No se puede eliminar una categoría que tiene medicamentos asociados.");
        }
        model.addAttribute("activePage", "categorias");
        model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
        return "categorias-medicamentos";
    }
}
