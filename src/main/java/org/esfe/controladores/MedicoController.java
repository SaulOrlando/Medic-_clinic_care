package org.esfe.controladores;

import org.esfe.modelos.Medico;
import org.esfe.servicios.interfaces.IMedicoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medicos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
public class MedicoController {

    private static final int TAMANO_PAGINA = 8;

    private final IMedicoService medicoService;

    public MedicoController(IMedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String busqueda,
                         @RequestParam(name = "especialidad", required = false) String especialidad,
                         @RequestParam(name = "pagina", defaultValue = "1") int pagina,
                         Model model) {
        List<Medico> filtrados = medicoService.obtenerTodos();

        if (busqueda != null && !busqueda.isBlank()) {
            String q = busqueda.trim().toLowerCase();
            filtrados = filtrados.stream()
                    .filter(m -> m.getUsuario().getNombreCompleto().toLowerCase().contains(q)
                            || m.getNumeroLicencia().toLowerCase().contains(q)
                            || m.getEspecialidad().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if (especialidad != null && !especialidad.isBlank() && !"todas".equals(especialidad)) {
            filtrados = filtrados.stream()
                    .filter(m -> especialidad.equalsIgnoreCase(m.getEspecialidad()))
                    .collect(Collectors.toList());
        }

        int total = filtrados.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / TAMANO_PAGINA));
        pagina = Math.min(Math.max(1, pagina), totalPaginas);
        int desde = (pagina - 1) * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, total);

        List<Medico> paginaMedicos = total == 0
                ? List.of()
                : filtrados.subList(desde, hasta);

        model.addAttribute("activePage", "medicos");
        model.addAttribute("medicos", paginaMedicos);
        model.addAttribute("q", busqueda);
        model.addAttribute("especialidadFiltro", especialidad == null ? "" : especialidad);
        model.addAttribute("opcionesEspecialidad", opcionesEspecialidad());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("paginasVisibles", paginasVisibles(pagina, totalPaginas));
        model.addAttribute("totalMedicos", total);
        model.addAttribute("desde", total == 0 ? 0 : desde + 1);
        model.addAttribute("hasta", hasta);
        return "medicos";
    }

    private Map<String, String> opcionesEspecialidad() {
        Map<String, String> opciones = new LinkedHashMap<>();
        opciones.put("Cardiología", "Cardiología");
        opciones.put("Dermatología", "Dermatología");
        opciones.put("Endocrinología", "Endocrinología");
        opciones.put("Gastroenterología", "Gastroenterología");
        opciones.put("Geriatría", "Geriatría");
        opciones.put("Ginecología", "Ginecología");
        opciones.put("Medicina General", "Medicina General");
        opciones.put("Nefrología", "Nefrología");
        opciones.put("Neumología", "Neumología");
        opciones.put("Neurología", "Neurología");
        opciones.put("Oftalmología", "Oftalmología");
        opciones.put("Oncología", "Oncología");
        opciones.put("Ortopedia", "Ortopedia");
        opciones.put("Pediatría", "Pediatría");
        opciones.put("Psiquiatría", "Psiquiatría");
        opciones.put("Urología", "Urología");
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
