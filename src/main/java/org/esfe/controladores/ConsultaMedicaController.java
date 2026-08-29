package org.esfe.controladores;

/*
 * ==========================================================================================
 * TODO (VISTA: Consulta Médica — PENDIENTE DE IMPLEMENTAR)
 * Referencia visual Stitch: docs/stitch/consulta.html  (captura: docs/stitch/consulta.png)
 * El HTML es autónomo (Tailwind vía CDN) — NO copiarlo tal cual; traducir al sistema
 * Thymeleaf + CSS del proyecto (fragments/base.html, design-system.css, layout.css).
 *
 * RUTA: /consultas       |   TEMPLATE: consultas.html   |   SERVICIO: IConsultaMedicaService
 * ENTIDAD: ConsultaMedica (cita 1:1, fechaConsulta, motivoConsulta, sintomatologia,
 *                          diagnostico) + List<RecetaDetalle> recetasDetalle.
 *
 * CÓMO DEBE FUNCIONAR (según Stitch):
 *  - Vista "Nota de Consulta", formulario clínico con breadcrumb Citas › Hoy › Consulta
 *    e indicador de estado ("En Progreso" -> "Atendido").
 *  - Layout bento 12-col:
 *      * Panel paciente (col-span-4): foto, nombre, fecha de nacimiento (y edad),
 *        N° historia (mono), Signos Vitales (últimos: PA, FC), Alergias (chips rojos),
 *        Medicamentos Activos (chips azules).
 *      * Formulario (col-span-8) con PROGRESOR SOAP (Subjetivo ->1, Objetivo ->2, Plan ->3):
 *          - Motivo de Consulta / Razón Principal *
 *          - Síntomas e Historia de la Enfermedad Actual (textarea)
 *          - Evaluación / Diagnóstico (input con link "Añadir Código CIE-10"; chips de
 *            códigos como I10 con botón para quitar)
 *          - Plan de Tratamiento y Notas de Recetas (textarea)
 *        Botones al pie: "Guardar Borrador" y "Guardar y Emitir Receta".
 *  - AL GUARDAR: registrar la ConsultaMedica y pasar la cita a estado ATENDIDA
 *    (ver flujo en docs/explicacion-sistema.md). "Guardar y Emitir Receta" redirige luego
 *    a la creación de Receta.
 *  - Roles (@PreAuthorize): Exclusivo MEDICO. Admin solo lectura histórica (cumplimiento legal).
 *
 * PENDIENTE: crear este controlador con @Controller + @RequestMapping("/consultas") y
 * las rutas necesarioas, agregando @PreAuthorize.
 * ==========================================================================================
 */
public class ConsultaMedicaController {

    // TODO: implementar (ver comentario de clase arriba).
    // Este archivo es SOLO un ancla de documentación sin @Controller, para que el siguiente
    // agente sepa exactamente dónde y cómo implementar la vista de Consulta Médica.

}
