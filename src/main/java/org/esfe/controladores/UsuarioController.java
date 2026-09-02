package org.esfe.controladores;

import org.esfe.modelos.Medico;
import org.esfe.modelos.Usuario;
import org.esfe.modelos.enums.RolUsuario;
import org.esfe.servicios.interfaces.IMedicoService;
import org.esfe.servicios.interfaces.IUsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UsuarioController {

    private static final int TAMANO_PAGINA = 8;

    private final IUsuarioService usuarioService;
    private final IMedicoService medicoService;

    public UsuarioController(IUsuarioService usuarioService, IMedicoService medicoService) {
        this.usuarioService = usuarioService;
        this.medicoService = medicoService;
    }

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String busqueda,
                         @RequestParam(name = "rol", required = false) String rolParam,
                         @RequestParam(name = "estado", required = false) String estadoParam,
                         @RequestParam(name = "pagina", defaultValue = "1") int pagina,
                         Model model) {
        RolUsuario rol = parsearRol(rolParam);
        Boolean activo = parsearEstado(estadoParam);

        List<Usuario> filtrados = usuarioService.buscarPorFiltro(busqueda, rol, activo);

        int total = filtrados.size();
        int totalPaginas = Math.max(1, (int) Math.ceil((double) total / TAMANO_PAGINA));
        pagina = Math.min(Math.max(1, pagina), totalPaginas);
        int desde = (pagina - 1) * TAMANO_PAGINA;
        int hasta = Math.min(desde + TAMANO_PAGINA, total);

        List<Usuario> paginaUsuarios = total == 0
                ? List.of()
                : filtrados.subList(desde, hasta);

        model.addAttribute("activePage", "usuarios");
        model.addAttribute("usuarios", paginaUsuarios);
        model.addAttribute("q", busqueda);
        model.addAttribute("rolFiltro", rolParam == null ? "" : rolParam);
        model.addAttribute("estadoFiltro", estadoParam == null ? "" : estadoParam);
        model.addAttribute("paginaActual", pagina);
        model.addAttribute("totalPaginas", totalPaginas);
        model.addAttribute("paginasVisibles", paginasVisibles(pagina, totalPaginas));
        model.addAttribute("totalUsuarios", total);
        model.addAttribute("desde", total == 0 ? 0 : desde + 1);
        model.addAttribute("hasta", hasta);
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("etiquetasRol", etiquetasRol());
        return "usuarios";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        cargarFormulario("crear", new Usuario(), model);
        return "usuarios-form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuarioForm") Usuario usuario,
                          @RequestParam(name = "especialidad", required = false) String especialidad,
                          @RequestParam(name = "numeroLicencia", required = false) String numeroLicencia,
                          Model model,
                          RedirectAttributes redirectAttributes) {
        try {
            usuario.setActivo(true);
            if (RolUsuario.MEDICO.equals(usuario.getRol())) {
                validarDatosMedico(especialidad, numeroLicencia);
            }
            Usuario guardado = usuarioService.crearUsuario(usuario);
            if (RolUsuario.MEDICO.equals(guardado.getRol())) {
                crearRegistroMedico(guardado, especialidad, numeroLicencia);
            }
            redirectAttributes.addFlashAttribute("exito", "Usuario creado correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarFormulario("crear", usuario, model);
            model.addAttribute("especialidad", especialidad == null ? "" : especialidad);
            model.addAttribute("numeroLicencia", numeroLicencia == null ? "" : numeroLicencia);
            return "usuarios-form";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return usuarioService.obtenerPorId(id)
                .map(usuario -> {
                    cargarFormulario("editar", usuario, model);
                    return "usuarios-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                    return "redirect:/usuarios";
                });
    }

    @PostMapping("/{id}/editar")
    public String editarGuardar(@PathVariable Integer id,
                                @ModelAttribute("usuarioForm") Usuario usuario,
                                @RequestParam(name = "especialidad", required = false) String especialidad,
                                @RequestParam(name = "numeroLicencia", required = false) String numeroLicencia,
                                @AuthenticationPrincipal UserDetails usuarioActual,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario existente = usuarioService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

            if (usuario.getContrasena() == null || usuario.getContrasena().isBlank()) {
                usuario.setContrasena(existente.getContrasena());
            }
            usuario.setIdUsuario(id);
            usuario.setActivo(existente.getActivo());

            if (usuarioActual != null
                    && existente.getCorreo().equalsIgnoreCase(usuarioActual.getUsername())) {
                usuario.setCorreo(existente.getCorreo());
                usuario.setRol(existente.getRol());
            }

            boolean esMedico = RolUsuario.MEDICO.equals(usuario.getRol());
            if (esMedico) {
                validarDatosMedico(especialidad, numeroLicencia);
            }

            Usuario saved = usuarioService.editarUsuario(usuario);

            if (esMedico) {
                actualizarRegistroMedico(saved, especialidad, numeroLicencia);
            }

            redirectAttributes.addFlashAttribute("exito", "Usuario actualizado correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("error", ex.getMessage());
            cargarFormulario("editar", usuario, model);
            model.addAttribute("especialidad", especialidad == null ? "" : especialidad);
            model.addAttribute("numeroLicencia", numeroLicencia == null ? "" : numeroLicencia);
            return "usuarios-form";
        }
    }

    @GetMapping("/{id}/restablecer-contrasena")
    public String formRestablecerContrasena(@PathVariable Integer id,
                                            Model model,
                                            RedirectAttributes redirectAttributes) {
        return usuarioService.obtenerPorId(id)
                .map(usuario -> {
                    model.addAttribute("activePage", "usuarios");
                    model.addAttribute("usuarioObjetivo", usuario);
                    return "usuarios-contrasena";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
                    return "redirect:/usuarios";
                });
    }

    @PostMapping("/{id}/restablecer-contrasena")
    public String restablecerContrasena(@PathVariable Integer id,
                                        @RequestParam("nuevaContrasena") String nuevaContrasena,
                                        RedirectAttributes redirectAttributes) {
        try {
            usuarioService.restablecerContrasena(id, nuevaContrasena);
            redirectAttributes.addFlashAttribute("exito", "Contraseña restablecida correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/usuarios/" + id + "/restablecer-contrasena";
    }

    @PostMapping("/{id}/estado")
    public String cambiarEstado(@PathVariable Integer id,
                                @AuthenticationPrincipal UserDetails usuarioActual,
                                RedirectAttributes redirectAttributes) {
        Usuario objetivo = usuarioService.obtenerPorId(id).orElse(null);
        if (objetivo == null) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/usuarios";
        }
        if (Boolean.TRUE.equals(objetivo.getActivo())
                && usuarioActual != null
                && objetivo.getCorreo().equalsIgnoreCase(usuarioActual.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "No puede desactivar su propia cuenta.");
            return "redirect:/usuarios";
        }
        usuarioService.cambiarEstado(id);
        boolean quedaronActivos = !Boolean.TRUE.equals(objetivo.getActivo());
        redirectAttributes.addFlashAttribute("exito",
                quedaronActivos ? "Usuario desactivado." : "Usuario activado.");
        return "redirect:/usuarios";
    }

    private RolUsuario parsearRol(String rolParam) {
        if (rolParam == null || rolParam.isBlank() || "todos".equals(rolParam)) {
            return null;
        }
        try {
            return RolUsuario.valueOf(rolParam);
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private Boolean parsearEstado(String estadoParam) {
        if (estadoParam == null || estadoParam.isBlank() || "todos".equals(estadoParam)) {
            return null;
        }
        if ("activo".equalsIgnoreCase(estadoParam)) {
            return Boolean.TRUE;
        }
        if ("inactivo".equalsIgnoreCase(estadoParam)) {
            return Boolean.FALSE;
        }
        return null;
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

    private void cargarFormulario(String modo, Usuario usuario, Model model) {
        model.addAttribute("activePage", "usuarios");
        model.addAttribute("usuarioForm", usuario);
        model.addAttribute("roles", RolUsuario.values());
        model.addAttribute("etiquetasRol", etiquetasRol());
        model.addAttribute("opcionesEspecialidad", opcionesEspecialidad());
        model.addAttribute("modo", modo);

        String especialidad = "";
        String numeroLicencia = "";
        if (usuario != null && usuario.getIdUsuario() != null) {
            Medico medico = medicoService.buscarPorUsuario(usuario.getIdUsuario()).orElse(null);
            if (medico != null) {
                especialidad = medico.getEspecialidad() == null ? "" : medico.getEspecialidad();
                numeroLicencia = medico.getNumeroLicencia() == null ? "" : medico.getNumeroLicencia();
            }
        }
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("numeroLicencia", numeroLicencia);
    }

    private Map<RolUsuario, String> etiquetasRol() {
        Map<RolUsuario, String> etiquetas = new LinkedHashMap<>();
        etiquetas.put(RolUsuario.ADMINISTRADOR, "Administrador");
        etiquetas.put(RolUsuario.MEDICO, "Médico");
        etiquetas.put(RolUsuario.RECEPCIONISTA, "Recepcionista");
        return etiquetas;
    }

    private void validarDatosMedico(String especialidad, String numeroLicencia) {
        if (especialidad == null || especialidad.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar una especialidad para crear un usuario médico.");
        }
        if (numeroLicencia == null || numeroLicencia.isBlank()) {
            throw new IllegalArgumentException("El número de licencia es obligatorio para un usuario médico.");
        }
    }

    private void crearRegistroMedico(Usuario usuario, String especialidad, String numeroLicencia) {
        Medico medico = new Medico();
        medico.setUsuario(usuario);
        medico.setEspecialidad(especialidad.trim());
        medico.setNumeroLicencia(numeroLicencia.trim());
        medico.setDisponible(true);
        medicoService.crearMedico(medico);
    }

    private void actualizarRegistroMedico(Usuario usuario, String especialidad, String numeroLicencia) {
        Medico existente = medicoService.buscarPorUsuario(usuario.getIdUsuario()).orElse(null);
        if (existente == null) {
            crearRegistroMedico(usuario, especialidad, numeroLicencia);
            return;
        }
        existente.setEspecialidad(especialidad.trim());
        existente.setNumeroLicencia(numeroLicencia.trim());
        medicoService.editarMedico(existente);
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
}