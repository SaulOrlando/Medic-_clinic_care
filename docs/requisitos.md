# MediClinic Care — Requisitos

Verificacion de requisitos extraidos de las historias de usuario en JIRA (`docs/GHistorias de usuarios.xml`) contra el codigo actual del proyecto.

---

## 1. SCRUM-116: Inicio de Sesion

**Como usuario del sistema (medico, recepcionista o administrador), quiero iniciar sesion con mis credenciales (correo/usuario y contrasena), para acceder a los modulos de la plataforma segun mi rol y proteger la informacion medica.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | El sistema valida el formato del correo/usuario y la contrasena | ✅ Cumple | `UsuarioService.autenticar()` valida credenciales via `IUsuarioRepository.findByCorreoAndContrasena()`. `CustomUserDetailsService` valida que el usuario exista y este activo. |
| 2 | Muestra un mensaje claro de error si los datos son incorrectos | ✅ Cumple | `login.html` muestra `${param.error}` con mensaje de error visible. |
| 3 | Redirige al panel principal (dashboard) correspondiente segun el rol del usuario tras un acceso exitoso | ✅ Cumple | `SecurityConfig` configura `defaultSuccessUrl("/panel", true)`. El `GlobalModelAdvice` expone `usuario` y `rolNombre` a todas las vistas para que el sidebar filtre por rol. |
| 4 | Incluye opcion de "Ocultar/Mostrar" contrasena | ⚠️ Parcial | `login.html` tiene campo de contrasena pero se requiere verificar si el toggle JS esta implementado. |

### Subtareas completadas
- SCRUM-120: Enum RolUsuario ✅
- SCRUM-121: Vista de configuracion ✅
- SCRUM-122: Vista de gestion de usuarios ✅
- SCRUM-123: auth/login.html ✅
- SCRUM-136: Usuario.java ✅
- SCRUM-171: UsuarioRepository ✅
- SCRUM-172: IUsuarioService ✅
- SCRUM-174: Vista Dashboard ✅
- SCRUM-176: UsuarioService ✅

---

## 2. SCRUM-117: Registro de Pacientes

**Como recepcionista de la clinica, quiero registrar la informacion personal y de contacto de un nuevo paciente, para aperturar su expediente digital y permitir la programacion de sus citas medicas.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | Campos obligatorios: Nombres completos, Apellidos, Documento de Identidad, Fecha de nacimiento, Telefono y Genero | ✅ Cumple | `Paciente.java` tiene `@NotBlank` en nombres, apellidos, documentoIdentidad; `@NotNull` en fechaNacimiento; campos telefono y genero. |
| 2 | Si se ingresa un Documento de Identidad duplicado, bloquear el guardado y mostrar mensaje de error | ✅ Cumple | `PacienteService.crearPaciente()` valida `existeDocumentoIdentidad()`. El controller expone endpoint `/pacientes/existe-documento` para validacion en vivo. `pacientes-form.html` tiene validacion JS. |
| 3 | No debe permitir fechas de nacimiento futuras | ✅ Cumple | `Paciente.java` tiene `@Past` en `fechaNacimiento`. |
| 4 | No guardar si falta algun campo obligatorio | ✅ Cumple | Validacion Jakarta `@NotBlank`/`@NotNull` + `BindingResult` en el controller. |
| 5 | Al guardar, generar codigo de expediente unico y mostrar confirmacion | ✅ Cumple | `PacienteService.crearPaciente()` genera `MC-<anio>-<secuencial>` via `IUsuarioRepository` secuencial. Flash attribute de confirmacion en el controller. |

### Subtareas completadas
- SCRUM-124: Paciente.java ✅
- SCRUM-125: PacienteRepository ✅
- SCRUM-126: IPacienteService ✅
- SCRUM-127: Vista gestion de pacientes ✅
- SCRUM-177: PacienteService ✅

---

## 3. SCRUM-118: Registros Medicos

**Como administrador del sistema, quiero registrar y gestionar el perfil de los medicos de la clinica, para asignarles horarios de atencion y vincularlos a las consultas.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | Formulario con campos obligatorios: Nombre completo, Especialidad, Numero de junta/Licencia medica, Telefono y Correo | ✅ Cumple | `MedicoController` crea `MedicoForm` DTO con todos los campos. `Medico.java` tiene validaciones `@NotBlank` y `@Column(unique)`. |
| 2 | Validacion para evitar duplicados en el numero de licencia medica | ✅ Cumple | `MedicoService.crearMedico()` valida `existsByNumeroLicencia()`. Endpoint `/medicos/existe-licencia` para validacion en vivo. |
| 3 | Opcion para activar o desactivar la disponibilidad de un medico | ✅ Cumple | Endpoint `/medicos/{id}/toggle-disponibilidad` con `Medico.cambiarDisponibilidad()`. |

### Subtareas completadas
- SCRUM-128: Medico.java ✅
- SCRUM-129: MedicoRepository ✅
- SCRUM-130: IMedicoService ✅
- SCRUM-131: Vista gestion de medicos ✅
- SCRUM-178: MedicoService ✅

---

## 4. SCRUM-119: Agendamiento de Citas

**Como recepcionista, quiero programar una cita medica seleccionando paciente, medico, fecha y hora.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | El sistema muestra un calendario interactivo con bloques de tiempo disponibles por medico | ⚠️ Parcial | `citas.html` existe con vista tipo calendario. Se requiere verificar que la vista muestre bloques visuales de disponibilidad (puede ser tabla/lista en lugar de calendario visual). |
| 2 | Se evita la duplicidad de citas (no permite agendar dos citas en el mismo horario con el mismo medico) | ✅ Cumple | `CitaService.programarCita()` valida `verificarDisponibilidad()` que usa `existeConflictoHorario()` filtrando por rango de fechas y excluyendo CANCELADA. |
| 3 | La cita se crea inicialmente con el estado "Programada" | ✅ Cumple | `Cita.java` tiene `@Enumerated(EnumType.STRING)` con default `PROGRAMADA`. `CitaService.programarCita()` crea con este estado. |

### Subtareas completadas
- SCRUM-132: Enum EstadoCita ✅
- SCRUM-133: Cita.java ✅
- SCRUM-134: CitaRepository ✅
- SCRUM-135: ICitaService ✅
- SCRUM-144: Vista agendamientos de citas ✅ (controlador + vista existen)
- SCRUM-179: CitaService ✅

---

## 5. SCRUM-7: Modificacion, Reagendamiento o Cancelacion de Citas

**Como recepcionista, quiero mover fecha/hora o cancelar una cita previamente agendada, para reorganizar la agenda medica ante imprevistos del paciente o doctor.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | Permite seleccionar una cita y cambiar el estado a "cancelada" o "reagendada" | ✅ Cumple | `CitaController` tiene endpoints `/citas/{id}/cancelar` y `/citas/{id}/reagendar` con `@PostMapping`. |
| 2 | Al cancelar, el bloque de horario vuelve a estar libre en la agenda del medico | ✅ Cumple | `Cita.cancelarCita()` cambia estado a `CANCELADA`. El sistema al verificar disponibilidad excluye citas `CANCELADA`. |
| 3 | Campo opcional para ingresar la razon o motivo del cambio/cancelacion | ✅ Cumple | `HistorialCita.java` tiene campo `motivo` (TEXT 1000). El controller lo registra al cancelar/reagendar. |

### Subtareas completadas
- SCRUM-139: HistorialCita.java ✅
- SCRUM-146: HistorialCitaRepository ✅
- SCRUM-175: IHistorialCitaService ✅
- SCRUM-180: HistorialCitaService ✅

---

## 6. SCRUM-6: Registro de Consulta Medica

**Como medico, quiero registrar el motivo de consulta, diagnostico y medicamentos recetados a un paciente durante su cita, para actualizar su historial clinico y dar por finalizada la atencion.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | Formulario con campos: Motivo de consulta, Sintomatologia, Diagnostico y Receta de medicamentos (seleccionados del catalogo) | ✅ Cumple | `ConsultaMedica.java` tiene `motivoConsulta`, `sintomatologia`, `diagnostico` (todos `@NotBlank`). `RecetaDetalle` maneja los medicamentos de la receta. Vista `consultas-form.html` existe. |
| 2 | Al guardar la consulta, el estado de la cita cambia automaticamente a "Atendida" | ✅ Cumple | `ConsultaMedicaService.registrarConsulta()` hace `cita.setEstado(EstadoCita.ATENDIDA)` antes de guardar. |

### Subtareas completadas
- SCRUM-140: Vista consulta medica ✅ (en revision visual)
- SCRUM-162: Extender ConsultaMedicaRepository (findByCita) ✅
- SCRUM-163: IConsultaMedicaService ✅
- SCRUM-164: ConsultaMedicaService ✅
- SCRUM-165: Subtarea "Borrar" — pendiente (sin resolver, sin asignar)

---

## 7. SCRUM-110: Receta Medica + SCRUM-111: Detalle de Receta

**Como medico, quiero crear una receta medica, para indicar los medicamentos que debe tomar el paciente.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | Registrar el paciente | ✅ Cumple | La receta se asocia a una Consulta, que a su vez esta ligada a una Cita con Paciente. |
| 2 | Registrar el medico | ✅ Cumple | La ConsultaMedica se asocia a una Cita que tiene el medico. |
| 3 | Registrar la fecha | ✅ Cumple | `ConsultaMedica` tiene `fechaConsulta` con default `LocalDateTime.now()`. |
| 4 | Agregar medicamentos (detalle) | ✅ Cumple | `RecetaDetalle` permite asociar multiples medicamentos a una consulta con cantidad e indicaciones. |
| 5 | Guardar la receta | ✅ Cumple | `RecetaController` tiene endpoints CRUD completos. |
| 6 | Seleccionar medicamento | ✅ Cumple | `recetas-form.html` carga catalogo de medicamentos disponibles. |
| 7 | Indicar dosis | ✅ Cumple | Campo `cantidad` en `RecetaDetalle.java`. |
| 8 | Indicar cada cuanto debe tomarlo (frecuencia) | ⚠️ Verificar | El campo `indicaciones` (TEXT) probablemente cubre frecuencia. No hay campo separado para frecuencia. Verificar si `indicaciones` es suficiente o si falta un campo dedicado. |
| 9 | Indicar por cuantos dias (duracion) | ⚠️ Verificar | Similar al anterior. Puede estar incluido en `indicaciones`. Verificar si la vista lo presenta como campos separados o un solo campo de texto libre. |

### Subtareas completadas
- SCRUM-141: Enum EstadoReceta ✅
- SCRUM-147: ConsultaMedica.java ✅
- SCRUM-148: RecetaDetalle.java ✅
- SCRUM-149: ConsultaMedicaRepository + RecetaDetalleRepository ✅
- SCRUM-150: IRecetaDetalleService ✅
- SCRUM-152: Vista recetas medicas ✅ (en revision)
- SCRUM-181: RecetaDetalleService ✅

---

## 8. SCRUM-11: Registro de Medicina

**Como encargado de inventario, quiero registrar medicamentos en el catalogo (nombre, concentracion, stock inicial) para que los medicos puedan recetarlos durante las consultas.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | Campos: Nombre comercial, Nombre generico, Presentacion (pastillas, jarabe), Unidad de medida y fecha de vencimiento | ✅ Cumple | `Medicamento.java` tiene todos estos campos: `nombreComercial`, `nombreGenerico`, `presentacion`, `unidadMedida`, `concentracion`, `fechaVencimiento`. |
| 2 | Asignacion obligatoria a una categoria de medicamento previamente existente | ✅ Cumple | `Medicamento` tiene `@ManyToOne CategoriaMedicamento categoria` con `@NotNull`. Validado en `MedicamentoService.crearMedicamento()`. |
| 3 | Control de stock disponible en inventario | ✅ Cumple | `stockInicial` y `stockDisponible` en `Medicamento.java`. `Medicamento.actualizarStock()` descuenta. Vista muestra metricas de stock. |
| 4 | Validacion de caducidad: No se debe tener medicamentos vencidos | ⚠️ Parcial | `Medicamento.validarCaducidad()` existe y la vista muestra badges. Sin embargo, **no se impide el guardado** de medicamentos vencidos — solo se muestra el estado visual. Se podria mejorar para bloquear el alta de medicamentos ya vencidos. |

### Subtareas completadas
- SCRUM-143: Medicamento.java ✅
- SCRUM-153: MedicamentoRepository ✅
- SCRUM-154: IMedicamentoService ✅
- SCRUM-155: Vista inventario de medicina ✅
- SCRUM-166: Enum TipoMovimiento ✅
- SCRUM-167: MovimientoInventario.java ✅
- SCRUM-168: MovimientoInventarioRepository ✅
- SCRUM-169: IMovimientoInventarioService ✅
- SCRUM-183: MovimientoInventarioService ✅
- SCRUM-184: MedicamentoService ✅

---

## 9. SCRUM-5: Gestion de Categorias de Medicamentos

**Como administrador, quiero crear y clasificar las categorias de farmacos (ej. analgesicos, Antibioticos, Antinflamatorios), para organizar el catalogo de medicinas y agilizar la busqueda al recetar.**

### Criterios de Aceptacion

| # | Criterio | Estado | Evidencia |
|---|---|---|---|
| 1 | Permite crear, editar y eliminar categorias | ✅ Cumple | `CategoriaMedicamentoController` tiene CRUD completo: crear, editar, eliminar. |
| 2 | No se puede eliminar una categoria si tiene medicamentos activos asociados | ✅ Cumple | `CategoriaMedicaService.eliminarCategoria()` valida `tieneMedicamentosAsociados()` y lanza excepcion. |
| 3 | Listado visual de categorias ordenadas alfabeticamente | ⚠️ Parcial | Vista `categorias-medicamentos.html` existe. Se requiere verificar si el listado esta ordenado por nombre en el repository o en el controller. `ICategoriaMedicamentoRepository` no tiene query explicita de ordenamiento por `findAll()` — depende del orden de JPA por defecto (por PK). **Posible gap: falta `findAllByOrderByNombre()` o `@OrderBy("nombre")`.** |

### Subtareas completadas
- SCRUM-112: CategoriaMedicamento.java ✅
- SCRUM-113: CategoriaMedicamentoRepository ✅
- SCRUM-114: ICategoriaMedicamentoService ✅
- SCRUM-115: Vista CategoriasMedicamentos ✅
- SCRUM-182: CategoriaMedicamentoService ✅

---

## Resumen de cumplimiento

| Historia | Criterios totales | Cumplidos | Parciales | No cumplidos |
|---|---|---|---|---|
| SCRUM-116: Inicio de Sesion | 4 | 3 | 1 (toggle contrasena) | 0 |
| SCRUM-117: Registro de Pacientes | 5 | 5 | 0 | 0 |
| SCRUM-118: Registros Medicos | 3 | 3 | 0 | 0 |
| SCRUM-119: Agendamiento de Citas | 3 | 2 | 1 (calendario visual) | 0 |
| SCRUM-7: Modificacion/Cancelacion Citas | 3 | 3 | 0 | 0 |
| SCRUM-6: Registro de Consulta Medica | 2 | 2 | 0 | 0 |
| SCRUM-110+111: Receta Medica | 9 | 6 | 3 (frecuencia, duracion como campos separados) | 0 |
| SCRUM-11: Registro de Medicina | 4 | 3 | 1 (bloqueo caducidad) | 0 |
| SCRUM-5: Gestion de Categorias | 3 | 2 | 1 (orden alfabetico) | 0 |
| **TOTAL** | **36** | **29** | **7** | **0** |

---

## Gaps identificados (a revisar/corregir)

### Seguridad
1. **`CategoriaMedicamentoController` no tiene `@PreAuthorize`** — cualquier usuario autenticado puede acceder a crear/editar/eliminar categorias, aunque el sidebar lo oculte. Falta `@PreAuthorize("hasAnyRole('ADMINISTRADOR','RECEPCIONISTA','MEDICO')")` o equivalente.
2. **Contrasenas en texto plano** — `PlainTextPasswordEncoder` no hashea contrasenas. Esto es un riesgo de seguridad significativo para produccion.
3. **`HistorialCita` y `MovimientoInventario` — `@PreAuthorize` no verificado** — estos modulos dependen del controlador padre (Citas/Inventario) pero se debe verificar que no existan rutas huérfanas.

### Funcionalidad
4. **Toggle mostrar/ocultar contrasena en login** — Verificar que `login.html` tenga el JS para mostrar/ocultar contrasena.
5. **Orden alfabetico de categorias** — `CategoriaMedicamentoRepository` no tiene query con `ORDER BY nombre`. Agregar `findAllByOrderByNombre()` o `@OrderBy` en la entidad.
6. **Bloqueo de medicamentos vencidos** — `Medicamento.validarCaducidad()` solo marca el estado visualmente. Podria agregarse validacion en `MedicamentoService` para rechazar creacion/edicion si `fechaVencimiento` es anterior a hoy.
7. **Receta — campos de frecuencia/duracion** — Actualmente un solo campo `indicaciones` (TEXT). Si la UI lo requiere como campos separados (dosis, frecuencia, duracion), hay que refactorizar `RecetaDetalle`.
8. **Vista de Citas — calendario visual** — Verificar si `citas.html` muestra un calendario interactivo (drag & drop, bloques de tiempo) o solo una tabla/lista. El criterio pide "calendario interactivo con bloques de tiempo".
9. **Vistas de Pacientes, Medicos, Consultas, Recetas en "En revision"** — Los templates existen pero pueden no estar 100% alineados al diseno Stitch.

### Pendientes sin resolver en JIRA
10. **SCRUM-165: "Borrar"** — Subtarea sin resolver y sin asignar dentro de SCRUM-6 (Registro de consulta medica). Verificar si es relevante.
