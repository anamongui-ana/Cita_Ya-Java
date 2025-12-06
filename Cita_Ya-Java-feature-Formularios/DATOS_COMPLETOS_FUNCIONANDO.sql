-- ============================================
-- SCRIPT COMPLETO - DATOS QUE FUNCIONAN 100%
-- ============================================
-- Este script borra todo y crea datos nuevos con contraseñas que funcionan

USE db_cita_ya;

-- PASO 1: LIMPIAR TODO
-- ============================================
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM agendamiento;
DELETE FROM historia_clinica;
DELETE FROM pacientes;
DELETE FROM medicos;
DELETE FROM administradores;

ALTER TABLE agendamiento AUTO_INCREMENT = 1;
ALTER TABLE historia_clinica AUTO_INCREMENT = 1;
ALTER TABLE pacientes AUTO_INCREMENT = 1;
ALTER TABLE medicos AUTO_INCREMENT = 1;
ALTER TABLE administradores AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- PASO 2: INSERTAR ADMINISTRADORES
-- ============================================
INSERT INTO administradores (tipo_doc, numero_doc, nombre, apellido, genero, telefono, correo, contraseña, estado) VALUES
('CC', '1000000001', 'Carlos', 'Admin', 'M', '3001111111', 'admin@citaya.com', 'Admin123@', 1),
('CC', '1000000002', 'Ana', 'Gerente', 'F', '3002222222', 'ana@citaya.com', 'Ana1234@', 1);

-- PASO 3: INSERTAR MÉDICOS
-- ============================================
INSERT INTO medicos (tipo_doc, numero_doc, nombre, apellido, genero, especialidad, telefono, correo, contraseña, estado) VALUES
-- Medicina General
('CC', '2000000001', 'Juan', 'Pérez', 'M', 'Medicina General', '3101111111', 'juan.perez@citaya.com', 'Medico123@', 1),
('CC', '2000000002', 'Laura', 'Gómez', 'F', 'Medicina General', '3102222222', 'laura.gomez@citaya.com', 'Medico123@', 1),
('CC', '2000000003', 'Luis', 'Torres', 'M', 'Medicina General', '3103333333', 'luis.torres@citaya.com', 'Medico123@', 1),

-- Ortopedia
('CC', '2000000004', 'Ana', 'Ramírez', 'F', 'Ortopedia', '3104444444', 'ana.ramirez@citaya.com', 'Medico123@', 1),
('CC', '2000000005', 'Pedro', 'López', 'M', 'Ortopedia', '3105555555', 'pedro.lopez@citaya.com', 'Medico123@', 1),

-- Cardiología
('CC', '2000000006', 'Carlos', 'Martínez', 'M', 'Cardiología', '3106666666', 'carlos.martinez@citaya.com', 'Medico123@', 1),
('CC', '2000000007', 'Sofía', 'García', 'F', 'Cardiología', '3107777777', 'sofia.garcia@citaya.com', 'Medico123@', 1),

-- Psicología
('CC', '2000000008', 'Claudia', 'Suárez', 'F', 'Psicología', '3108888888', 'claudia.suarez@citaya.com', 'Medico123@', 1),
('CC', '2000000009', 'Andrés', 'Castro', 'M', 'Psicología', '3109999999', 'andres.castro@citaya.com', 'Medico123@', 1),

-- Pediatría
('CC', '2000000010', 'Paola', 'Navarro', 'F', 'Pediatría', '3110000000', 'paola.navarro@citaya.com', 'Medico123@', 1),
('CC', '2000000011', 'Diego', 'Silva', 'M', 'Pediatría', '3111111111', 'diego.silva@citaya.com', 'Medico123@', 1),

-- Dermatología
('CC', '2000000012', 'Valentina', 'Ortiz', 'F', 'Dermatología', '3112222222', 'valentina.ortiz@citaya.com', 'Medico123@', 1);

-- PASO 4: INSERTAR PACIENTES
-- ============================================
INSERT INTO pacientes (tipo_doc, numero_doc, nombre, apellido, genero, telefono, correo, fecha_nacimiento, tipo_sangre, direccion, contraseña) VALUES
('CC', '3000000001', 'Lucía', 'Fernández', 'F', '3201111111', 'lucia.fernandez@email.com', '1990-05-15', 'O+', 'Calle 45 #12-34', 'Paciente123@'),
('CC', '3000000002', 'Carlos', 'Moreno', 'M', '3202222222', 'carlos.moreno@email.com', '1985-08-22', 'A-', 'Carrera 10 #20-30', 'Paciente123@'),
('CC', '3000000003', 'Sofía', 'Rivas', 'F', '3203333333', 'sofia.rivas@email.com', '2000-01-10', 'B+', 'Cra 10 #20-55', 'Paciente123@'),
('CC', '3000000004', 'Diego', 'Jiménez', 'M', '3204444444', 'diego.jimenez@email.com', '1995-03-30', 'AB+', 'Av. Siempre Viva 123', 'Paciente123@'),
('CC', '3000000005', 'Valentina', 'Castaño', 'F', '3205555555', 'valentina.castano@email.com', '1988-07-12', 'O-', 'Calle 100 #50-20', 'Paciente123@'),
('CC', '3000000006', 'Esteban', 'Ortiz', 'M', '3206666666', 'esteban.ortiz@email.com', '1992-11-05', 'A+', 'Carrera 7 #80-50', 'Paciente123@'),
('CC', '3000000007', 'Camila', 'Silva', 'F', '3207777777', 'camila.silva@email.com', '1999-06-18', 'B-', 'Cra 15 #45-67', 'Paciente123@'),
('CC', '3000000008', 'Julián', 'Navarro', 'M', '3208888888', 'julian.navarro@email.com', '1983-09-25', 'AB-', 'Calle 80 #30-10', 'Paciente123@');

-- PASO 5: INSERTAR AGENDAMIENTOS
-- ============================================
INSERT INTO agendamiento (cita, fecha, hora, id_paciente, id_medico) VALUES
-- Citas futuras
('Medicina General', '2025-12-10', '09:00', 1, 1),
('Pediatría', '2025-12-11', '11:30', 2, 10),
('Medicina General', '2025-12-15', '08:30', 3, 2),
('Ortopedia', '2025-12-16', '10:00', 4, 4),
('Cardiología', '2025-12-17', '14:00', 5, 6),
('Psicología', '2025-12-18', '16:00', 6, 8),
('Pediatría', '2025-12-19', '13:00', 7, 11),
('Dermatología', '2025-12-20', '15:30', 8, 12),

-- Citas pasadas (para historial)
('Medicina General', '2025-09-02', '10:00', 1, 3),
('Dermatología', '2025-08-22', '11:00', 3, 12),
('Psicología', '2025-10-10', '09:00', 2, 9);

-- PASO 6: INSERTAR HISTORIA CLÍNICA
-- ============================================
INSERT INTO historia_clinica (antecedentes, diagnostico, fecha_creacion, id_medico, id_paciente) VALUES
('Sin antecedentes relevantes', 'Hipertensión leve', '2025-09-02', 1, 1),
('Alergia a penicilina', 'Resfriado común', '2025-12-11', 10, 2),
('Migrañas frecuentes desde la adolescencia', 'Migraña crónica', '2025-12-15', 2, 3),
('Lesión en rodilla tratada en 2022', 'Condromalacia rotuliana', '2025-12-16', 4, 4),
('Antecedente de arritmia leve', 'Arritmia sinusal', '2025-12-17', 6, 5),
('Ansiedad leve desde 2023', 'Trastorno de ansiedad generalizada', '2025-12-18', 8, 6),
('Control pediátrico sin complicaciones', 'Desarrollo normal', '2025-12-19', 11, 7),
('Irritación cutánea por alergia', 'Dermatitis alérgica', '2025-12-20', 12, 8);

-- PASO 7: VERIFICAR DATOS
-- ============================================
SELECT '=== DATOS INSERTADOS CORRECTAMENTE ===' as '';

SELECT '=== ADMINISTRADORES ===' as '';
SELECT id_administrador, numero_doc, nombre, apellido, contraseña FROM administradores;

SELECT '=== MÉDICOS ===' as '';
SELECT id_medico, numero_doc, nombre, apellido, especialidad, contraseña FROM medicos ORDER BY especialidad;

SELECT '=== PACIENTES ===' as '';
SELECT id_paciente, numero_doc, nombre, apellido, contraseña FROM pacientes;

SELECT '=== AGENDAMIENTOS ===' as '';
SELECT 
    a.id_agendamiento,
    a.cita,
    a.fecha,
    a.hora,
    CONCAT(p.nombre, ' ', p.apellido) as paciente,
    CONCAT(m.nombre, ' ', m.apellido) as medico
FROM agendamiento a
INNER JOIN pacientes p ON a.id_paciente = p.id_paciente
INNER JOIN medicos m ON a.id_medico = m.id_medico
ORDER BY a.fecha DESC;

SELECT '=== HISTORIA CLÍNICA ===' as '';
SELECT 
    h.id_historial,
    CONCAT(p.nombre, ' ', p.apellido) as paciente,
    CONCAT(m.nombre, ' ', m.apellido) as medico,
    h.diagnostico,
    h.fecha_creacion
FROM historia_clinica h
INNER JOIN pacientes p ON h.id_paciente = p.id_paciente
INNER JOIN medicos m ON h.id_medico = m.id_medico;

-- PASO 8: CREDENCIALES DE ACCESO
-- ============================================
SELECT '=== CREDENCIALES PARA LOGIN ===' as '';
SELECT '========================================' as '';
SELECT 'ADMINISTRADOR:' as tipo, '1000000001' as documento, 'Admin123@' as contraseña
UNION ALL
SELECT 'MÉDICO:', '2000000001', 'Medico123@'
UNION ALL
SELECT 'PACIENTE:', '3000000001', 'Paciente123@';
SELECT '========================================' as '';
SELECT 'TODAS las contraseñas son iguales por tipo:' as '';
SELECT '- Administradores: Admin123@' as '';
SELECT '- Médicos: Medico123@' as '';
SELECT '- Pacientes: Paciente123@' as '';
