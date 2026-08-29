package org.esfe.controladores;

/*
 * TODO (VISTA: Categorías de Medicamentos — referencia visual en docs/stitch/categorias.html)
 * Este controlador ya es funcional. Alinear la plantilla `categorias-medicamentos.html` al diseño
 * Stitch aprobado (el HTML `.html` es autónomo con Tailwind vía CDN — NO copiar tal cual, traducir
 * al sistema Thymeleaf + CSS del proyecto). El diseño debe tener:
 *  - Layout: breadcrumb "Inventario › Categorías", botón "Nueva Categoría",
 *    barra de BÚSQUEDA + FILTROS + EXPORTAR sobre la tabla.
 *  - Tabla (columnas): Nombre de Categoría | Descripción | Medicamentos Activos | Estado | Acciones.
 *    "Medicamentos Activos" cuenta los medicamentos asociados a la categoría (0 si está vacía).
 *    Estado: badge píldora "Activa" (sin medicamentos) / "En uso" (tiene medicamentos).
 *  - Acciones por fila: editar (icono) y eliminar. El botón eliminar debe deshabilitarse
 *    con tooltip "No se puede eliminar: contiene medicamentos activos" cuando la categoría
 *    tenga medicamentos asociados (ya validado en CategoriaMedicamentoService.eliminarCategoria).
 *  - Alta/edición: panel lateral deslizante (drawer 400px) con campos
 *    Nombre de la Categoría * (único) + Descripción (opcional) + etiqueta visual opcional.
 *  - Banner contextual inferior: política de eliminación (no se borra una categoría con medicamentos).
 *  - Roles: Admin y Recepcionista gestionan; Médico solo lectura.
 * Persistir/ajustar SIN romper el CRUD actual (listar, /nuevo, POST /, /{id}/editar, POST /{id}, /{id}/eliminar).
 */
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
