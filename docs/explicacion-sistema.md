# MediClinic Care — Explicación del sistema

## Estado actual del proyecto

**Funcionando hoy:**
- Login (`login.html`) con autenticación Spring Security.
- Panel/Home (`panel.html` + `fragments/base.html`): sidebar que filtra según rol, header con nombre y rol del usuario logueado, dashboard con métricas dinámicas (médico ve solo lo suyo, el resto ve el flujo general de la clínica).
- Capa de datos completa: 10 entidades JPA, 10 repositorios, servicios de lógica de negocio (citas, recetas, inventario, etc.) ya implementados con validaciones.

**No está terminado:** los controladores y vistas de Pacientes, Citas, Consulta, Recetas, Médicos, Usuarios e Inventario. Las reglas de permisos por URL vía `@PreAuthorize` todavía no existen (todo autenticado puede entrar a `/panel`; el resto de rutas ni siquiera tienen controlador).

## Qué hace la página según cada rol

### ADMINISTRADOR (Iliana Melgar)
Ve **todo el sidebar**: Panel, Pacientes, Médicos, Citas, Consulta, Recetas, Inventario, Categorías, Usuarios y Configuración. Es el rol de gobierno: crea cuentas, asigna roles, resetea contraseñas y desactiva personal. En Consulta/Recetas accede **solo lectura** por cumplimiento legal (auditar que el médico registró bien). En el dashboard ve el flujo general de la clínica.

### MÉDICO (Saul Tobar, Camila Fuentes, Roberto Campos)
Ve: Panel, Pacientes, Citas, Consulta, Recetas, Inventario (solo lectura) y Configuración. **No ve** Médicos, Usuarios ni Categorías/edit. En su Panel ve **su** resumen clínico: sus citas de hoy, sus pacientes de la semana, sus consultas pendientes y su agenda. Trabaja en Consulta Médica (registra signos, motivo, diagnóstico, notas) y genera Recetas.

### RECEPCIONISTA (Fabiola Cortez)
Ve: Panel, Pacientes, Citas, Médicos, Recetas (solo para imprimir), Inventario y Categorías. Es el operador del día a día: agenda, programa/cancela/reagenda citas para cualquier médico, registra pacientes (solo datos de contacto/citas), consulta el directorio de médicos y despacha farmacia.

### ENCARGADO_INVENTARIO (Guadalupe Sion)
Ve: Panel, Inventario y Categorías. Controla stock, registra entradas/salidas/ajustes y vigila caducidades.

## Vistas (las que faltan y qué harán)

| Vista | Ruta prevista | Qué hará |
|---|---|---|
| **Pacientes** | `/pacientes` | Lista con búsqueda por nombre/DUI. Crear, editar, ver expediente, historial de citas y consultas. |
| **Citas** | `/citas` | Calendario/agenda filtrable por médico o especialidad. Botones programar / reprogramar / cancelar. Al agendar valida disponibilidad (por eso existe `Cita.estaDisponible()` y `CitaService`). |
| **Consulta Médica** | `/consultas` | Form del médico: signos vitales, motivo, sintomatología, diagnóstico, notas. Por cita partiendo del historial. |
| **Recetas** | `/recetas` | Prescripción: por consulta se agregan medicamentos con dosis/frecuencia/duración; vista imprimible para recepcionista. |
| **Inventario** | `/medicamentos` | Lista por categoría, stock disponible, alerta de caducidad (es lo que hace `Medicamento.actualizarStock()` junto con `MovimientoInventarioService`). |
| **Médicos** | `/medicos` | Directorio, especialidad, licencia, disponibilidad, asignación de consultorios/horarios. |
| **Usuarios** | `/usuarios` | CRUD de cuentas del admin: crear, asignar rol, resetear contraseña, desactivar. La de mayor jerarquía. |
| **Configuración** | `/configuracion` | Perfil propio: cambiar nombre, teléfono, imagen. |

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
> La encargada (Guadalupe) registra una compra → `MovimientoInventarioService` valida el medicamento y el usuario responsable, suma a `stock_disponible` y crea movimiento `ENTRADA`. Si un medicamento está por vencer, el sistema lo alerta en la vista.

**6. Alta de personal:**
> El admin (Iliana) crea un usuario con su rol → `UsuarioService.crearUsuario()` valida que el correo no exista y los datos obligatorios; queda disponible para login. Si es MEDICO, además se crea su registro en `medicos` para que tenga agenda propia.

## Resumen de la seguridad que falta resaltar

El `SecurityConfig` hoy solo exige login. Cuando se creen los controladores hay que añadir `@PreAuthorize` según la tabla de permisos (p. ej., crear usuarios solo `ROLE_ADMINISTRADOR`), porque aunque el **sidebar ya oculta** opciones por rol, un usuario inteligente aún podría escribir `/usuarios` en la URL si no hay controlador de seguridad detrás.