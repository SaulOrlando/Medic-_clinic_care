USE SistemaClinico;
GO

SET NOCOUNT ON;
SET XACT_ABORT ON;

-- =====================================================
-- SEED - Datos falsos para desarrollo / pruebas
-- Cuentas creadas (contrasena: 12345678 para todas):
--   saul@mediclinic.com      -> MEDICO
--   fabiola@mediclinic.com  -> RECEPCIONISTA
--   iliana@mediclinic.com   -> ADMINISTRADOR
--   guadalupe@mediclinic.com  -> ENCARGADO_INVENTARIO
--   camila@mediclinic.com  -> MEDICO (extra)
--   roberto@mediclinic.com  -> MEDICO (extra)
-- =====================================================

DECLARE @hoy         AS DATE     = CAST(GETDATE() AS DATE);
DECLARE @hoyInicio   AS DATETIME = CAST(@hoy AS DATETIME);
DECLARE @lunes       AS DATE     = DATEADD(day, - (DATEPART(weekday, @hoy) - 2), @hoy);
DECLARE @lunesIni    AS DATETIME = CAST(@lunes AS DATETIME);
DECLARE @martes      AS DATETIME = DATEADD(day, 1, @lunesIni);
DECLARE @miercoles   AS DATETIME = DATEADD(day, 2, @lunesIni);
DECLARE @jueves      AS DATETIME = DATEADD(day, 3, @lunesIni);
DECLARE @viernes     AS DATETIME = DATEADD(day, 4, @lunesIni);
DECLARE @lunesProx   AS DATETIME = DATEADD(day, 7, @lunesIni);
DECLARE @martesProx  AS DATETIME = DATEADD(day, 8, @lunesIni);

BEGIN TRANSACTION;

-- ============ USUARIOS ============
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'saul@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('saul@mediclinic.com', '12345678', 'MEDICO', 'Saul Tobar', '7711-1001');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'fabiola@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('fabiola@mediclinic.com', '12345678', 'RECEPCIONISTA', 'Fabiola Cortez', '7711-1002');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'iliana@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('iliana@mediclinic.com', '12345678', 'ADMINISTRADOR', 'Iliana Melgar', '7711-1003');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'guadalupe@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('guadalupe@mediclinic.com', '12345678', 'ENCARGADO_INVENTARIO', 'Guadalupe Sion', '7711-1004');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'camila@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('camila@mediclinic.com', '12345678', 'MEDICO', 'Camila Fuentes', '7711-1005');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'roberto@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('roberto@mediclinic.com', '12345678', 'MEDICO', 'Roberto Campos', '7711-1006');

DECLARE @idSaul      INT = (SELECT id_usuario FROM usuarios WHERE correo = 'saul@mediclinic.com');
DECLARE @idFabiola   INT = (SELECT id_usuario FROM usuarios WHERE correo = 'fabiola@mediclinic.com');
DECLARE @idIliana    INT = (SELECT id_usuario FROM usuarios WHERE correo = 'iliana@mediclinic.com');
DECLARE @idGuadalupe INT = (SELECT id_usuario FROM usuarios WHERE correo = 'guadalupe@mediclinic.com');
DECLARE @idCamila    INT = (SELECT id_usuario FROM usuarios WHERE correo = 'camila@mediclinic.com');
DECLARE @idRoberto   INT = (SELECT id_usuario FROM usuarios WHERE correo = 'roberto@mediclinic.com');

-- ============ MEDICOS ============
IF NOT EXISTS (SELECT 1 FROM medicos WHERE numero_licencia = 'LIC-MED-0001')
    INSERT INTO medicos (id_usuario, especialidad, numero_licencia, disponible) VALUES
    (@idSaul, 'Medicina General', 'LIC-MED-0001', 1);

IF NOT EXISTS (SELECT 1 FROM medicos WHERE numero_licencia = 'LIC-MED-0002')
    INSERT INTO medicos (id_usuario, especialidad, numero_licencia, disponible) VALUES
    (@idCamila, 'Pediatria', 'LIC-MED-0002', 1);

IF NOT EXISTS (SELECT 1 FROM medicos WHERE numero_licencia = 'LIC-MED-0003')
    INSERT INTO medicos (id_usuario, especialidad, numero_licencia, disponible) VALUES
    (@idRoberto, 'Cardiologia', 'LIC-MED-0003', 1);

DECLARE @medSaul   INT = (SELECT id_medico FROM medicos WHERE numero_licencia = 'LIC-MED-0001');
DECLARE @medCamila INT = (SELECT id_medico FROM medicos WHERE numero_licencia = 'LIC-MED-0002');
DECLARE @medRoberto INT = (SELECT id_medico FROM medicos WHERE numero_licencia = 'LIC-MED-0003');

-- ============ PACIENTES ============
IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '04412345-1')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0001', 'Maria', 'Garcia', '04412345-1', '1991-05-14', '7611-1101', 'Femenino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '02567890-4')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0002', 'Carlos', 'Hernandez', '02567890-4', '1985-11-02', '7611-1102', 'Masculino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '03345678-1')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0003', 'Ana', 'Martinez', '03345678-1', '1998-02-23', '7611-1103', 'Femenino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '01234567-2')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0004', 'Jorge', 'Perez', '01234567-2', '1972-06-30', '7611-1104', 'Masculino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '05678912-3')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0005', 'Lucia', 'Ramirez', '05678912-3', '2005-09-10', '7611-1105', 'Femenino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '06789012-5')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0006', 'Pedro', 'Lopez', '06789012-5', '1960-12-25', '7611-1106', 'Masculino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '08901234-6')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0007', 'Rosa', 'Sanchez', '08901234-6', '1988-04-17', '7611-1107', 'Femenino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '09876543-7')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0008', 'Miguel', 'Torres', '09876543-7', '1995-01-08', '7611-1108', 'Masculino');

DECLARE @pac1 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '04412345-1');
DECLARE @pac2 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '02567890-4');
DECLARE @pac3 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '03345678-1');
DECLARE @pac4 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '01234567-2');
DECLARE @pac5 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '05678912-3');
DECLARE @pac6 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '06789012-5');
DECLARE @pac7 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '08901234-6');
DECLARE @pac8 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '09876543-7');

-- ============ CITAS (semana en curso, hoy y proximas) ============
DECLARE @citaMon1 INT, @citaMon2 INT, @citaTue1 INT, @citaTue2 INT;
DECLARE @citaWed1 INT, @citaWed2 INT, @citaThu1 INT, @citaThu2 INT;
DECLARE @citaFri1 INT, @citaFri2 INT, @citaFri3 INT, @citaFri4 INT, @citaFri5 INT, @citaFri6 INT, @citaFri7 INT;
DECLARE @citaNext1 INT, @citaNext2 INT;

-- Lunes
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac1, @medSaul, @idFabiola, DATEADD(minute, 540, @lunesIni), 30, 'ATENDIDA');
SET @citaMon1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac3, @medCamila, @idFabiola, DATEADD(minute, 660, @lunesIni), 30, 'ATENDIDA');
SET @citaMon2 = SCOPE_IDENTITY();

-- Martes
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac2, @medSaul, @idFabiola, DATEADD(minute, 600, @martes), 30, 'ATENDIDA');
SET @citaTue1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac4, @medRoberto, @idFabiola, DATEADD(minute, 840, @martes), 45, 'ATENDIDA');
SET @citaTue2 = SCOPE_IDENTITY();

-- Miercoles
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac5, @medSaul, @idFabiola, DATEADD(minute, 540, @miercoles), 30, 'ATENDIDA');
SET @citaWed1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac6, @medCamila, @idFabiola, DATEADD(minute, 600, @miercoles), 30, 'ATENDIDA');
SET @citaWed2 = SCOPE_IDENTITY();

-- Jueves
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac7, @medSaul, @idFabiola, DATEADD(minute, 570, @jueves), 30, 'ATENDIDA');
SET @citaThu1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac8, @medRoberto, @idFabiola, DATEADD(minute, 690, @jueves), 45, 'ATENDIDA');
SET @citaThu2 = SCOPE_IDENTITY();

-- Hoy (viernes)
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac5, @medSaul, @idFabiola, DATEADD(minute, 510, @viernes), 30, 'ATENDIDA');
SET @citaFri1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac1, @medSaul, @idFabiola, DATEADD(minute, 540, @viernes), 30, 'PROGRAMADA');
SET @citaFri2 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac2, @medSaul, @idFabiola, DATEADD(minute, 585, @viernes), 30, 'PROGRAMADA');
SET @citaFri3 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac3, @medCamila, @idFabiola, DATEADD(minute, 630, @viernes), 30, 'PROGRAMADA');
SET @citaFri4 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac4, @medRoberto, @idFabiola, DATEADD(minute, 780, @viernes), 45, 'PROGRAMADA');
SET @citaFri5 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac7, @medSaul, @idFabiola, DATEADD(minute, 900, @viernes), 30, 'CANCELADA');
SET @citaFri6 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac6, @medSaul, @idFabiola, DATEADD(minute, 990, @viernes), 30, 'REAGENDADA');
SET @citaFri7 = SCOPE_IDENTITY();

-- Proxima semana
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac1, @medSaul, @idFabiola, DATEADD(minute, 540, @lunesProx), 30, 'PROGRAMADA');
SET @citaNext1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac4, @medCamila, @idFabiola, DATEADD(minute, 600, @martesProx), 30, 'PROGRAMADA');
SET @citaNext2 = SCOPE_IDENTITY();

-- ============ HISTORIAL DE CITAS ============
INSERT INTO historial_citas (id_cita, id_usuario, estado_anterior, estado_nuevo, motivo, fecha_cambio)
    VALUES (@citaFri6, @idFabiola, 'PROGRAMADA', 'CANCELADA', 'El paciente cancelo por motivos personales', GETDATE());

INSERT INTO historial_citas (id_cita, id_usuario, estado_anterior, estado_nuevo, motivo, fecha_cambio)
    VALUES (@citaFri7, @idFabiola, 'PROGRAMADA', 'REAGENDADA', 'Se reagendo la cita por solicitud del paciente', GETDATE());

-- ============ CONSULTAS MEDICAS (citas atendidas) ============
DECLARE @consMon1 INT, @consMon2 INT, @consTue1 INT, @consTue2 INT;
DECLARE @consWed1 INT, @consWed2 INT, @consThu1 INT, @consThu2 INT, @consFri1 INT;

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaMon1, DATEADD(minute, 545, @lunesIni), 'Fiebre y dolor de cabeza',
            'Temperatura de 38.5 grados, cefalea frontal, malestar general',
            'Infeccion respiratoria leve');
SET @consMon1 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaMon2, DATEADD(minute, 665, @lunesIni), 'Control de odontologia infantil',
            'Sin sintomatologia aguda, paciente en revision',
            'Paciente sano, control de rutina');
SET @consMon2 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaTue1, DATEADD(minute, 605, @martes), 'Dolor abdominal',
            'Dolor tipo colico en epigastrio, nauseas ocasionales',
            'Gastritis aguda');
SET @consTue1 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaTue2, DATEADD(minute, 845, @martes), 'Palpitaciones',
            'Palpitaciones intermitentes, fatiga al esfuerzo',
            'Arritmia benigna en seguimiento');
SET @consTue2 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaWed1, DATEADD(minute, 545, @miercoles), 'Consulta por rinitis alergica',
            'Estornudos frecuentes, prurito nasal, lagrimeo',
            'Rinitis alergica estacional');
SET @consWed1 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaWed2, DATEADD(minute, 605, @miercoles), 'Dolor de oido en nino',
            'Otalgia derecha, fiebre moderada, irritabilidad',
            'Otitis media aguda');
SET @consWed2 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaThu1, DATEADD(minute, 575, @jueves), 'Control de diabetes',
            'Control mensual, glucemia en ayunas de 145',
            'Diabetes tipo 2 controlada');
SET @consThu1 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaThu2, DATEADD(minute, 695, @jueves), 'Dolor toracico',
            'Dolor opresivo leve, antecedente de hipertension',
            'Angina estable, seguimiento');
SET @consThu2 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaFri1, DATEADD(minute, 515, @viernes), 'Dolor de garganta',
            'Odindinamia, fiebre de 38 grados, faringe eritematosa',
            'Faringitis aguda');
SET @consFri1 = SCOPE_IDENTITY();

-- ============ CATEGORIAS ============
IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Analgesicos')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Analgesicos', 'Medicamentos para el alivio del dolor');

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Antibioticos')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Antibioticos', 'Medicamentos para infecciones bacterianas');

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Antihistaminicos')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Antihistaminicos', 'Medicamentos para alergias');

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Vitaminas y Suplementos')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Vitaminas y Suplementos', 'Complementos nutricionales');

-- ============ MEDICAMENTOS ============
DECLARE @catAnalg   INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Analgesicos');
DECLARE @catAntib   INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Antibioticos');
DECLARE @catAntihis INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Antihistaminicos');
DECLARE @catVit     INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Vitaminas y Suplementos');

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Paracetamol')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catAnalg, 'Paracetamol', 'Paracetamol', 'Tabletas', 'mg', '500', 120, 96, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Ibuprofeno')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catAnalg, 'Ibuprofeno', 'Ibuprofeno', 'Tabletas', 'mg', '400', 80, 74, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Amoxicilina')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catAntib, 'Amoxicilina', 'Amoxicilina', 'Capsulas', 'mg', '500', 60, 42, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Azitromicina')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catAntib, 'Azitromicina', 'Azitromicina', 'Tabletas', 'mg', '250', 40, 31, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Loratadina')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catAntihis, 'Loratadina', 'Loratadina', 'Tabletas', 'mg', '10', 50, 45, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Vitamina C')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catVit, 'Vitamina C', 'Acido ascorbico', 'Tabletas', 'mg', '1000', 100, 88, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Vitamina D3')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catVit, 'Vitamina D3', 'Colecalciferol', 'Capsulas', 'UI', '2000', 70, 62, DATEADD(year, 1, @hoy));

-- ============ MOVIMIENTOS DE INVENTARIO (compras iniciales) ============
DECLARE @medPara INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Paracetamol');
DECLARE @medIbu  INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Ibuprofeno');
DECLARE @medAmo  INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Amoxicilina');
DECLARE @medLor  INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Loratadina');
DECLARE @medVitC INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Vitamina C');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medPara AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medPara, @idGuadalupe, 'ENTRADA', 120, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medIbu AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medIbu, @idGuadalupe, 'ENTRADA', 80, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medAmo AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medAmo, @idGuadalupe, 'ENTRADA', 60, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medLor AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medLor, @idGuadalupe, 'ENTRADA', 50, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medVitC AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medVitC, @idGuadalupe, 'ENTRADA', 100, @hoyInicio, 'Compra inicial');

-- ============ RECETAS DETALLES ============
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, estado)
    VALUES (@consMon1, @medPara, 10, 'Tomar 1 tableta cada 8 horas por 3 dias', 'DISPENSADA');

INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, estado)
    VALUES (@consTue1, @medPara, 15, 'Tomar 1 tableta cada 8 horas si hay dolor', 'DISPENSADA');

INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, estado)
    VALUES (@consWed1, @medLor, 10, 'Tomar 1 tableta cada 24 horas por 5 dias', 'PRESCRITA');

COMMIT TRANSACTION;

PRINT 'SEED COMPLETADO CORRECTAMENTE';