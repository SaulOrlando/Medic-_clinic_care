package org.esfe.controladores;

import org.esfe.modelos.Medico;
import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.RolUsuario;
import org.esfe.servicios.interfaces.IMedicoService;
import org.esfe.servicios.interfaces.IUsuarioService;
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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/medicos")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'RECEPCIONISTA')")
public class MedicoController {

    private static final int TAMANO_PAGINA = 7;

    private final IMedicoService medicoService;
    private final IUsuarioService usuarioService;

    public MedicoController(IMedicoService medicoService, IUsuarioService usuarioService) {
        this.medicoService = medicoService;
        this.usuarioService = usuarioService;
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
                    .filter(m -> m.getUsuario() != null
                            && m.getUsuario().getNombreCompleto() != null
                            && m.getUsuario().getNombreCompleto().toLowerCase().contains(q)
                            || m.getNumeroLicencia() != null && m.getNumeroLicencia().toLowerCase().contains(q)
                            || m.getUsuario() != null && m.getUsuario().getCorreo() != null
                            && m.getUsuario().getCorreo().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }

        if (especialidad != null && !especialidad.isBlank() && !"todas".equals(especialidad)) {
            String esp = especialidad.trim();
            filtrados = filtrados.stream()
                    .filter(m -> esp.equalsIgnoreCase(m.getEspecialidad()))
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
        model.addAttribute("totalMedicos", total);
        model.addAttribute("q", busqueda);
        model.addAttribute("especialidadFiltro", especialidad == null ? "" : especialidad);
        model.addAttribute("especialidades", especialidades());
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("paginasVisibles", paginasVisibles(pagina, totalPaginas));
        model.addAttribute("desde", total == 0 ? 0 : desde + 1);
        model.addAttribute("hasta", hasta);
        return "medicos";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam("nombreCompleto") String nombreCompleto,
                          @RequestParam("correo") String correo,
                          @RequestParam("telefono") String telefono,
                          @RequestParam("especialidad") String especialidad,
                          @RequestParam("numeroLicencia") String numeroLicencia,
                          @RequestParam(value = "contrasena", required = false) String contrasena,
                          RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = new Usuario();
            usuario.setNombreCompleto(nombreCompleto);
            usuario.setCorreo(correo);
            usuario.setTelefono(telefono);
            usuario.setRol(RolUsuario.MEDICO);
            usuario.setActivo(Boolean.TRUE);
            usuario.setContrasena(contrasena != null && !contrasena.isBlank()
                    ? contrasena
                    : generarContrasenaInicial(nombreCompleto));
            Usuario guardado = usuarioService.crearUsuario(usuario);

            Medico medico = new Medico();
            medico.setUsuario(guardado);
            medico.setEspecialidad(especialidad);
            medico.setNumeroLicencia(numeroLicencia);
            medico.setDisponible(Boolean.TRUE);
            medicoService.crearMedico(medico);

            redirectAttributes.addFlashAttribute("exito",
                    "Médico registrado correctamente con usuario de acceso @mediclinic.");
            return "redirect:/medicos";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/medicos";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return medicoService.obtenerPorId(id)
                .map(medico -> {
                    model.addAttribute("activePage", "medicos");
                    model.addAttribute("medico", medico);
                    model.addAttribute("especialidades", especialidades());
                    return "medicos-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Médico no encontrado.");
                    return "redirect:/medicos";
                });
    }

    @PostMapping("/{id}/editar")
    public String editarGuardar(@PathVariable Integer id,
                                @RequestParam("especialidad") String especialidad,
                                @RequestParam("numeroLicencia") String numeroLicencia,
                                @RequestParam("telefono") String telefono,
                                RedirectAttributes redirectAttributes) {
        try {
            Medico existente = medicoService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado."));
            existente.setEspecialidad(especialidad);
            existente.setNumeroLicencia(numeroLicencia);
            if (existente.getUsuario() != null) {
                existente.getUsuario().setTelefono(telefono);
                usuarioService.editarUsuario(existente.getUsuario());
            }
            medicoService.editarMedico(existente);
            redirectAttributes.addFlashAttribute("exito", "Médico actualizado correctamente.");
            return "redirect:/medicos";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/medicos";
        }
    }

    @PostMapping("/{id}/disponibilidad")
    public String cambiarDisponibilidad(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            Medico medico = medicoService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado."));
            boolean nuevo = !Boolean.TRUE.equals(medico.getDisponible());
            medicoService.cambiarDisponibilidad(id, nuevo);
            redirectAttributes.addFlashAttribute("exito",
                    nuevo ? "Médico marcado como disponible." : "Médico marcado como no disponible.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/medicos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        Boolean resultado = medicoService.eliminarMedico(id);
        if (Boolean.TRUE.equals(resultado)) {
            redirectAttributes.addFlashAttribute("exito", "Médico eliminado correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede eliminar el médico porque tiene citas u otros registros asociados.");
        }
        return "redirect:/medicos";
    }

    @GetMapping(value = "/existe-licencia", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Boolean> existeLicencia(@RequestParam(name = "licencia") String licencia,
                                               @RequestParam(name = "id", required = false) Integer idMedico) {
        Map<String, Boolean> respuesta = new HashMap<>();
        if (licencia == null || licencia.isBlank()) {
            respuesta.put("duplicado", Boolean.FALSE);
            return respuesta;
        }
        Optional<Medico> encontrado = medicoService.buscarPorNumeroLicencia(licencia.trim());
        boolean duplicado = encontrado.isPresent() && !encontrado.get().getIdMedico().equals(idMedico);
        respuesta.put("duplicado", duplicado);
        return respuesta;
    }

    private String generarContrasenaInicial(String nombreCompleto) {
        String base = nombreCompleto != null && !nombreCompleto.isBlank()
                ? nombreCompleto.split("\\s+")[0].toLowerCase() : "medico";
        return "Medic1inic!" + base;
    }

    private List<String> especialidades() {
        return List.of("Cardiología", "Neurología", "Pediatría", "Medicina General",
                "Dermatología", "Ginecología", "Oftalmología", "Traumatología");
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