-- =============================================
-- LIMPIEZA DE BASE DE DATOS — MediClinic Care
-- Elimina todos los registros en orden correcto
-- (respeta las dependencias de foreign keys)
-- =============================================

USE SistemaClinico;
GO

-- Eliminar en orden inverso de dependencias
DELETE FROM recetas_detalles;
DELETE FROM movimientos_inventario;
DELETE FROM consultas_medicas;
DELETE FROM historial_citas;
DELETE FROM citas;
DELETE FROM medicamentos;
DELETE FROM categorias_medicamentos;
DELETE FROM pacientes;
DELETE FROM medicos;
DELETE FROM usuarios;

-- Reiniciar identities
DBCC CHECKIDENT ('recetas_detalles', RESEED, 0);
DBCC CHECKIDENT ('movimientos_inventario', RESEED, 0);
DBCC CHECKIDENT ('consultas_medicas', RESEED, 0);
DBCC CHECKIDENT ('historial_citas', RESEED, 0);
DBCC CHECKIDENT ('citas', RESEED, 0);
DBCC CHECKIDENT ('medicamentos', RESEED, 0);
DBCC CHECKIDENT ('categorias_medicamentos', RESEED, 0);
DBCC CHECKIDENT ('pacientes', RESEED, 0);
DBCC CHECKIDENT ('medicos', RESEED, 0);
DBCC CHECKIDENT ('usuarios', RESEED, 0);

PRINT 'Base de datos limpia correctamente. Todos los registros eliminados.';
GO
