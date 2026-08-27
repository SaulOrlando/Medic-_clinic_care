-- =============================================
-- DATOS INICIALES — MediClinic Care
-- Usuarios de prueba
-- =============================================

USE SistemaClinico;
GO

-- =============================================
-- USUARIOS
-- Contraseña para todos: 12345678
-- =============================================
INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono)
VALUES
    ('saul@mediclinic.com',      '12345678', 'MEDICO',         'Saul Ramirez',       '555-102-3045'),
    ('iliana@mediclinic.com',    '12345678', 'RECEPCIONISTA',  'Iliana Melgar',      '555-203-4156'),
    ('guadalupe@mediclinic.com', '12345678', 'ADMINISTRADOR',  'Guadalupe Sion',     '555-304-5267'),
    ('fabiola@mediclinic.com',   '12345678', 'MEDICO',         'Fabiola Cortez',     '555-405-6378');
GO

-- =============================================
-- MEDICOS (Saul y Fabiola son medicos)
-- id_medico 1 = Saul, id_medico 2 = Fabiola
-- =============================================
INSERT INTO medicos (id_usuario, especialidad, numero_licencia, disponible)
VALUES
    (1, 'Medicina General',    'LIC-2024-001', 1),
    (4, 'Pediatria',           'LIC-2024-002', 1);
GO

-- =============================================
-- CATEGORIAS DE MEDICAMENTOS
-- =============================================
INSERT INTO categorias_medicamentos (nombre, descripcion)
VALUES
    ('Analgesicos',      'Medicamentos para el alivio del dolor'),
    ('Antibioticos',     'Medicamentos para infecciones bacterianas'),
    ('Antiinflamatorios','Medicamentos para reducir la inflamacion'),
    ('Vitaminas',        'Suplementos vitaminyicos'),
    ('Cardiovasculares', 'Medicamentos para el sistema cardiovascular');
GO

-- =============================================
-- PACIENTES DE PRUEBA
-- =============================================
INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero)
VALUES
    ('EXP-001', 'Carlos',   'Mendoza Lopez',   '0801-1990-12345', '1990-05-15', '555-506-7489', 'Masculino'),
    ('EXP-002', 'Maria',    'Garcia Ruiz',      '0801-1985-67890', '1985-11-22', '555-607-8590', 'Femenino'),
    ('EXP-003', 'Pedro',    'Sanchez Morales',  '0801-2000-11111', '2000-03-10', '555-708-9601', 'Masculino'),
    ('EXP-004', 'Ana',      'Rivera Cruz',      '0801-1978-22222', '1978-07-30', '555-809-0712', 'Femenino'),
    ('EXP-005', 'Jorge',    'Lopez Martinez',   '0801-1995-33333', '1995-01-18', '555-910-1823', 'Masculino');
GO

-- =============================================
-- CITAS DE PRUEBA
-- id_medico 1 = Saul, id_medico 2 = Fabiola
-- =============================================
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
VALUES
    (1, 1, 2, '2026-08-27 09:00:00', 30, 'PROGRAMADA'),
    (2, 1, 2, '2026-08-27 09:45:00', 30, 'ATENDIDA'),
    (3, 2, 2, '2026-08-27 10:30:00', 45, 'REPROGRAMADA'),
    (4, 1, 2, '2026-08-28 08:00:00', 30, 'PROGRAMADA'),
    (5, 2, 2, '2026-08-28 11:00:00', 45, 'PROGRAMADA');
GO

PRINT 'Datos iniciales insertados correctamente.';
GO
