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

    private static final int TAMANO_PAGINA = 8;

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

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("activePage", "medicos");
        model.addAttribute("medicoForm", new MedicoForm());
        model.addAttribute("modo", "crear");
        model.addAttribute("opcionesEspecialidad", opcionesEspecialidad());
        return "medicos-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("medicoForm") MedicoForm form,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            if (usuarioService.existeCorreo(form.getCorreo())) {
                throw new IllegalStateException("Ya existe un usuario con ese correo electrónico.");
            }
            if (medicoService.buscarPorNumeroLicencia(form.getNumeroLicencia()).isPresent()) {
                throw new IllegalStateException("Ya existe un médico con ese número de licencia.");
            }

            Usuario usuario = new Usuario();
            usuario.setNombreCompleto(form.getNombreCompleto());
            usuario.setCorreo(form.getCorreo());
            usuario.setContrasena(form.getCorreo());
            usuario.setTelefono(form.getTelefono());
            usuario.setRol(RolUsuario.MEDICO);
            usuario.setActivo(true);
            Usuario usuarioGuardado = usuarioService.crearUsuario(usuario);

            Medico medico = new Medico();
            medico.setUsuario(usuarioGuardado);
            medico.setEspecialidad(form.getEspecialidad());
            medico.setNumeroLicencia(form.getNumeroLicencia());
            medico.setDisponible(true);
            medicoService.crearMedico(medico);

            redirectAttributes.addFlashAttribute("exito",
                    "Médico registrado correctamente. Iniciales: " + usuarioGuardado.getIniciales());
            return "redirect:/medicos";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("activePage", "medicos");
            model.addAttribute("medicoForm", form);
            model.addAttribute("modo", "crear");
            model.addAttribute("opcionesEspecialidad", opcionesEspecialidad());
            return "medicos-form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return medicoService.obtenerPorId(id)
                .map(medico -> {
                    MedicoForm form = new MedicoForm();
                    form.setIdMedico(medico.getIdMedico());
                    form.setNombreCompleto(medico.getUsuario().getNombreCompleto());
                    form.setCorreo(medico.getUsuario().getCorreo());
                    form.setTelefono(medico.getUsuario().getTelefono());
                    form.setEspecialidad(medico.getEspecialidad());
                    form.setNumeroLicencia(medico.getNumeroLicencia());
                    model.addAttribute("activePage", "medicos");
                    model.addAttribute("medicoForm", form);
                    model.addAttribute("modo", "editar");
                    model.addAttribute("opcionesEspecialidad", opcionesEspecialidad());
                    return "medicos-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Médico no encontrado.");
                    return "redirect:/medicos";
                });
    }

    @PostMapping("/{id}/editar")
    public String editarGuardar(@PathVariable Integer id,
                                @ModelAttribute("medicoForm") MedicoForm form,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Medico existente = medicoService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado."));

            existente.setEspecialidad(form.getEspecialidad());
            existente.setNumeroLicencia(form.getNumeroLicencia());

            if (!existente.getUsuario().getCorreo().equals(form.getCorreo())
                    && usuarioService.existeCorreo(form.getCorreo())) {
                throw new IllegalStateException("Ya existe un usuario con ese correo electrónico.");
            }
            existente.getUsuario().setNombreCompleto(form.getNombreCompleto());
            existente.getUsuario().setCorreo(form.getCorreo());
            existente.getUsuario().setTelefono(form.getTelefono());
            usuarioService.editarUsuario(existente.getUsuario());

            medicoService.editarMedico(existente);
            redirectAttributes.addFlashAttribute("exito", "Médico actualizado correctamente.");
            return "redirect:/medicos";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("activePage", "medicos");
            model.addAttribute("medicoForm", form);
            model.addAttribute("modo", "editar");
            model.addAttribute("opcionesEspecialidad", opcionesEspecialidad());
            return "medicos-form";
        }
    }

    @PostMapping("/{id}/toggle-disponibilidad")
    @ResponseBody
    public Map<String, Object> toggleDisponibilidad(@PathVariable Integer id,
                                                     @RequestParam("disponible") Boolean disponible) {
        Map<String, Object> respuesta = new HashMap<>();
        try {
            Medico medico = medicoService.cambiarDisponibilidad(id, disponible);
            respuesta.put("exito", true);
            respuesta.put("disponible", medico.getDisponible());
        } catch (IllegalArgumentException ex) {
            respuesta.put("exito", false);
            respuesta.put("mensaje", ex.getMessage());
        }
        return respuesta;
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
        boolean duplicado = encontrado.isPresent()
                && !encontrado.get().getIdMedico().equals(idMedico);
        respuesta.put("duplicado", duplicado);
        return respuesta;
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

    public static class MedicoForm {
        private Integer idMedico;
        private String nombreCompleto;
        private String correo;
        private String telefono;
        private String especialidad;
        private String numeroLicencia;

        public Integer getIdMedico() { return idMedico; }
        public void setIdMedico(Integer idMedico) { this.idMedico = idMedico; }
        public String getNombreCompleto() { return nombreCompleto; }
        public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
        public String getCorreo() { return correo; }
        public void setCorreo(String correo) { this.correo = correo; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public String getEspecialidad() { return especialidad; }
        public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }
        public String getNumeroLicencia() { return numeroLicencia; }
        public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }
    }
}
