# MediClinic Care — Explicacion del sistema

## Roles del sistema

El sistema tiene **3 roles** definidos en el enum `RolUsuario` (fuente de verdad: `docs/GHistorias de usuarios.xml`):

| Rol | Quien es | Acceso general |
|---|---|---|
| **ADMINISTRADOR** | Dueño/gobierno de la clinica | Todo. Crea cuentas, asigna roles, resetea contrasenas, desactiva personal. En Recetas es solo lectura (auditoria legal). No ve la seccion Consulta en el sidebar (la deja solo para el medico); puede acceder a historicos por URL (auditoria). |
| **MEDICO** | Doctor que atiende pacientes | Panel propio, Pacientes, Citas (solo las suyas), Consulta Medica (crear/editar), Recetas (crear/editar/dispensar), Inventario/Categorias (solo lectura). No ve ni Medicos ni Usuarios. |
| **RECEPCIONISTA** | Operador del dia a dia | Panel, Pacientes, Citas (todas), Medicos, Recetas (solo impresion/dispensar), Inventario y Categorias. No ve Consulta Medica ni Usuarios. |

### Sidebar por rol

| Modulo | ADMIN | MEDICO | RECEPCIONISTA |
|---|---|---|---|
| Panel Principal | Si | Si (vista propia) | Si |
| Pacientes | Si | Si (solo contacto/citas) | Si (solo contacto/citas) |
| Medicos | Si | No | Si |
| Citas | Si (todas) | Si (solo las suyas) | Si (todas) |
| Consulta Medica | No (no aparece en el sidebar) | Si (crear/editar) | No |
| Recetas | Si (solo lectura) | Si (crear/editar) | Si (solo imprimir) |
| Inventario | Si | Solo lectura | Si |
| Categorias | Si | Solo lectura | Si |
| Usuarios | Si | No | No |
| Configuracion | Si | Si | Si |

---

## Descripcion de cada vista

### 1. Login (`/login` — `HomeController`)
**Historia:** SCRUM-116 — Inicio de Sesion

Formulario de acceso con correo y contrasena. Incluye boton para mostrar/ocultar contrasena. Muestra mensaje de error si las credenciales son incorrectas. Tras login exitoso redirige a `/panel`.

### 2. Panel Principal / Dashboard (`/panel` — `HomeController`)
**Historia:** SCRUM-116 (subtarea SCRUM-174)

Vista general con metricas dinamicas:
- Citas de hoy
- Pacientes recientes
- Informes pendientes
- Volumen semanal
- Proximas citas

El contenido cambia segun el rol: el **medico** ve su resumen clinico personal (sus citas, sus pacientes, sus consultas pendientes). El **recepcionista** y el **admin** ven el flujo general de la clinica.

### 3. Gestion de Usuarios (`/usuarios` — `UsuarioController`)
**Historia:** SCRUM-116 (subtarea SCRUM-122)

- **Solo ADMINISTRADOR** puede acceder (`@PreAuthorize`).
- CRUD completo: listar, crear, editar, restablecer contrasena, activar/desactivar.
- Busqueda por nombre, filtro por rol y estado, paginacion (8 por pagina).
- Validacion: correo unico, contrasena minimo 8 caracteres, no puede desactivar su propia cuenta.

### 4. Gestion de Pacientes (`/pacientes` — `PacienteController`)
**Historia:** SCRUM-117 — Registro de pacientes

- **Roles:** ADMINISTRADOR, MEDICO, RECEPCIONISTA.
- Directorio con busqueda por nombre o documento de identidad.
- Formulario con campos obligatorios: Nombres, Apellidos, Documento de Identidad, Fecha de Nacimiento, Telefono, Genero.
- **Validaciones:**
  - Documento de identidad unico (validacion en vivo via endpoint `/pacientes/existe-documento`).
  - Fecha de nacimiento no puede ser futura (`@Past`).
  - Al guardar se genera automaticamente un codigo de expediente unico (`MC-<anio>-<secuencial>`).

### 5. Gestion de Medicos (`/medicos` — `MedicoController`)
**Historia:** SCRUM-118 — Registros medicos

- **Roles:** ADMINISTRADOR, RECEPCIONISTA.
- Directorio de personal medico con especialidad, numero de licencia y disponibilidad.
- Formulario: Nombre completo, Especialidad, Numero de Licencia, Telefono, Correo.
- **Validaciones:**
  - Numero de licencia unico (endpoint `/medicos/existe-licencia`).
  - Al registrar un medico se crea automaticamente su Usuario con rol MEDICO (contrasena = correo).
- **Toggle de disponibilidad** via endpoint `/medicos/{id}/toggle-disponibilidad` (AJAX).

### 6. Agenda de Citas (`/citas` — `CitaController`)
**Historia:** SCRUM-119 — Agendamiento de citas + SCRUM-7 — Modificacion/Reagendamiento/Cancelacion

- **Roles:** ADMINISTRADOR, MEDICO, RECEPCIONISTA.
- Vista tipo calendario con bloques de tiempo por medico.
- **Programar:** Seleccionar paciente, medico, fecha/hora y duracion. El sistema valida disponibilidad del medico (`CitaService.verificarDisponibilidad()`) y previene duplicidad de horarios.
- **Estado inicial:** `PROGRAMADA`.
- **Reagendar:** Cambia fecha/hora y estado a `REAGENDADA`, registra en historial_citas.
- **Cancelar:** Cambia estado a `CANCELADA`, registra motivo en historial_citas. Libera el bloque de horario.
- El **medico** solo ve y gestiona sus propias citas. La recepcionista ve todas.

### 7. Consulta Medica (`/consultas` — `ConsultaMedicaController`)
**Historia:** SCRUM-6 — Registro de consulta medica

- **Roles:** MEDICO (crear/editar/ver), ADMINISTRADOR (solo lectura historica).
- **El medico ve SOLO sus propias consultas** en el listado (filtrado por su usuario/medico). El ADMINISTRADOR ve todas (auditoria).
- Acciones por fila: **Ver detalles** (pagina de detalle con motivo, sintomas, diagnostico, datos de cita/paciente y medicamentos recetados), **Editar** y **Eliminar** (solo ADMIN).
- **El medico puede iniciar una consulta desde `/consultas`**: boton "Nueva Consulta" que muestra una lista de citas pendientes (estado `PROGRAMADA` o `REAGENDADA` sin consulta registrada) del propio medico; al elegir una cita se abre el formulario.
- Formulario SOAP: Motivo de Consulta, Sintomatologia, Diagnostico. **No incluye signos vitales** (alineado al criterio del JIRA SCRUM-6).
- Botones: **"Guardar Borrador"** (guarda y vuelve al historial) y **"Guardar y Emitir Receta"** (guarda la consulta y redirige a `/recetas/consulta/{id}/agregar` para agregar el primer medicamento de esa consulta). Alineado con SCRUM-6, que incluye la receta dentro del registro de consulta.
- Se asocia a una Cita especifica (one-to-one: una cita = una consulta).
- Al guardar la consulta, la **cita pasa automaticamente a estado `ATENDIDA`**.
- No se puede crear una consulta para una cita que ya tiene una registrada (`findByCita`).

### 8. Receta Medica (`/recetas` — `RecetaController`)
**Historia:** SCRUM-110 — Receta medica + SCRUM-111 — Detalle de receta

- **Roles:** MEDICO (crear/editar/eliminar), RECEPCIONISTA y ADMINISTRADOR (solo dispensar/ver).
- Listado de recetas **agrupado por consulta**: cada grupo muestra paciente + Cita (fecha/hora) + medico, y debajo los medicamentos prescritos (Medicamento, Dosis, Frecuencia, Duracion, Notas, Estado).
- **Crear receta:** Se asocia a una Consulta. Al abrir `/recetas/nueva`, el desplegable de consultas muestra **solo las consultas del medico conectado**, con contexto (Paciente + fecha/hora de la cita) para distinguirlas. Si no hay consultas, se muestra un aviso que enlaza a `/consultas`.
- La receta queda vinculada al modelo asi: **Cita (1:1) -> ConsultaMedica (via id_consulta) -> RecetaDetalle(s)**. La receta NO se vincula directamente a la cita; se vincula a la consulta que a su vez guarda la cita.
- **Dispensar:** La recepcionista marca la receta como `DISPENSADA`, lo que descuenta stock del inventario via `Medicamento.actualizarStock()` y genera un `MovimientoInventario`.
- **Vista imprimible:** Presentacion tipo "prescription pad" con encabezado de clinica, datos del medico y paciente.

### 9. Inventario de Medicamentos (`/medicamentos` — `MedicamentoController`)
**Historia:** SCRUM-11 — Registro de medicina

- **Roles:** ADMINISTRADOR, RECEPCIONISTA (gestionar), MEDICO (solo lectura).
- Lista todos los medicamentos con nombre comercial/generico, categoria, stock, fecha de vencimiento y estado.
- **Tarjetas de metricas:** total de articulos, stock bajo, proximos a vencer.
- **Alertas visuales por fila:**
  - Stock con badge de color (<=10 rojo, <=30 ambar, resto verde).
  - Estado Vigente/Vencido via `Medicamento.validarCaducidad()`.
- **Validaciones:**
  - Nombre comercial obligatorio y unico.
  - Categoria obligatoria y debe existir.
  - Fecha de vencimiento posterior a hoy.
- Eliminacion protegida: solo si no tiene registros asociados (recetas o movimientos).

### 10. Categorias de Medicamentos (`/categorias-medicamentos` — `CategoriaMedicamentoController`)
**Historia:** SCRUM-5 — Gestion de categorias de Medicamentos

- **Roles:** ADMINISTRADOR, RECEPCIONISTA (gestionar), MEDICO (solo lectura).
- CRUD: tabla con ID, nombre, descripcion y estado.
- **Estado:** `Activa` (sin medicamentos) o `En uso` (con medicamentos asociados), calculado por `CategoriaMedicamentoService.tieneMedicamentosAsociados()`.
- Validaciones: nombre obligatorio y unico.
- No se puede eliminar una categoria con medicamentos asociados.

### 11. Configuracion / Mi Perfil (`/configuracion` — `ConfiguracionController`)
**Historia:** SCRUM-121

- **Todos los roles** acceden (cada quien ve su propio perfil).
- Cambio de nombre, telefono y foto de perfil.
- Subida de imagen (max 3MB, formatos JPG/PNG/WebP) almacenada como base64.

---

## Flujo principal de negocio (ejemplo completo)

### Flujo 1: Paciente nuevo + primera cita
```
1. Recepcionista registra paciente en /pacientes → se genera expediente MC-2026-001
2. Recepcionista programa cita en /citas → selecciona paciente, medico, fecha → estado PROGRAMADA
3. HistorialCita registra: quien agendo, cuando, y estado inicial
```

### Flujo 2: Atencion medica completa
```
1. Medico ve sus citas de hoy en /panel
2. En /consultas pulsa "Nueva Consulta", elige su cita pendiente (PROGRAMADA/REAGENDADA sin consulta)
   y entra al formulario SOAP -> registra motivo, sintomas, diagnostico
3. Pulsa "Guardar y Emitir Receta" -> la consulta se guarda, la cita pasa a ATENDIDA y
   el sistema lo lleva a /recetas/consulta/{id}/agregar para agregar el primer medicamento
4. Agrega los medicamentos del catalogo sobre esa consulta
5. Recepcionista dispensa receta en /recetas/{id}/dispensar -> stock descuenta
6. Se genera MovimientoInventario de tipo SALIDA
```

### Flujo 3: Cancelacion/Reagendamiento
```
1. Recepcionista cancela cita en /citas → estado pasa a CANCELADA
2. HistorialCita registra: motivo, usuario, fecha del cambio
3. El bloque de horario queda libre para reasignar
```

### Flujo 4: Control de inventario
```
1. Recepcionista registra compra en /medicamentos/nuevo → MovimientoInventario ENTRADA
2. Stock se incrementa via Medicamento.actualizarStock()
3. Si un medicamento esta por vencer o con stock bajo, el sistema alerta en la vista
```

### Flujo 5: Alta de personal
```
1. Admin crea usuario en /usuarios/nuevo → asigna rol
2. Si el rol es MEDICO, tambien se crea su registro en /medicos
3. Queda disponible para login
```

---

## Seguridad

### Control de acceso URL (Spring Security)
Cada controlador tiene `@PreAuthorize` que restringe el acceso por rol. Ademas, `SecurityConfig` define reglas a nivel de URL:

| Ruta | Roles permitidos |
|---|---|
| `/login` | Publico |
| `/panel` | Autenticado |
| `/usuarios/**` | ADMINISTRADOR |
| `/medicos/**` | ADMINISTRADOR, RECEPCIONISTA |
| `/citas/**` | ADMINISTRADOR, MEDICO, RECEPCIONISTA |
| `/consultas/**` | MEDICO, ADMINISTRADOR |
| `/recetas/**` | MEDICO, RECEPCIONISTA, ADMINISTRADOR |
| `/medicamentos/**` | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| `/categorias-medicamentos/**` | ADMINISTRADOR, RECEPCIONISTA, MEDICO |
| `/pacientes/**` | ADMINISTRADOR, MEDICO, RECEPCIONISTA |
| `/configuracion` | Todos los roles |

### Nota de seguridad
El sidebar oculta opciones por rol via Thymeleaf `th:if`, pero la proteccion real viene de `@PreAuthorize` en controladores y `SecurityConfig` a nivel URL. Un usuario que escriba directamente la URL no podra acceder si no tiene el rol adecuado.

---

## Capa de datos

- **10 entidades JPA** en `modelos/`
- **4 enums:** RolUsuario, EstadoCita, EstadoReceta, TipoMovimiento
- **10 repositorios** en `repositorios/`
- **11 interfaces de servicio** + **11 implementaciones** en `servicios/`
- **10 controladores** en `controladores/`
- **22 templates Thymeleaf** + 1 fragmento base
- **Base de datos:** SQL Server (SistemaClinico), script `db.sql`
