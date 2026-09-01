package org.esfe.controladores;

import org.esfe.modelos.Paciente;
import org.esfe.servicios.interfaces.IPacienteService;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pacientes")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MEDICO', 'RECEPCIONISTA')")
public class PacienteController {

    private static final int TAMANO_PAGINA = 8;

    private final IPacienteService pacienteService;

    public PacienteController(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String busqueda,
                         @RequestParam(name = "genero", required = false) String genero,
                         @RequestParam(name = "pagina", defaultValue = "1") int pagina,
                         Model model) {
        List<Paciente> filtrados = pacienteService.obtenerTodos();

        if (busqueda != null && !busqueda.isBlank()) {
            String q = busqueda.trim().toLowerCase();
            filtrados = filtrados.stream()
                    .filter(p -> p.getNombres().toLowerCase().contains(q)
                            || p.getApellidos().toLowerCase().contains(q)
                            || p.getDocumentoIdentidad().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if (genero != null && !genero.isBlank() && !"todos".equals(genero)) {
            String generoBuscado = genero.trim();
            filtrados = filtrados.stream()
                    .filter(p -> generoBuscado.equalsIgnoreCase(p.getGenero()))
                    .collect(Collectors.toList());
        }

        int total = filtrados.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / TAMANO_PAGINA));
        pagina = Math.min(Math.max(1, pagina), totalPaginas);
        int desde = (pagina - 1) * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, total);

        List<Paciente> paginaPacientes = total == 0
                ? List.of()
                : filtrados.subList(desde, hasta);

        model.addAttribute("activePage", "pacientes");
        model.addAttribute("pacientes", paginaPacientes);
        model.addAttribute("q", busqueda);
        model.addAttribute("generoFiltro", genero == null ? "" : genero);
        model.addAttribute("opcionesGenero", opcionesGenero());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("paginasVisibles", paginasVisibles(pagina, totalPaginas));
        model.addAttribute("totalPacientes", total);
        model.addAttribute("desde", total == 0 ? 0 : desde + 1);
        model.addAttribute("hasta", hasta);
        return "pacientes";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario("crear", new Paciente(), model);
        return "pacientes-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("pacienteForm") Paciente paciente,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            Paciente guardado = crearConExpedienteAutogenerado(paciente);
            redirectAttributes.addFlashAttribute("exito",
                    "Paciente registrado correctamente. Expediente generado: #" + guardado.getCodigoExpediente());
            return "redirect:/pacientes";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarFormulario("crear", paciente, model);
            return "pacientes-form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return pacienteService.obtenerPorId(id)
                .map(paciente -> {
                    cargarFormulario("editar", paciente, model);
                    return "pacientes-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Paciente no encontrado.");
                    return "redirect:/pacientes";
                });
    }

    @PostMapping("/{id}/editar")
    public String editarGuardar(@PathVariable Integer id,
                                @ModelAttribute("pacienteForm") Paciente paciente,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Paciente existente = pacienteService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado."));
            paciente.setIdPaciente(id);
            paciente.setCodigoExpediente(existente.getCodigoExpediente());
            pacienteService.editarPaciente(paciente);
            redirectAttributes.addFlashAttribute("exito", "Paciente actualizado correctamente.");
            return "redirect:/pacientes";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarFormulario("editar", paciente, model);
            return "pacientes-form";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Boolean resultado = pacienteService.eliminarPaciente(id);
        if (Boolean.TRUE.equals(resultado)) {
            redirectAttributes.addFlashAttribute("exito", "Paciente eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el paciente porque tiene citas u otros registros asociados.");
        }
        return "redirect:/pacientes";
    }

    @GetMapping(value = "/existe-documento", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> existeDocumento(@RequestParam(name = "documento") String documento,
                                                @RequestParam(name = "id", required = false) Integer idPaciente) {
        Map<String, Boolean> respuesta = new HashMap<>();
        if (documento == null || documento.isBlank()) {
            respuesta.put("duplicado", Boolean.FALSE);
            return respuesta;
        }
        Optional<Paciente> encontrado = pacienteService.buscarPorDocumentoIdentidad(documento.trim());
        boolean duplicado = encontrado.isPresent() && !encontrado.get().getIdPaciente().equals(idPaciente);
        respuesta.put("duplicado", duplicado);
        return respuesta;
    }

    private Paciente crearConExpedienteAutogenerado(Paciente paciente) {
        int secuencial = pacienteService.obtenerTodos().size();
        while (true) {
            String codigo = generarCodigoExpediente(++secuencial);
            paciente.setCodigoExpediente(codigo);
            try {
                return pacienteService.crearPaciente(paciente);
            } catch (IllegalStateException ex) {
                if (ex.getMessage() != null && ex.getMessage().contains("documento")) {
                    throw ex;
                }
            }
        }
    }

    private String generarCodigoExpediente(int secuencial) {
        return String.format("MC-%d-%04d", LocalDate.now().getYear(), secuencial);
    }

    private void cargarFormulario(String modo, Paciente paciente, Model model) {
        model.addAttribute("activePage", "pacientes");
        model.addAttribute("pacienteForm", paciente);
        model.addAttribute("modo", modo);
        model.addAttribute("opcionesGenero", opcionesGenero());
    }

    private Map<String, String> opcionesGenero() {
        Map<String, String> opciones = new LinkedHashMap<>();
        opciones.put("Femenino", "Femenino");
        opciones.put("Masculino", "Masculino");
        opciones.put("Otro", "Otro");
        opciones.put("Prefiero no decirlo", "Prefiero no decirlo");
        return opciones;
    }

    private List<Integer> paginasVisibles(int paginaActual, int totalPaginas) {
        if (totalPaginas <= 7) {
            return java.util.stream.IntStream.rangeClosed(1, totalPaginas).boxed().toList();
        }
        int inicio = Math.max(1, paginaActual - 1);
        int fin = Math.min(totalPaginas, paginaActual + 1);
        if (inicio <= 2) {
            fin = 4;
            inicio = 1;
        }
        if (fin >= totalPaginas - 1) {
            inicio = totalPaginas - 3;
            fin = totalPaginas;
        }
        return java.util.stream.IntStream.rangeClosed(inicio, fin).boxed().toList();
    }
}