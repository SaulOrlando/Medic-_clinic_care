# AGENTS.md — MediClinic Care

## Project Overview

Spring Boot medical clinic management system for scheduling appointments, managing patient records, and tracking consultations. Uses SQL Server, Thymeleaf, and Spring Data JPA.

- **Stack:** Java 25, Spring Boot 4.1.0, Maven, SQL Server (mssql-jdbc), Thymeleaf, Jakarta Persistence (Hibernate)
- **Entry point:** `src/main/java/org/esfe/MediclinicAppApplication.java`

## Setup Commands

```bash
# Compile
./mvnw compile

# Run the application
./mvnw spring-boot:run

# Run tests
./mvnw test
```

> **Note:** `application.properties` currently has no database configuration. A SQL Server connection must be configured before the app can start. Use `db.sql` to create the schema.

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
