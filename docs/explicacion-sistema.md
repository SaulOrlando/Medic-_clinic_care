# MediClinic Care — Explicación del sistema

## Estado actual del proyecto

**Funcionando hoy:**
- Login (`login.html`) con autenticación Spring Security.
- Panel/Home (`panel.html` + `fragments/base.html`): sidebar que filtra según rol, header con nombre y rol del usuario logueado, dashboard con métricas dinámicas (médico ve solo lo suyo, el resto ve el flujo general de la clínica).
- Capa de datos completa: 10 entidades JPA, 10 repositorios, servicios de lógica de negocio (citas, recetas, inventario, etc.) ya implementados con validaciones.
- **Inventario de Medicamentos y Categorías**: controladores, vistas y servicios funcionales (`/medicamentos` y `/categorias-medicamentos`). CRUD completo de medicamentos y categorías con alertas de stock bajo y vencimiento.
- **Gestión de Usuarios** (`/usuarios`): listado con búsqueda, filtros por rol/estado y paginación; alta/edición, restablecer contraseña y activar/desactivar cuentas. Acceso restringido a Admins.
- **Mi Perfil** (`/configuracion`): cambiar nombre, teléfono y foto de perfil.
- Seguridad por URL y por método: `SecurityConfig` + `@EnableMethodSecurity` + `@PreAuthorize` en controladores sensibles.

**No está terminado:** los controladores y vistas de Pacientes, Citas, Consulta, Recetas y Médicos. Los ítems del sidebar que apuntan a esas rutas (`/pacientes`, `/citas`, `/consultas`, `/recetas`, `/medicos`) aún arrojan 404 porque no tienen controlador detrás. Cuando se creen, hay que seguir el patrón ya usado: controlador + vista Thymeleaf + `@PreAuthorize` según la tabla de permisos.

## Roles del sistema

Los roles existen en `RolUsuario`: `MEDICO`, `RECEPCIONISTA`, `ADMINISTRADOR` y `ENCARGADO_INVENTARIO` (rol dedicado a farmacia).

## Qué hace la página según cada rol

### ADMINISTRADOR (Iliana Melgar)
Ve **todo el sidebar**: Panel, Pacientes, Médicos, Citas, Consulta, Recetas, Inventario, Categorías, Usuarios y Configuración. Es el rol de gobierno: crea cuentas, asigna roles, resetea contraseñas y desactiva personal. En Consulta/Recetas accede **solo lectura** por cumplimiento legal (auditar que el médico registró bien). En el dashboard ve el flujo general de la clínica.

### MÉDICO (Saul Tobar, Camila Fuentes, Roberto Campos)
Ve: Panel, Pacientes, Citas, Consulta, Recetas, Inventario, Categorías y Configuración. **No ve** Médicos, Usuarios ni la gestión de personal. En su Panel ve **su** resumen clínico: sus citas de hoy, sus pacientes de la semana, sus consultas pendientes y su agenda. Trabaja en Consulta Médica (registra signos, motivo, diagnóstico, notas) y genera Recetas. En Inventario/Categorías debería tener acceso **solo lectura** (para saber qué hay disponible) — ver nota en la sección de Inventario.

### RECEPCIONISTA (Fabiola Cortez)
Ve: Panel, Pacientes, Médicos, Citas, Recetas (solo para imprimir), Inventario, Categorías y Configuración. Es el operador del día a día: agenda, programa/cancela/reagenda citas para cualquier médico, registra pacientes (solo datos de contacto/citas), consulta el directorio de médicos y despacha farmacia.

### ENCARGADO_INVENTARIO (Guadalupe Sion)
Ve: Panel, Inventario, Categorías y Configuración. Controla stock, registra entradas/salidas/ajustes de inventario, vigila caducidades y gestiona categorías.

## Módulo: Inventario de Medicamentos y Categorías

> **Función:** control de stock de la farmacia interna de la clínica, alertas de caducidad y organización por categorías.
> **Roles:** Administrador y Recepcionista (encargados de farmacia) lo gestionan; los médicos tienen acceso de **solo lectura** para saber qué hay disponible. El rol `ENCARGADO_INVENTARIO` es el dedicado exclusivamente a la farmacia.

### Inventario (`/medicamentos` → `MedicamentoController`)
- Lista todos los medicamentos con nombre comercial/genérico, categoría, presentación, stock, fecha de vencimiento y estado.
- **Tarjetas de métricas** al inicio: total de medicamentos, medicamentos con **stock bajo** (≤ 10), **vencidos** (fecha anterior a hoy) y número de **categorías** en uso.
- **Alertas visuales** por fila: el stock se muestra con badge de color (≤ 10 rojo, ≤ 30 ámbar, resto verde) y el estado del medicamento como **Vigente/Vencido** usando `Medicamento.validarCaducidad()`.
- Alta/edición con `medicamentos-form.html`: valida nombre comercial obligatorio y único, categoría asociada existente y fecha de vencimiento posterior a hoy (`MedicamentoService.crearMedicamento()`).
- Eliminación protegida: `MedicamentoService.eliminarMedicamento()` permite borrar solo si el medicamento **no** tiene registros asociados (recetas o movimientos de inventario).

### Categorías (`/categorias-medicamentos` → `CategoriaMedicamentoController`)
- CRUD de categorías en una sola vista (`categorias-medicamentos.html`): tabla con ID, nombre, descripción y estado.
- Estado **Activa** (sin medicamentos asociados) o **En uso** (tiene medicamentos), calculado por `CategoriaMedicamentoService.tieneMedicamentosAsociados()`.
- Validaciones: nombre obligatorio y único (`crearCategoria()` y `editarCategoria()`); **no se puede eliminar** una categoría que tenga medicamentos asociados (`eliminarCategoria()` devuelve `FALSE`).

> **Nota de permisos:** hoy tanto `SecurityConfig` como el `@PreAuthorize` de `MedicamentoController` abren el módulo al grupo `ADMINISTRADOR / RECEPCIONISTA / MEDICO / ENCARGADO_INVENTARIO`, y dentro del grupo *todos* pueden crear/editar/eliminar. El "solo lectura para médicos" es la intención de diseño y **todavía no está aplicado** por método: faltaría restringir los endpoints de escritura con `@PreAuthorize` específico.

## Vistas implementadas

| Vista | Ruta | Estado | Qué hace |
|---|---|---|---|
| **Login** | `/login` | ✅ | Autenticación con Spring Security (form-login). |
| **Panel** | `/panel` | ✅ | Dashboard dinámico por rol: citas de hoy, pacientes recientes, informes pendientes, gráfica de volumen semanal y próximas citas. |
| **Inventario** | `/medicamentos` | ✅ | Lista + tarjetas de métricas + alertas de stock bajo/vencidos; alta, editar y eliminar medicamentos. |
| **Categorías** | `/categorias-medicamentos` | ✅ | CRUD de categorías, estado Activa/En uso, protección de borrado con medicamentos asociados. |
| **Usuarios** | `/usuarios` | ✅ | Listado con búsqueda, filtros y paginación; crear/editar, restablecer contraseña, activar/desactivar. Solo Admin. |
| **Configuración** | `/configuracion` | ✅ | Perfil propio: cambiar nombre, teléfono y foto de perfil (JPG/PNG/WebP, máx. 3 MB). |

## Vistas pendientes (qué harán)

| Vista | Ruta prevista | Qué hará |
|---|---|---|
| **Pacientes** | `/pacientes` | Lista con búsqueda por nombre/DUI. Crear, editar, ver expediente, historial de citas y consultas. |
| **Citas** | `/citas` | Calendario/agenda filtrable por médico o especialidad. Botones programar / reprogramar / cancelar. Al agendar valida disponibilidad (por eso existe `Cita.estaDisponible()` y `CitaService`). |
| **Consulta Médica** | `/consultas` | Form del médico: signos vitales, motivo, sintomatología, diagnóstico, notas. Por cita partiendo del historial. |
| **Recetas** | `/recetas` | Prescripción: por consulta se agregan medicamentos con dosis/frecuencia/duración; vista imprimible para recepcionista. |
| **Médicos** | `/medicos` | Directorio, especialidad, licencia, disponibilidad, asignación de consultorios/horarios. |

## Procesos de ejemplo (flujo completo)

**1. El paciente va a la clínica a pedir cita:**
> La recepcionista (Fabiola) abre `/citas` → filtra por especialidad "Cardiología" → el sistema consulta `CitaService.programar()` que valida que el médico tenga `disponible=TRUE` y que no exista otra cita (`existsByMedicoIdMedicoAndFechaHoraAndEstadoNot`). Si pasa, crea la Cita con `estado=PROGRAMADA` y afecta a un Paciente y al `usuarioGestor` (ella). Si la agenda del médico ya está llena, no se puede.

**2. Cita cancelada/reagendada:**
> La recepcionista cancela → la Cita pasa a `CANCELADA` y se escribe un registro en `historial_citas` (quién, cuándo, estado anterior → nuevo, motivo). Igual al reagendar: `Cita.reagendarCita()` cambia fecha y estado a `REAGENDADA`, dejando trazabilidad.

**3. El médico atiende a un paciente:**
> Saul llega su cita de hoy (la ve en su Panel: "Mis Citas") → entra a `/consultas` → `ConsultaMedicaService` registra motivo, sintomatología y diagnóstico ligados a la Cita (`one-to-one: una cita = una consulta`). La cita pasa a `ATENDIDA`. Ese registro queda para auditoría del admin.

**4. Receta:**
> En la consulta, Saul prescribe → `RecetaDetalleService` agrega medicamentos con cantidad e indicaciones y consulta el stock en el inventario (`estado=PRESCRITA`). Si la farmacia despacha, pasa a `DISPENSADA` y `Medicamento.actualizarStock()` descuenta, generando un `MovimientoInventario` (la recepcionista puede imprimir la receta si el paciente la perdió).

**5. Inventario:**
> La encargada (Guadalupe) registra un medicamento nuevo o una compra → `MedicamentoService` valida nombre único, categoría existente y fecha de vencimiento válida; `MovimientoInventarioService` suma a `stock_disponible` y crea un movimiento `ENTRADA`. Si un medicamento está por vencer o con stock bajo, la vista `/medicamentos` lo resalta con alertas.

**6. Alta de personal:**
> El admin (Iliana) crea un usuario con su rol → `UsuarioService.crearUsuario()` valida que el correo no exista y los datos obligatorios; queda disponible para login. Si es MEDICO, además se crea su registro en `medicos` para que tenga agenda propia.

## Resumen de la seguridad

El `SecurityConfig` ya protege por URL:
- `/login`, `/css/**`, `/js/**`, `/images/**` — públicos.
- `/usuarios` y `/usuarios/**` — solo `ROLE_ADMINISTRADOR`.
- `/medicamentos/**` y `/categorias-medicamentos/**` — `ADMINISTRADOR`, `RECEPCIONISTA`, `MEDICO`, `ENCARGADO_INVENTARIO`.
- Cualquier otra ruta — requiere autenticación. Al denegarse el acceso se redirige a `/panel`.

Además está activo `@EnableMethodSecurity` y los controladores sensibles usan `@PreAuthorize` a nivel de clase (`UsuarioController` → `hasRole('ADMINISTRADOR')`, `MedicamentoController` → `hasAnyRole(...)`).

**Pendientes:**
- Cuando se creen Pacientes, Citas, Consulta, Recetas y Médicos, añadir su `@PreAuthorize` según la tabla de permisos (p. ej., crear usuarios solo `ROLE_ADMINISTRADOR`, `Consultas` solo `MEDICO`/`ADMIN`), porque aunque el **sidebar ya oculta** opciones por rol, un usuario inteligente podría escribir la URL directamente si no hay controlador de seguridad detrás.
- Aplicar el "solo lectura" real para médicos en Inventario/Categorías (hoy el grupo completo puede crear/editar/eliminar).
- **Nota:** el `PasswordEncoder` es `PlainTextPasswordEncoder` (contraseñas en texto plano en BD); conviene migrar a hash (p. ej., BCrypt).