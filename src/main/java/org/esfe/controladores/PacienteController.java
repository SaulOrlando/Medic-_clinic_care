package org.esfe.controladores;

/*
 * ==========================================================================================
 * TODO (VISTA: Gestión de Pacientes — PENDIENTE DE IMPLEMENTAR)
 * Referencia visual Stitch: docs/stitch/pacientes.html  (captura: docs/stitch/pacientes.png)
 * El HTML es autónomo (Tailwind vía CDN) — NO copiarlo tal cual; traducir su estructura al
 * sistema Thymeleaf + CSS del proyecto (fragments/base.html, design-system.css, layout.css).
 *
 * RUTA: /pacientes        |   TEMPLATE: pacientes.html   |   SERVICIO: IPacienteService
 * ENTIDAD: Paciente (codigoExpediente, nombres, apellidos, documentoIdentidad,
 *                    fechaNacimiento, telefono, genero) + List<Cita> citas.
 *
 * CÓMO DEBE FUNCIONAR (según Stitch):
 *  - Vista "Directorio de Pacientes": breadcrumb + título + botón "Agregar Paciente".
 *  - Búsqueda global en el header (placeholder "Buscar pacientes...") filtrando por
 *    nombre, apellido o documento de identidad.
 *  - Tabla (columnas): Documento de Identidad (mono) | Nombre del Paciente
 *    (avatar con iniciales + nombre) | Fecha de Nacimiento | Género (badge píldora)
 *    | Teléfono (derecha) | acciones (menú more_vert visible al hover de la fila).
 *  - Paginación al pie: "Mostrando X a Y de Z pacientes".
 *  - Alta/edición: panel lateral deslizante (480px) multi-paso (progresor "Paso X de 3"):
 *      * Paso 1: Nombres *, Apellidos *, Documento de Identidad * (DNI/Pasaporte) con
 *        VALIDACIÓN DE DUPLICADO EN VIVO (aviso rojo "Ya existe un paciente con este
 *        Documento de Identidad" + icono warning; usar IPacienteService para verificar).
 *      * Paso 2: Fecha de Nacimiento * (date), Género * (select Femenino/Masculino/Otro/…),
 *        Teléfono Principal * (tel).
 *      * Al guardar: genera codigoExpediente (p. ej. EXP/ID), muestra mensaje de éxito
 *        con el expediente creado y cierra el panel.
 *  - Roles (@PreAuthorize): Médico y Recepcionista (solo datos de contacto/citas),
 *    Admin (auditoría).
 *
 * PENDIENTE: crear este controlador con @Controller + @RequestMapping("/pacientes") y
 * las rutas listar / nuevo / guardar / editar / eliminar, agregando @PreAuthorize.
 * ==========================================================================================
 */
public class PacienteController {

    // TODO: implementar (ver comentario de clase arriba).
    // Este archivo es SOLO un ancla de documentación sin @Controller, para que el siguiente
    // agente sepa exactamente dónde y cómo implementar la vista de Pacientes.

}
