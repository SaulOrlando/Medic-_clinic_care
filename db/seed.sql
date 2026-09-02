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
--   camila@mediclinic.com  -> MEDICO (extra)
--   roberto@mediclinic.com  -> MEDICO (extra)
--   guadalupe@mediclinic.com -> MEDICO (extra)
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

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'camila@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('camila@mediclinic.com', '12345678', 'MEDICO', 'Camila Fuentes', '7711-1005');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'roberto@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('roberto@mediclinic.com', '12345678', 'MEDICO', 'Roberto Campos', '7711-1006');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE correo = 'guadalupe@mediclinic.com')
    INSERT INTO usuarios (correo, contrasena, rol, nombre_completo, telefono) VALUES
    ('guadalupe@mediclinic.com', '12345678', 'MEDICO', 'Guadalupe Sion', '7711-1007');

DECLARE @idSaul      INT = (SELECT id_usuario FROM usuarios WHERE correo = 'saul@mediclinic.com');
DECLARE @idFabiola   INT = (SELECT id_usuario FROM usuarios WHERE correo = 'fabiola@mediclinic.com');
DECLARE @idIliana    INT = (SELECT id_usuario FROM usuarios WHERE correo = 'iliana@mediclinic.com');
DECLARE @idCamila    INT = (SELECT id_usuario FROM usuarios WHERE correo = 'camila@mediclinic.com');
DECLARE @idRoberto   INT = (SELECT id_usuario FROM usuarios WHERE correo = 'roberto@mediclinic.com');
DECLARE @idGuadalupe INT = (SELECT id_usuario FROM usuarios WHERE correo = 'guadalupe@mediclinic.com');

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

IF NOT EXISTS (SELECT 1 FROM medicos WHERE numero_licencia = 'LIC-MED-0004')
    INSERT INTO medicos (id_usuario, especialidad, numero_licencia, disponible) VALUES
    (@idGuadalupe, 'Dermatologia', 'LIC-MED-0004', 1);

DECLARE @medSaul   INT = (SELECT id_medico FROM medicos WHERE numero_licencia = 'LIC-MED-0001');
DECLARE @medCamila INT = (SELECT id_medico FROM medicos WHERE numero_licencia = 'LIC-MED-0002');
DECLARE @medRoberto INT = (SELECT id_medico FROM medicos WHERE numero_licencia = 'LIC-MED-0003');
DECLARE @medGuadalupe INT = (SELECT id_medico FROM medicos WHERE numero_licencia = 'LIC-MED-0004');

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

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '07451238-9')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0009', 'Carmen', 'Vargas', '07451238-9', '1982-03-15', '7611-1109', 'Femenino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '08123456-0')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0010', 'Fernando', 'Morales', '08123456-0', '1976-07-22', '7611-1110', 'Masculino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '03987654-3')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0011', 'Patricia', 'Reyes', '03987654-3', '1990-11-05', '7611-1111', 'Femenino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '05234567-8')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0012', 'Andres', 'Castillo', '05234567-8', '1999-01-30', '7611-1112', 'Masculino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '06345678-2')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0013', 'Sofia', 'Ortiz', '06345678-2', '2001-06-18', '7611-1113', 'Femenino');

IF NOT EXISTS (SELECT 1 FROM pacientes WHERE documento_identidad = '09567890-5')
    INSERT INTO pacientes (codigo_expediente, nombres, apellidos, documento_identidad, fecha_nacimiento, telefono, genero) VALUES
    ('EXP-0014', 'Roberto', 'Jimenez', '09567890-5', '1968-09-12', '7611-1114', 'Masculino');

DECLARE @pac1 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '04412345-1');
DECLARE @pac2 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '02567890-4');
DECLARE @pac3 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '03345678-1');
DECLARE @pac4 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '01234567-2');
DECLARE @pac5 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '05678912-3');
DECLARE @pac6 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '06789012-5');
DECLARE @pac7 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '08901234-6');
DECLARE @pac8 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '09876543-7');
DECLARE @pac9 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '07451238-9');
DECLARE @pac10 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '08123456-0');
DECLARE @pac11 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '03987654-3');
DECLARE @pac12 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '05234567-8');
DECLARE @pac13 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '06345678-2');
DECLARE @pac14 INT = (SELECT id_paciente FROM pacientes WHERE documento_identidad = '09567890-5');

-- ============ CITAS (semana en curso, hoy y proximas) ============
DECLARE @citaMon1 INT, @citaMon2 INT, @citaMon3 INT, @citaMon4 INT;
DECLARE @citaTue1 INT, @citaTue2 INT, @citaTue3 INT, @citaTue4 INT;
DECLARE @citaWed1 INT, @citaWed2 INT, @citaWed3 INT, @citaWed4 INT;
DECLARE @citaThu1 INT, @citaThu2 INT, @citaThu3 INT, @citaThu4 INT;
DECLARE @citaFri1 INT, @citaFri2 INT, @citaFri3 INT, @citaFri4 INT, @citaFri5 INT, @citaFri6 INT, @citaFri7 INT, @citaFri8 INT, @citaFri9 INT, @citaFri10 INT;
DECLARE @citaNext1 INT, @citaNext2 INT, @citaNext3 INT;

-- Lunes
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac1, @medSaul, @idFabiola, DATEADD(minute, 540, @lunesIni), 30, 'ATENDIDA');
SET @citaMon1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac3, @medCamila, @idFabiola, DATEADD(minute, 660, @lunesIni), 30, 'ATENDIDA');
SET @citaMon2 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac9, @medGuadalupe, @idFabiola, DATEADD(minute, 510, @lunesIni), 30, 'ATENDIDA');
SET @citaMon3 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac10, @medGuadalupe, @idFabiola, DATEADD(minute, 600, @lunesIni), 30, 'ATENDIDA');
SET @citaMon4 = SCOPE_IDENTITY();

-- Martes
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac2, @medSaul, @idFabiola, DATEADD(minute, 600, @martes), 30, 'ATENDIDA');
SET @citaTue1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac4, @medRoberto, @idFabiola, DATEADD(minute, 840, @martes), 45, 'ATENDIDA');
SET @citaTue2 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac11, @medGuadalupe, @idFabiola, DATEADD(minute, 540, @martes), 30, 'ATENDIDA');
SET @citaTue3 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac12, @medGuadalupe, @idFabiola, DATEADD(minute, 630, @martes), 30, 'ATENDIDA');
SET @citaTue4 = SCOPE_IDENTITY();

-- Miercoles
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac5, @medSaul, @idFabiola, DATEADD(minute, 540, @miercoles), 30, 'ATENDIDA');
SET @citaWed1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac6, @medCamila, @idFabiola, DATEADD(minute, 600, @miercoles), 30, 'ATENDIDA');
SET @citaWed2 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac13, @medGuadalupe, @idFabiola, DATEADD(minute, 480, @miercoles), 30, 'ATENDIDA');
SET @citaWed3 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac14, @medGuadalupe, @idFabiola, DATEADD(minute, 690, @miercoles), 30, 'ATENDIDA');
SET @citaWed4 = SCOPE_IDENTITY();

-- Jueves
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac7, @medSaul, @idFabiola, DATEADD(minute, 570, @jueves), 30, 'ATENDIDA');
SET @citaThu1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac8, @medRoberto, @idFabiola, DATEADD(minute, 690, @jueves), 45, 'ATENDIDA');
SET @citaThu2 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac9, @medGuadalupe, @idFabiola, DATEADD(minute, 510, @jueves), 30, 'ATENDIDA');
SET @citaThu3 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac11, @medGuadalupe, @idFabiola, DATEADD(minute, 600, @jueves), 30, 'PROGRAMADA');
SET @citaThu4 = SCOPE_IDENTITY();

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
    VALUES (@pac12, @medGuadalupe, @idFabiola, DATEADD(minute, 480, @viernes), 30, 'PROGRAMADA');
SET @citaFri6 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac13, @medGuadalupe, @idFabiola, DATEADD(minute, 720, @viernes), 30, 'PROGRAMADA');
SET @citaFri7 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac7, @medSaul, @idFabiola, DATEADD(minute, 900, @viernes), 30, 'CANCELADA');
SET @citaFri8 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac6, @medSaul, @idFabiola, DATEADD(minute, 990, @viernes), 30, 'REAGENDADA');
SET @citaFri9 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac10, @medGuadalupe, @idFabiola, DATEADD(minute, 1050, @viernes), 30, 'PROGRAMADA');
SET @citaFri10 = SCOPE_IDENTITY();

-- Proxima semana
INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac1, @medSaul, @idFabiola, DATEADD(minute, 540, @lunesProx), 30, 'PROGRAMADA');
SET @citaNext1 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac4, @medCamila, @idFabiola, DATEADD(minute, 600, @martesProx), 30, 'PROGRAMADA');
SET @citaNext2 = SCOPE_IDENTITY();

INSERT INTO citas (id_paciente, id_medico, id_usuario_gestor, fecha_hora, duracion_minutos, estado)
    VALUES (@pac9, @medGuadalupe, @idFabiola, DATEADD(minute, 540, @lunesProx), 30, 'PROGRAMADA');
SET @citaNext3 = SCOPE_IDENTITY();

-- ============ HISTORIAL DE CITAS ============
INSERT INTO historial_citas (id_cita, id_usuario, estado_anterior, estado_nuevo, motivo, fecha_cambio)
    VALUES (@citaFri8, @idFabiola, 'PROGRAMADA', 'CANCELADA', 'El paciente cancelo por motivos personales', GETDATE());

INSERT INTO historial_citas (id_cita, id_usuario, estado_anterior, estado_nuevo, motivo, fecha_cambio)
    VALUES (@citaFri9, @idFabiola, 'PROGRAMADA', 'REAGENDADA', 'Se reagendo la cita por solicitud del paciente', GETDATE());

INSERT INTO historial_citas (id_cita, id_usuario, estado_anterior, estado_nuevo, motivo, fecha_cambio)
    VALUES (@citaThu4, @idFabiola, 'PROGRAMADA', 'PROGRAMADA', 'Cita confirmada para revision de lunares', GETDATE());

-- ============ CONSULTAS MEDICAS (citas atendidas) ============
DECLARE @consMon1 INT, @consMon2 INT, @consMon3 INT, @consMon4 INT;
DECLARE @consTue1 INT, @consTue2 INT, @consTue3 INT, @consTue4 INT;
DECLARE @consWed1 INT, @consWed2 INT, @consWed3 INT, @consWed4 INT;
DECLARE @consThu1 INT, @consThu2 INT, @consThu3 INT, @consFri1 INT;

-- Lunes - Saul
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaMon1, DATEADD(minute, 545, @lunesIni), 'Fiebre y dolor de cabeza',
            'Temperatura de 38.5 grados, cefalea frontal, malestar general',
            'Infeccion respiratoria leve');
SET @consMon1 = SCOPE_IDENTITY();

-- Lunes - Camila
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaMon2, DATEADD(minute, 665, @lunesIni), 'Control de odontologia infantil',
            'Sin sintomatologia aguda, paciente en revision',
            'Paciente sano, control de rutina');
SET @consMon2 = SCOPE_IDENTITY();

-- Lunes - Guadalupe (dermatologia)
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaMon3, DATEADD(minute, 515, @lunesIni), 'Erupcion cutanea en brazos',
            'Lesiones eritematosas pruriginosas en antebrazos, duracion 3 dias',
            'Dermatitis de contacto');
SET @consMon3 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaMon4, DATEADD(minute, 605, @lunesIni), 'Revision de lunar',
            'Lunar en espalda con cambio de forma y color, asimetria visible',
            'Lunar displasico, biopsia recomendada');
SET @consMon4 = SCOPE_IDENTITY();

-- Martes - Saul
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaTue1, DATEADD(minute, 605, @martes), 'Dolor abdominal',
            'Dolor tipo colico en epigastrio, nauseas ocasionales',
            'Gastritis aguda');
SET @consTue1 = SCOPE_IDENTITY();

-- Martes - Roberto
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaTue2, DATEADD(minute, 845, @martes), 'Palpitaciones',
            'Palpitaciones intermitentes, fatiga al esfuerzo',
            'Arritmia benigna en seguimiento');
SET @consTue2 = SCOPE_IDENTITY();

-- Martes - Guadalupe
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaTue3, DATEADD(minute, 545, @martes), 'Acne severo en rostro',
            'Nodulos inflamados en zona T, cicatrices previas, tratamiento previo fallido',
            'Acne quistico severo, iniciar isotretinoina');
SET @consTue3 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaTue4, DATEADD(minute, 635, @martes), 'Caída del cabello',
            'Pérdida difusa en cuero cabelludo,��ntesis de 3 meses',
            'Alopecia androgenica incipiente, iniciar tratamiento topico');
SET @consTue4 = SCOPE_IDENTITY();

-- Miercoles - Saul
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaWed1, DATEADD(minute, 545, @miercoles), 'Consulta por rinitis alergica',
            'Estornudos frecuentes, prurito nasal, lagrimeo',
            'Rinitis alergica estacional');
SET @consWed1 = SCOPE_IDENTITY();

-- Miercoles - Camila
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaWed2, DATEADD(minute, 605, @miercoles), 'Dolor de oido en nino',
            'Otalgia derecha, fiebre moderada, irritabilidad',
            'Otitis media aguda');
SET @consWed2 = SCOPE_IDENTITY();

-- Miercoles - Guadalupe
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaWed3, DATEADD(minute, 485, @miercoles), 'Hongos en las uñas',
            'Onicomicosis en uñas de pies, engrosamiento y decoloración',
            'Onicomicosis, iniciar antifungico oral');
SET @consWed3 = SCOPE_IDENTITY();

INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaWed4, DATEADD(minute, 695, @miercoles), 'Psoriasis en codos',
            'Placas escamosas eritematosas en ambos codos, prurito leve',
            'Psoriasis en placas, tratamiento topico con corticoide');
SET @consWed4 = SCOPE_IDENTITY();

-- Jueves - Saul
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaThu1, DATEADD(minute, 575, @jueves), 'Control de diabetes',
            'Control mensual, glucemia en ayunas de 145',
            'Diabetes tipo 2 controlada');
SET @consThu1 = SCOPE_IDENTITY();

-- Jueves - Roberto
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaThu2, DATEADD(minute, 695, @jueves), 'Dolor toracico',
            'Dolor opresivo leve, antecedente de hipertension',
            'Angina estable, seguimiento');
SET @consThu2 = SCOPE_IDENTITY();

-- Jueves - Guadalupe
INSERT INTO consultas_medicas (id_cita, fecha_consulta, motivo_consulta, sintomatologia, diagnostico)
    VALUES (@citaThu3, DATEADD(minute, 515, @jueves), 'Vitiligo en manos',
            'Manchas despigmentadas en ambas manos, progresion gradual',
            'Vitiligo, fototerapia recomendada');
SET @consThu3 = SCOPE_IDENTITY();

-- Viernes - Saul
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

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Antiinflamatorios')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Antiinflamatorios', 'Medicamentos para reducir inflamacion');

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Dermatologicos')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Dermatologicos', 'Tratamientos para afecciones de la piel');

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Gastrointestinal')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Gastrointestinal', 'Medicamentos para el sistema digestivo');

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Cardiovasculares')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Cardiovasculares', 'Medicamentos para el sistema cardiovascular');

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Respiratorios')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Respiratorios', 'Medicamentos para afecciones respiratorias');

IF NOT EXISTS (SELECT 1 FROM categorias_medicamentos WHERE nombre = 'Antifungicos')
    INSERT INTO categorias_medicamentos (nombre, descripcion) VALUES
    ('Antifungicos', 'Medicamentos para infecciones por hongos');

-- ============ MEDICAMENTOS ============
DECLARE @catAnalg   INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Analgesicos');
DECLARE @catAntib   INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Antibioticos');
DECLARE @catAntihis INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Antihistaminicos');
DECLARE @catVit     INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Vitaminas y Suplementos');
DECLARE @catInflam  INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Antiinflamatorios');
DECLARE @catDerma   INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Dermatologicos');
DECLARE @catGastro  INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Gastrointestinal');
DECLARE @catCardio  INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Cardiovasculares');
DECLARE @catRespi   INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Respiratorios');
DECLARE @catAntifun INT = (SELECT id_categoria FROM categorias_medicamentos WHERE nombre = 'Antifungicos');

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

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Naproxeno')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catInflam, 'Naproxeno', 'Naproxeno sodico', 'Tabletas', 'mg', '500', 60, 55, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Diclofenaco')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catInflam, 'Diclofenaco', 'Diclofenaco sodico', 'Tabletas', 'mg', '75', 45, 40, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Betametasona crema')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catDerma, 'Betametasona crema', 'Betametasona valerato', 'Crema', 'g', '0.05', 80, 73, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Clotrimazol crema')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catAntifun, 'Clotrimazol crema', 'Clotrimazol', 'Crema', 'g', '1', 50, 47, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Isotretinoina')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catDerma, 'Isotretinoina', 'Isotretinoina', 'Capsulas', 'mg', '20', 30, 26, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Omeprazol')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catGastro, 'Omeprazol', 'Omeprazol', 'Capsulas', 'mg', '20', 90, 82, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Enalapril')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catCardio, 'Enalapril', 'Enalapril maleato', 'Tabletas', 'mg', '10', 60, 57, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Metformina')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catGastro, 'Metformina', 'Clorhidrato de metformina', 'Tabletas', 'mg', '850', 100, 95, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Salbutamol spray')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catRespi, 'Salbutamol spray', 'Salbutamol', 'Spray', 'dosis', '100 mcg', 40, 36, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Minoxidil topico')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catDerma, 'Minoxidil topico', 'Minoxidil', 'Solucion topica', 'ml', '5', 30, 28, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Cetirizina')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catAntihis, 'Cetirizina', 'Clorhidrato de cetirizina', 'Tabletas', 'mg', '10', 70, 65, DATEADD(year, 2, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Prednisona')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catInflam, 'Prednisona', 'Prednisona', 'Tabletas', 'mg', '5', 50, 48, DATEADD(year, 1, @hoy));

IF NOT EXISTS (SELECT 1 FROM medicamentos WHERE nombre_comercial = 'Acido folico')
    INSERT INTO medicamentos (id_categoria, nombre_comercial, nombre_generico, presentacion, unidad_medida, concentracion, stock_inicial, stock_disponible, fecha_vencimiento) VALUES
    (@catVit, 'Acido folico', 'Acido pteroil-L-glutamico', 'Tabletas', 'mg', '5', 100, 97, DATEADD(year, 3, @hoy));

-- ============ MOVIMIENTOS DE INVENTARIO (compras iniciales) ============
DECLARE @medPara INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Paracetamol');
DECLARE @medIbu  INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Ibuprofeno');
DECLARE @medAmo  INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Amoxicilina');
DECLARE @medLor  INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Loratadina');
DECLARE @medVitC INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Vitamina C');
DECLARE @medVitD INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Vitamina D3');
DECLARE @medNap  INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Naproxeno');
DECLARE @medDicl INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Diclofenaco');
DECLARE @medBeta INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Betametasona crema');
DECLARE @medClot INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Clotrimazol crema');
DECLARE @medIsot INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Isotretinoina');
DECLARE @medOmep INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Omeprazol');
DECLARE @medEnal INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Enalapril');
DECLARE @medMetf INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Metformina');
DECLARE @medSalm INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Salbutamol spray');
DECLARE @medMino INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Minoxidil topico');
DECLARE @medCeti INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Cetirizina');
DECLARE @medPred INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Prednisona');
DECLARE @medAciF INT = (SELECT id_medicamento FROM medicamentos WHERE nombre_comercial = 'Acido folico');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medPara AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medPara, @idFabiola, 'ENTRADA', 120, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medIbu AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medIbu, @idFabiola, 'ENTRADA', 80, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medAmo AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medAmo, @idFabiola, 'ENTRADA', 60, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medLor AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medLor, @idFabiola, 'ENTRADA', 50, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medVitC AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medVitC, @idFabiola, 'ENTRADA', 100, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medVitD AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medVitD, @idFabiola, 'ENTRADA', 70, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medNap AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medNap, @idFabiola, 'ENTRADA', 60, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medDicl AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medDicl, @idFabiola, 'ENTRADA', 45, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medBeta AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medBeta, @idFabiola, 'ENTRADA', 80, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medClot AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medClot, @idFabiola, 'ENTRADA', 50, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medIsot AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medIsot, @idFabiola, 'ENTRADA', 30, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medOmep AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medOmep, @idFabiola, 'ENTRADA', 90, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medEnal AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medEnal, @idFabiola, 'ENTRADA', 60, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medMetf AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medMetf, @idFabiola, 'ENTRADA', 100, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medSalm AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medSalm, @idFabiola, 'ENTRADA', 40, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medMino AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medMino, @idFabiola, 'ENTRADA', 30, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medCeti AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medCeti, @idFabiola, 'ENTRADA', 70, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medPred AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medPred, @idFabiola, 'ENTRADA', 50, @hoyInicio, 'Compra inicial');

IF NOT EXISTS (SELECT 1 FROM movimientos_inventario WHERE id_medicamento = @medAciF AND motivo = 'Compra inicial')
    INSERT INTO movimientos_inventario (id_medicamento, id_usuario, tipo_movimiento, cantidad, fecha_movimiento, motivo) VALUES
    (@medAciF, @idFabiola, 'ENTRADA', 100, @hoyInicio, 'Compra inicial');

-- ============ RECETAS DETALLES ============
-- Saul - infeccion respiratoria
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consMon1, @medPara, 10, 'Tomar 1 tableta cada 8 horas', '1 tableta (500mg)', 'Cada 8 horas', '3 dias', 'DISPENSADA');

-- Saul - gastritis
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consTue1, @medPara, 15, 'Tomar 1 tableta cada 8 horas si hay dolor', '1 tableta (500mg)', 'Cada 8 horas', 'C/3 dias cuando haya dolor', 'DISPENSADA');

INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consTue1, @medOmep, 14, 'Tomar 1 capsula en ayunas', '1 capsula (20mg)', '1 vez al dia en ayunas', '14 dias', 'PRESCRITA');

-- Saul - rinitis alergica
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consWed1, @medLor, 10, 'Tomar 1 tableta cada 24 horas', '1 tableta (10mg)', 'Cada 24 horas', '5 dias', 'PRESCRITA');

INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consWed1, @medCeti, 10, 'Tomar 1 tableta cada 24 horas', '1 tableta (10mg)', 'Cada 24 horas', '10 dias', 'PRESCRITA');

-- Saul - diabetes
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consThu1, @medMetf, 30, 'Tomar 1 tableta de 850mg cada 12 horas con alimentos', '1 tableta (850mg)', 'Cada 12 horas', '15 dias', 'PRESCRITA');

-- Saul - faringitis
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consFri1, @medAmo, 15, 'Tomar 1 capsula de 500mg cada 8 horas', '1 capsula (500mg)', 'Cada 8 horas', '5 dias', 'PRESCRITA');

INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consFri1, @medPara, 10, 'Tomar 1 tableta cada 6 horas si hay fiebre', '1 tableta (500mg)', 'Cada 6 horas si hay fiebre', '3 dias', 'PRESCRITA');

-- Saul - rinitis (dosis doble de antihistaminico)
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consWed1, @medPred, 7, 'Tomar 1 tableta de 5mg cada 24 horas, reducir dosis', '1 tableta (5mg)', 'Cada 24 horas', '5 dias (reducir gradualmente)', 'PRESCRITA');

-- Roberto - arritmia
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consTue2, @medEnal, 30, 'Tomar 1 tableta de 10mg cada 12 horas', '1 tableta (10mg)', 'Cada 12 horas', '15 dias', 'PRESCRITA');

-- Roberto - angina
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consThu2, @medEnal, 30, 'Tomar 1 tableta de 10mg cada 12 horas, seguimiento en 1 mes', '1 tableta (10mg)', 'Cada 12 horas', '30 dias', 'PRESCRITA');

-- Guadalupe - dermatitis de contacto
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consMon3, @medBeta, 1, 'Aplicar capa fina en zona afectada 2 veces al dia', 'Capa fina', '2 veces al dia', '7 dias', 'DISPENSADA');

INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consMon3, @medCeti, 7, 'Tomar 1 tableta cada 24 horas', '1 tableta (10mg)', 'Cada 24 horas', '7 dias', 'DISPENSADA');

-- Guadalupe - acne quistico
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consTue3, @medIsot, 30, 'Tomar 1 capsula de 20mg con alimento graso, evitar embarazo', '1 capsula (20mg)', '1 vez al dia con alimentos', '30 dias', 'PRESCRITA');

-- Guadalupe - alopecia
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consTue4, @medMino, 1, 'Aplicar 1ml en cuero cabelludo 2 veces al dia', '1 ml', '2 veces al dia', '3 meses', 'PRESCRITA');

-- Guadalupe - onicomicosis
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consWed3, @medClot, 1, 'Aplicar en uñas afectadas 2 veces al dia', 'Capa fina', '2 veces al dia', '6 semanas', 'PRESCRITA');

-- Guadalupe - psoriasis
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consWed4, @medBeta, 1, 'Aplicar en placas de psoriasis 1 vez al dia', 'Capa fina', '1 vez al dia', '14 dias', 'PRESCRITA');

INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consWed4, @medPred, 14, 'Tomar 1 tableta de 5mg cada 24 horas, reducir gradualmente', '1 tableta (5mg)', 'Cada 24 horas', '14 dias (reducir gradualmente)', 'PRESCRITA');

-- Guadalupe - vitiligo
INSERT INTO recetas_detalles (id_consulta, id_medicamento, cantidad, indicaciones, dosis, frecuencia, duracion, estado)
    VALUES (@consThu3, @medBeta, 1, 'Aplicar crema en manchas despigmentadas 1 vez al dia', 'Capa fina', '1 vez al dia', '30 dias', 'PRESCRITA');

COMMIT TRANSACTION;

PRINT 'SEED COMPLETADO CORRECTAMENTE';