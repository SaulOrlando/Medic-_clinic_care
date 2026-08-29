# AGENTS.md — MediClinic Care

## Project Overview

Spring Boot medical clinic management system for scheduling appointments, managing patient records, and tracking consultations. Uses SQL Server, Thymeleaf, and Spring Data JPA.

- **Stack:** Java 25, Spring Boot 4.1.0, Maven, SQL Server (mssql-jdbc), Thymeleaf, Jakarta Persistence (Hibernate)
- **Entry point:** `src/main/java/org/esfe/MediclinicAppApplication.java`

## Functional Modules & Role Permissions

### Roles del sistema
| Rol | Código |
|---|---|
| Administrador | `ROLE_ADMINISTRADOR` |
| Médico | `ROLE_MEDICO` |
| Recepcionista | `ROLE_RECEPCIONISTA` |

### Módulos

| # | Módulo | Función | Roles |
|---|---|---|---|
| 2 | **Panel Principal (Dashboard)** | Vista general del estado de la clínica. Métricas clave (citas hoy, pacientes recientes, informes pendientes), gráfica de volumen semanal y agenda próxima. La info es dinámica: el médico ve su resumen clínico; el recepcionista ve el flujo general. | Todos |
| 3 | **Agenda de Citas** | Gestión del calendario. Programar, cancelar o reprogramar citas médicas, filtrando por disponibilidad de médicos o especialidades. | Recepcionista y Médico. Admin con visibilidad total. |
| 4 | **Gestión de Pacientes** | Directorio central con historias clínicas, antecedentes médicos y datos de contacto. | Médico y Recepcionista (solo datos de contacto/citas). Admin para auditoría. |
| 5 | **Consulta Médica** | Interfaz de trabajo del médico: registrar signos vitales, motivos de consulta, diagnósticos y notas clínicas. | Exclusivo Médicos. Admin acceso a registros históricos (cumplimiento legal). |
| 6 | **Receta Médica** | Generación de prescripciones digitales con dosis, frecuencia y duración del tratamiento. | Exclusivo Médicos. Recepcionista solo lectura para impresión. |
| 7 | **Inventario de Medicamentos y Categorías** | Control de stock, alertas de caducidad, organización por categorías. | Admin y Recepcionista (farmacia). Médicos solo lectura. |
| 8 | **Gestión de Médicos** | Directorio de personal médico, especialidades, horarios y asignación de consultorios. | Admin y Recepcionista. |
| 9 | **Gestión de Usuarios (Personal)** | Creación de cuentas, asignación de roles, restablecimiento de contraseñas, desactivación de personal. | Exclusivo Administrador. |
| 10 | **Mi Perfil y Ajustes** | Cambio de nombre, imagen de perfil o teléfono. Acceso rápido para que el admin registre nuevos usuarios. | Todos (cada quien ve su propio perfil). |

> **IMPORTANTE:** Al implementar cualquier funcionalidad, verificar la tabla de permisos por rol antes de crear controladores, vistas o endpoints. Los endpoints sensibles deben validarse con `@PreAuthorize` o equivalente.

## Setup Commands

```bash
# Compile
./mvnw compile

# Run the application (local profile — credentials from application-local.properties)
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"

# Run the application (default profile — uses fallback credentials in application.properties)
./mvnw spring-boot:run

# Run tests
./mvnw test
```

The application starts on **http://localhost:8081**.

> **Note:** Credentials live in `application-local.properties` (gitignored). `application.properties` uses `${DB_USERNAME}` / `${DB_PASSWORD}` placeholders with fallback defaults — never commit real secrets. Use `db.sql` to create the schema.

## Project Structure

```
src/main/java/org/esfe/
├── MediclinicAppApplication.java
├── modelos/                  # JPA entities (10 classes) + enums/ (4 enums)
├── repositorios/             # Spring Data JPA repositories (10 interfaces)
├── servicios/
│   interfaces/               # Service interfaces (TO DO)
│   implementaciones/         # Service implementations (TO DO)
├── controladores/            # Controllers (TO DO)
└── security/                 # Security config (TO DO)
```

## Architecture & Conventions

### Layers (follow this order)

1. **Modelos** (`modelos/`) — JPA entities with `@Entity`, `@Table`, Jakarta Validation annotations, manual getters/setters (no Lombok). Each entity maps to a SQL Server table.
2. **Repositorios** (`repositorios/`) — Interfaces extending `JpaRepository<Entity, Integer>`. Named with `I` prefix (e.g., `IUsuarioRepository`). Custom queries use Spring Data method naming or `@Query`.
3. **Servicios** (`servicios/interfaces/` + `servicios/implementaciones/`) — Interface in `interfaces/`, implementation in `implementaciones/` with `@Service`.
4. **Controladores** (`controladores/`) — Spring MVC controllers with `@Controller` for Thymeleaf views.
5. **Security** (`security/`) — Spring Security configuration.

### Code Style

- No Lombok — all getters/setters written manually
- Entities include domain methods (e.g., `Cita.programarCita()`, `Medicamento.actualizarStock()`)
- Enums stored as `@Enumerated(EnumType.STRING)` with `length = 30` or `length = 20`
- All relationships use `FetchType.LAZY` on `@ManyToOne` and `@OneToOne`
- Foreign keys in DB are represented as object references in entities (e.g., `@ManyToOne Paciente paciente` instead of `Integer idPaciente`)
- Table/column names use snake_case; Java fields use camelCase
- `@Column(name = "...")` always specified explicitly

### Naming Conventions

| Element | Convention | Example |
|---|---|---|
| Entity class | PascalCase | `CategoriaMedicamento` |
| Repository interface | `I` + Entity + `Repository` | `ICategoriaMedicamentoRepository` |
| Service interface | `I` + Entity + `Service` | `ICategoriaMedicamentoService` |
| Service implementation | Entity + `Service` | `CategoriaMedicamentoService` |
| Controller | Entity + `Controller` | `CategoriaMedicamentoController` |
| Enum | PascalCase | `RolUsuario`, `EstadoCita` |

### Database

- **DBMS:** SQL Server
- **Schema script:** `db.sql` at project root creates the `SistemaClinico` database
- **DDL:** 9 tables — `usuarios`, `medicos`, `pacientes`, `citas`, `historial_citas`, `consultas_medicas`, `categorias_medicamentos`, `medicamentos`, `movimientos_inventario`, `recetas_detalles`
- **DIAGRAMS:** `src/diagramas/clases.puml` (class diagram) and `src/diagramas/diagramabasededatos.puml` (ER diagram) are the source of truth

## Testing

```bash
./mvnw test
```

- Tests are in `src/test/java/org/esfe/`
- Currently only the default Spring Boot context-load test exists
- Add unit tests for services and integration tests for repositories

## PR Instructions

- Run `./mvnw compile` before committing to verify no compilation errors
- Run `./mvnw test` to ensure tests pass
- Follow existing code conventions — no Lombok, manual getters/setters, `I` prefix on interfaces
- When adding new entities, always update both the class diagram and ER diagram in `src/diagramas/`

## Referencia de Diseño — Stitch (fuente de verdad visual)

> **LEE ESTO ANTES DE IMPLEMENTAR CUALQUIER VISTA.** Los bocetos (UI) aprobados viven en un
> proyecto **Stitch** al que **NO tendrás acceso al MCP**. Para no depender de él, las capturas y
> el HTML de cada pantalla se descargaron y quedaron en **`docs/stitch/`**. Usa esos archivos para
> replicar el diseño (estructura, campos, layout, colores, estados) en Thymeleaf con el sistema de
> diseño CSS existente (`/css/design-system.css`, `/css/layout.css`…) y el fragmento base
> `fragments/base.html`.

**Proyecto Stitch:** `MediClinic Management System` (ID `3044856348708812253`).

### Archivos de referencia

| Vista | Ruta del controlador | Referencia Stitch (`docs/stitch/`) |
|---|---|---|
| Categorías de Medicamentos | `CategoriaMedicamentoController` (existe) | `categorias.html` / `categorias.png` |
| Gestión de Pacientes | `PacienteController` (pendiente) | `pacientes.html` / `pacientes.png` |
| Gestión de Médicos | `MedicoController` (pendiente) | `medicos.html` / `medicos.png` |
| Consulta Médica | `ConsultaMedicaController` (pendiente) | `consulta.html` / `consulta.png` |
| Receta Médica | `RecetaController` (pendiente) | `recetas.html` / `recetas.png` |
| Inventario de Medicamentos | `MedicamentoController` (existe) | `inventario.html` / `inventario.png` |

> Los `.html` son autónomos (Tailwind vía CDN) — **NO los copies tal cual**. Son la referencia
> visual/estructural: traduce su markup a los componentes Thymeleaf + CSS del proyecto. Los `.png`
> son la captura renderizada.

### Design System (tokens a respetar)

Los bocetos usan el diseño **"Clinical Precision"** ya reflejado en `design-system.css`:
- **Colores:** Primary `#1976D2`, Secondary `#2E7D32` (verde "saludable"), Error `#D32F2F` (alertas críticas), Amber `#FFA000` (pendientes/aviso).
- **Tipografía:** `Inter`, 14px base (`body-md`) con tabular figures para datos numéricos.
- **Formas:** radios suaves ~4px; badges de estado con radio alto (pill).
- **Layout:** sidebar fija 260px; contenido fluido; tablas a ancho completo con badges de estado.
- **Badges de estado:** Programada = azul, Atendida = verde, Cancelada = gris, Reagendada = ámbar.

---

### Guía de implementación por vista

#### 1. Categorías de Medicamentos (`/categorias-medicamentos`, Ya implementado)
- **Stitch:** `docs/stitch/categorias.html`. Layout: breadcrumb "Inventario › Categorías", botón "Nueva Categoría", barra de **búsqueda + filtros + exportar**, tabla (Nombre, Descripción, Medicamentos Activos, Estado, Acciones), panel lateral deslizante para crear/editar, y un banner contextual explicando la política de eliminación.
- **Documentado en TODO del controlador** `CategoriaMedicamentoController`. La implementación actual es funcional; alinear el HTML al Stitch (columns: "Medicamentos Activos" cuenta medicamentos por categoría, estado **Activa/En uso**).

#### 2. Gestión de Pacientes (`/pacientes`, Pendiente)
- **Stitch:** `docs/stitch/pacientes.html`. Vista "Directorio de Pacientes".
- **Campos del formulario (panel lateral 480px, multi-paso):** Nombres, Apellidos, Documento de Identidad (con **validación de duplicado en vivo**), Fecha de Nacimiento, Género (select), Teléfono. El usuario requiere campo `usuarioGestor`/expediente.
- **Tabla:** Documento de Identidad, Nombre (con avatar iniciales), Fecha de Nacimiento, Género (badge), Teléfono, columna acciones (menú). Con paginación "Mostrando X de Y".
- **Roles:** Médico y Recepcionista (solo datos de contacto/citas), Admin (auditoría).
- **Pendiente de crear:** `PacienteController` (+ template `pacientes.html`) con `@PreAuthorize`.
- **Entidad:** `Paciente` (`codigoExpediente`, `nombres`, `apellidos`, `documentoIdentidad`, `fechaNacimiento`, `telefono`, `genero`) + `IPacienteService`.

#### 3. Gestión de Médicos (`/medicos`, Pendiente)
- **Stitch:** `docs/stitch/medicos.html`. Vista "Directorio de Médicos".
- **Layout bento 12-col:** formulario de registro (col-span-4) + tabla del directorio activo (col-span-8).
- **Formulario:** Nombre Completo, Especialidad (select), Número de Licencia (con check de unicidad), Teléfono, Correo. Botones Limpiar/Registrar.
- **Tabla:** Médico (avatar + nombre + ID), Especialidad, Licencia/Contacto, Estado (**toggle de disponibilidad**), acciones. Paginación.
- **Roles:** Admin y Recepcionista. (Los médicos no ven este módulo.)
- **Pendiente de crear:** `MedicoController` (+ `medicos.html`) — al registrar un Médico se crea su `Usuario` (rol MEDICO) y su `Medico`.
- **Entidad:** `Medico` (`usuario` 1:1, `especialidad`, `numeroLicencia`, `disponible`) + `IMedicoService`.

#### 4. Consulta Médica (`/consultas`, Pendiente)
- **Stitch:** `docs/stitch/consulta.html`. Formulario "Nota de Consulta" con **progresor SOAP** (Subjetivo → Objetivo → Plan).
- **Panel paciente (izq):** foto, nombre, Fecha nacimiento, N° historia, **Signos Vitales** (PA, FC), **Alergias** (chips rojo), **Medicamentos Activos** (chips azul).
- **Formulario (der):** Motivo de Consulta *, Síntomas e Historia *, Evaluación/Diagnóstico (con link "Añadir Código CIE-10"), Plan de Tratamiento. Botones "Guardar Borrador" y "Guardar y Emitir Receta".
- **Roles:** Exclusivo Médico; Admin solo lectura histórica.
- **Pendiente de crear:** `ConsultaMedicaController` (+ `consultas.html`). Al guardar, la cita pasa a `ATENDIDA` (ver flujo en `docs/explicacion-sistema.md`).
- **Entidad:** `ConsultaMedica` (`cita` 1:1, `motivoConsulta`, `sintomatologia`, `diagnostico`, `fechaConsulta`) + `IConsultaMedicaService`.

#### 5. Receta Médica (`/recetas`, Pendiente)
- **Stitch:** `docs/stitch/recetas.html`. Vista imprimible tipo "prescription pad".
- **Interacción:** barra de acciones con "Enviar por Correo" e "Imprimir Receta" (`@media print` oculta nav y no imprime elementos de UI).
- **Contenido:** encabezado de clínica, datos del médico (nombre, especialidad, licencia), tarjeta de paciente (nombre, fecha nacimiento, género, MRN), listado de medicamentos (Medicamento, Dosis, Frecuencia, Duración, Notas), notas/instrucciones y firma del médico. ID de receta electrónica.
- **Roles:** Exclusivo Médico (generar); Recepcionista solo lectura/impresión; Admin histórico.
- **Pendiente de crear:** `RecetaController` (+ `recetas.html` + vista imprimible). La generación usa `RecetaDetalle` (`cantidad`, `indicaciones`, `estado` PRESCRITA/DISPENSADA) — al dispensar descuenta stock vía `Medicamento.actualizarStock()`.
- **Entidad:** `RecetaDetalle` (`consulta` N:1, `medicamento` N:1, `cantidad`, `indicaciones`, `estado`) + `IRecetaDetalleService`.

#### 6. Inventario de Medicamentos (`/medicamentos`, Ya implementado)
- **Stitch:** `docs/stitch/inventario.html`. Vista "Inventario de Medicamentos".
- **Layout:** tarjetas de métricas (Artículos Totales, Stock Bajo, Próximo a Vencer) + tabla principal (Nombre Comercial, Nombre Genérico, Categoría, Stock, Vencimiento, Estado) + panel lateral para registrar medicamento y gestionar categorías.
- **Estado por fila:** vigente (check verde), próximo a vencer (badge ámbar), vencido/bloqueado (rojo, `line-through`), stock bajo (fondo resaltado).
- **Roles:** Admin y Recepcionista gestionan; Médicos solo lectura.
- **Documentado en TODO del controlador** `MedicamentoController`. Alinear el HTML al Stitch.

### Referencia de negocio
Detalle de los flujos y procesos de cada vista: **`docs/explicacion-sistema.md`**.
