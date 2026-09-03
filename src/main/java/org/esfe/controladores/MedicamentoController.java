package org.esfe.controladores;

/*
 * TODO (VISTA: Inventario de Medicamentos — referencia visual en docs/stitch/inventario.html)
 * Este controlador ya es funcional. Alinear la plantilla `medicamentos.html` al diseño Stitch
 * aprobado (el HTML `.html` es autónomo con Tailwind vía CDN — traducir a Thymeleaf + CSS propio).
 * El diseño debe tener:
 *  - Bento grid 12-col: tabla principal (col-span-8) + panel lateral (col-span-4).
 *  - Encabezado: breadcrumb "Inventario", título, botones "Filtrar" y "Nuevo Medicamento".
 *  - Tarjetas de métricas (3): Artículos Totales (medication), Stock Bajo (warning/ámbar),
 *    Próximo a Vencer (event_busy/rojo).
 *  - Tabla "Stock Actual" (columnas): Nombre Comercial | Nombre Genérico | Categoría (badge)
 *    | Stock (derecha, tabular) | Vencimiento | Estado.
 *  - Estado por fila: vigente (check verde), próximo a vencer (badge ámbar "PRÓX. VENC."),
 *    vencido/bloqueado (rojo, `line-through` + badge "BLOQUEADO"), stock bajo (fondo resaltado).
 *    Usar Medicamento.validarCaducidad()/stockDisponible para calcular el estado.
 *  - Panel lateral: formulario "Registrar Medicamento" (Nombre Comercial *, Nombre Genérico,
 *    Categoría * select, Stock Inicial, Fecha de Vencimiento *) + sección "Categorías"
 *    con alta rápida y cuenta de artículos (y aviso de que no se borran categorías con medicamentos).
 *  - Roles: Admin y Recepcionista gestionan (crear/editar/eliminar); Médico SOLO lectura.
 * Sin romper el CRUD actual (/ GET, /nuevo, /guardar, /{id}/editar GET+POST, /{id}/eliminar POST).
 */
import org.esfe.modelos.CategoriaMedicamento;
import org.esfe.modelos.Medicamento;
import org.esfe.servicios.interfaces.ICategoriaMedicamentoService;
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

@Controller
@RequestMapping("/medicamentos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA', 'MEDICO')")
public class MedicamentoController {

    private final IMedicamentoService medicamentoService;
    private final ICategoriaMedicamentoService categoriaMedicamentoService;

    public MedicamentoController(IMedicamentoService medicamentoService,
                                  ICategoriaMedicamentoService categoriaMedicamentoService) {
        this.medicamentoService = medicamentoService;
        this.categoriaMedicamentoService = categoriaMedicamentoService;
    }

    @GetMapping
    public String listar(Model model) {
        List<Medicamento> medicamentos = medicamentoService.obtenerTodos();
        List<Medicamento> stockBajo = medicamentoService.obtenerConStockBajo(10);
        List<Medicamento> vencidos = medicamentoService.obtenerVencidosAntesDe(LocalDate.now());
        List<CategoriaMedicamento> categorias = categoriaMedicamentoService.obtenerTodos();

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
        model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
        model.addAttribute("modo", "crear");
        return "medicamentos-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("medicamento") Medicamento medicamento,
                           RedirectAttributes redirectAttributes) {
        try {
            if (medicamento.getCategoriaId() != null) {
                CategoriaMedicamento cat = categoriaMedicamentoService.obtenerPorId(medicamento.getCategoriaId())
                        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));
                medicamento.setCategoria(cat);
            }
            if (medicamento.getStockDisponible() == null) {
                medicamento.setStockDisponible(medicamento.getStockInicial() != null ? medicamento.getStockInicial() : 0);
            }
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
                    model.addAttribute("categorias", categoriaMedicamentoService.obtenerTodos());
                    model.addAttribute("modo", "editar");
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
            if (medicamento.getCategoriaId() != null) {
                CategoriaMedicamento cat = categoriaMedicamentoService.obtenerPorId(medicamento.getCategoriaId())
                        .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));
                medicamento.setCategoria(cat);
            }
            if (medicamento.getStockDisponible() == null) {
                medicamento.setStockDisponible(medicamento.getStockInicial() != null ? medicamento.getStockInicial() : 0);
            }
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
