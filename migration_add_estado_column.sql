-- Script de migración para agregar la columna 'estado' a la tabla agendamiento
-- Ejecutar este script en la base de datos antes de iniciar la aplicación

-- 1. Agregar la columna estado (permitir NULL temporalmente)
ALTER TABLE agendamiento ADD COLUMN estado VARCHAR(50);

-- 2. Actualizar registros existentes basándose en la fecha/hora
-- Citas futuras -> Programada
UPDATE agendamiento 
SET estado = 'Programada' 
WHERE CONCAT(fecha, ' ', hora) > NOW();

-- Citas pasadas (más de 30 minutos) -> Completada (asumimos que fueron atendidas)
UPDATE agendamiento 
SET estado = 'Completada' 
WHERE CONCAT(fecha, ' ', hora) <= DATE_SUB(NOW(), INTERVAL 30 MINUTE)
AND estado IS NULL;

-- Citas en curso (dentro de los últimos 30 minutos) -> En Curso
UPDATE agendamiento 
SET estado = 'En Curso' 
WHERE CONCAT(fecha, ' ', hora) > DATE_SUB(NOW(), INTERVAL 30 MINUTE)
AND CONCAT(fecha, ' ', hora) <= NOW()
AND estado IS NULL;

-- 3. Hacer la columna NOT NULL ahora que todos los registros tienen valor
ALTER TABLE agendamiento MODIFY COLUMN estado VARCHAR(50) NOT NULL;

-- Verificar los cambios
SELECT estado, COUNT(*) as cantidad 
FROM agendamiento 
GROUP BY estado;
