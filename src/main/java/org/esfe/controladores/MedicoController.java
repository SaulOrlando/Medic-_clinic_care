package org.esfe.controladores;

/*
 * ==========================================================================================
 * TODO (VISTA: Gestión de Médicos — PENDIENTE DE IMPLEMENTAR)
 * Referencia visual Stitch: docs/stitch/medicos.html  (captura: docs/stitch/medicos.png)
 * El HTML es autónomo (Tailwind vía CDN) — NO copiarlo tal cual; traducir al sistema
 * Thymeleaf + CSS del proyecto (fragments/base.html, design-system.css, layout.css).
 *
 * RUTA: /medicos         |   TEMPLATE: medicos.html     |   SERVICIO: IMedicoService
 * ENTIDAD: Medico (usuario 1:1, especialidad, numeroLicencia, disponible) + List<Cita> citas.
 *
 * CÓMO DEBE FUNCIONAR (según Stitch):
 *  - Vista "Directorio de Médicos": breadcrumb + título + botón "Registrar Médico".
 *  - Búsqueda global en header (placeholder "Buscar médicos...").
 *  - Layout bento 12-col:
 *      * Formulario de registro (col-span-4, tarjeta "Nuevo Registro"): Nombre Completo,
 *        Especialidad (select: Cardiología/Neurología/Pediatría/Medicina General…),
 *        Número de Licencia (con CHECK DE UNICIDAD en vivo: icono check_circle verde +
 *        "Licencia verificada." o warning si ya existe; usar IMedicoService),
 *        Número de Teléfono, Correo Electrónico. Botones "Limpiar" y "Registrar".
 *      * Tabla del directorio activo (col-span-8, encabezado "Directorio Activo (N)"):
 *        Médico (avatar + nombre + ID), Especialidad, Licencia/Contacto (licencia mono +
 *        correo), Estado (TOGGLE de disponibilidad on/off), acciones (more_vert al hover).
 *        Con paginación al pie "Mostrando X a Y de Z entradas".
 *  - AL REGISTRAR un Médico: se debe crear también su Usuario (ROL MEDICO, correo único)
 *    y su registro Medico (1:1) con la licencia. Usar UsuarioService + MedicoService.
 *  - Roles (@PreAuthorize): Admin y Recepcionista. Los Médicos NO ven este módulo.
 *
 * PENDIENTE: crear este controlador con @Controller + @RequestMapping("/medicos") y
 * las rutas listar / nuevo / guardar / editar / eliminar / toggle-disponibilidad,
 * agregando @PreAuthorize.
 * ==========================================================================================
 */
public class MedicoController {

    // TODO: implementar (ver comentario de clase arriba).
    // Este archivo es SOLO un ancla de documentación sin @Controller, para que el siguiente
    // agente sepa exactamente dónde y cómo implementar la vista de Médicos.

}
