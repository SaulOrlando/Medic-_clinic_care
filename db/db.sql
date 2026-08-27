CREATE DATABASE SistemaClinico;
GO

USE SistemaClinico;
GO

-- =============================================
-- TABLA: USUARIOS
-- =============================================
CREATE TABLE usuarios (
    id_usuario INT IDENTITY(1,1) PRIMARY KEY,
    correo VARCHAR(150) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    nombre_completo VARCHAR(150) NOT NULL,
    telefono VARCHAR(20)
);
GO

-- =============================================
-- TABLA: MEDICOS
-- =============================================
CREATE TABLE medicos (
    id_medico INT IDENTITY(1,1) PRIMARY KEY,
    id_usuario INT NOT NULL UNIQUE,
    especialidad VARCHAR(100) NOT NULL,
    numero_licencia VARCHAR(50) NOT NULL UNIQUE,
    disponible BIT NOT NULL DEFAULT 1,

    CONSTRAINT FK_medicos_usuarios
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
);
GO

-- =============================================
-- TABLA: PACIENTES
-- =============================================
CREATE TABLE pacientes (
    id_paciente INT IDENTITY(1,1) PRIMARY KEY,
    codigo_expediente VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    documento_identidad VARCHAR(30) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    telefono VARCHAR(20),
    genero VARCHAR(20)
);
GO

-- =============================================
-- TABLA: CITAS
-- =============================================
CREATE TABLE citas (
    id_cita INT IDENTITY(1,1) PRIMARY KEY,
    id_paciente INT NOT NULL,
    id_medico INT NOT NULL,
    id_usuario_gestor INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    duracion_minutos INT NOT NULL,
    estado VARCHAR(30) NOT NULL,

    CONSTRAINT FK_citas_pacientes
        FOREIGN KEY (id_paciente)
        REFERENCES pacientes(id_paciente),

    CONSTRAINT FK_citas_medicos
        FOREIGN KEY (id_medico)
        REFERENCES medicos(id_medico),

    CONSTRAINT FK_citas_usuario_gestor
        FOREIGN KEY (id_usuario_gestor)
        REFERENCES usuarios(id_usuario)
);
GO

-- =============================================
-- TABLA: HISTORIAL DE CITAS
-- =============================================
CREATE TABLE historial_citas (
    id_historial INT IDENTITY(1,1) PRIMARY KEY,
    id_cita INT NOT NULL,
    id_usuario INT NOT NULL,
    estado_anterior VARCHAR(30) NOT NULL,
    estado_nuevo VARCHAR(30) NOT NULL,
    motivo NVARCHAR(MAX),
    fecha_cambio DATETIME NOT NULL DEFAULT GETDATE(),

    CONSTRAINT FK_historial_cita
        FOREIGN KEY (id_cita)
        REFERENCES citas(id_cita),

    CONSTRAINT FK_historial_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
);
GO

-- =============================================
-- TABLA: CONSULTAS MEDICAS
-- =============================================
CREATE TABLE consultas_medicas (
    id_consulta INT IDENTITY(1,1) PRIMARY KEY,
    id_cita INT NOT NULL UNIQUE,
    fecha_consulta DATETIME NOT NULL,
    motivo_consulta NVARCHAR(MAX),
    sintomatologia NVARCHAR(MAX),
    diagnostico NVARCHAR(MAX),

    CONSTRAINT FK_consultas_cita
        FOREIGN KEY (id_cita)
        REFERENCES citas(id_cita)
);
GO

-- =============================================
-- TABLA: CATEGORIAS DE MEDICAMENTOS
-- =============================================
CREATE TABLE categorias_medicamentos (
    id_categoria INT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion NVARCHAR(MAX)
);
GO

-- =============================================
-- TABLA: MEDICAMENTOS
-- =============================================
CREATE TABLE medicamentos (
    id_medicamento INT IDENTITY(1,1) PRIMARY KEY,
    id_categoria INT NOT NULL,
    nombre_comercial VARCHAR(150) NOT NULL,
    nombre_generico VARCHAR(150) NOT NULL,
    presentacion VARCHAR(50),
    unidad_medida VARCHAR(30),
    concentracion VARCHAR(50),
    stock_inicial INT NOT NULL DEFAULT 0,
    stock_disponible INT NOT NULL DEFAULT 0,
    fecha_vencimiento DATE,

    CONSTRAINT FK_medicamentos_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categorias_medicamentos(id_categoria),

    CONSTRAINT CK_medicamentos_stock
        CHECK (stock_inicial >= 0 AND stock_disponible >= 0)
);
GO

-- =============================================
-- TABLA: MOVIMIENTOS DE INVENTARIO
-- =============================================
CREATE TABLE movimientos_inventario (
    id_movimiento INT IDENTITY(1,1) PRIMARY KEY,
    id_medicamento INT NOT NULL,
    id_usuario INT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    cantidad INT NOT NULL,
    fecha_movimiento DATETIME NOT NULL DEFAULT GETDATE(),
    motivo NVARCHAR(MAX),

    CONSTRAINT FK_movimientos_medicamento
        FOREIGN KEY (id_medicamento)
        REFERENCES medicamentos(id_medicamento),

    CONSTRAINT FK_movimientos_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario),

    CONSTRAINT CK_movimientos_cantidad
        CHECK (cantidad > 0)
);
GO

-- =============================================
-- TABLA: RECETAS DETALLES
-- =============================================
CREATE TABLE recetas_detalles (
    id_receta_detalle INT IDENTITY(1,1) PRIMARY KEY,
    id_consulta INT NOT NULL,
    id_medicamento INT NOT NULL,
    cantidad INT NOT NULL,
    indicaciones NVARCHAR(MAX),
    estado VARCHAR(20) NOT NULL,

    CONSTRAINT FK_recetas_consulta
        FOREIGN KEY (id_consulta)
        REFERENCES consultas_medicas(id_consulta),

    CONSTRAINT FK_recetas_medicamento
        FOREIGN KEY (id_medicamento)
        REFERENCES medicamentos(id_medicamento),

    CONSTRAINT CK_recetas_cantidad
        CHECK (cantidad > 0)
);
GO
