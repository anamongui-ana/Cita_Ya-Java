


USE db_cita_ya;

-- ============================================
-- 1) LIMPIAR TABLAS (OPCIONAL)
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

-- ============================================
-- 2) INSERTAR ADMINISTRADORES
-- ============================================
INSERT INTO administradores (tipo_doc, numero_doc, nombre, apellido, genero, telefono, correo, contraseña, estado) VALUES
('CC', '1095842331', 'Carolina', 'Montoya', 'F', '3125849912', 'carolina.admin@citaya.com', 'AdmCaro92**AA', 1),
('CC', '1032445889', 'Ricardo', 'Delgado', 'M', '3017784421', 'ricardo.admin@citaya.com', 'RicAdmin#88Pro', 1);

-- ============================================
-- 3) INSERTAR MÉDICOS (CON TUS 6 ESPECIALIDADES)
-- ============================================
INSERT INTO medicos (tipo_doc, numero_doc, nombre, apellido, genero, especialidad, telefono, correo, contraseña, estado) VALUES
('CC', '1025488994', 'Juan', 'Pérez', 'M', 'Medicina General', '3154489921', 'j.perez@citaya.com', 'MedJp#2024Strong', 1),
('CC', '1102345991', 'Laura', 'Gómez', 'F', 'Ortopedia', '3128897441', 'l.gomez@citaya.com', 'LgomezOrto*22Ok', 1),
('CC', '1039984522', 'Ana', 'Ramírez', 'F', 'Cardiología', '3204458712', 'a.ramirez@citaya.com', 'CardioAR@12567', 1),
('CC', '1002294478', 'Mateo', 'Vargas', 'M', 'Pediatría', '3137790022', 'm.vargas@citaya.com', 'PediaMV!5050Pro', 1),
('CC', '1045589902', 'Sofía', 'Cárdenas', 'F', 'Psicología', '3198824476', 's.cardenas@citaya.com', 'PsicoSC_2099XX', 1),
('CC', '1028894711', 'Daniel', 'Quintero', 'M', 'Dermatología', '3007745523', 'd.quintero@citaya.com', 'DermaDQ#4477Plus', 1);

-- ============================================
-- 4) INSERTAR PACIENTES
-- ============================================
INSERT INTO pacientes (tipo_doc, numero_doc, nombre, apellido, genero, telefono, correo, fecha_nacimiento, tipo_sangre, direccion, contraseña) VALUES
('CC', '1012339844', 'María Fernanda', 'Torres', 'F', '3217789456', 'mftorres@email.com', '1996-04-15', 'O+', 'Calle 45 #22-31', 'MfT*2024Strong'),
('CC', '1094482315', 'Carlos Andrés', 'Pérez', 'M', '3009912234', 'carlos.perez@email.com', '1989-11-22', 'A+', 'Carrera 10 #30-21', 'CpA_11Seguro99'),
('CC', '1023345512', 'Ana Valeria', 'Sánchez', 'F', '3137809921', 'anasz@email.com', '1998-06-10', 'B-', 'Av. Central 101', 'AvS@98PowerPass'),
('CC', '1019982334', 'Esteban Rafael', 'Narváez', 'M', '3208814421', 'er.narvaez@email.com', '1994-09-17', 'AB+', 'Cra 9 #12-55', 'ErN*2024SeguroF'),
('CC', '1033457621', 'Lucía Paloma', 'Cáceres', 'F', '3186649822', 'lp.caceres@email.com', '1991-12-02', 'O-', 'Calle 90 #55-12', 'LcP@21LevelUp!'),
('CC', '1047852231', 'Julián Sebastián', 'Moreira', 'M', '3159981123', 'js.moreira@email.com', '1990-02-27', 'A-', 'Cra 15 #45-66', 'Jsm*44UltraPass'),
('CC', '1003327188', 'Valentina Nicole', 'López', 'F', '3208845121', 'vn.lopez@email.com', '1997-08-19', 'B+', 'Av. 3 #44-10', 'Vnl@31SecurePlus'),
('CC', '1029875543', 'Sofía Renata', 'Chamorro', 'F', '3188841200', 'sr.chamorro@email.com', '2000-03-12', 'AB-', 'Barrio Jardín', 'Src_2024LongPass');

-- ============================================
-- 5) INSERTAR AGENDAMIENTO
-- ============================================
INSERT INTO agendamiento (cita, fecha, hora, id_paciente, id_medico) VALUES
('Medicina General', '2025-02-10', '09:00', 1, 1),
('Cardiología', '2025-02-15', '10:30', 3, 3),
('Psicología', '2025-03-01', '14:00', 7, 5),
('Pediatría', '2025-03-05', '11:00', 5, 4),
('Ortopedia', '2025-03-10', '08:00', 2, 2),
('Psicología', '2025-03-15', '16:00', 8, 5),
('Dermatología', '2025-03-17', '15:00', 4, 6),
('Medicina General', '2025-03-22', '09:30', 6, 1);

-- ============================================
-- 6) INSERTAR HISTORIA CLÍNICA
-- ============================================
INSERT INTO historia_clinica (id_paciente, id_medico, antecedentes, diagnostico, fecha_atencion, fecha_creacion) VALUES
(1, 1,
 'Alergias respiratorias desde la infancia. Episodios frecuentes en temporadas frías. Sin cirugías previas.',
 'Cuadro de infección respiratoria viral con congestión nasal, tos seca y malestar general.',
 '2025-01-12', CURDATE()),

(2, 2,
 'Dolor lumbar ocasional por malas posturas. No cuenta con cirugías previas.',
 'Lumbalgia mecánica sin signos de alarma. Recomendado reposo y fisioterapia.',
 '2025-02-04', CURDATE()),

(3, 3,
 'Antecedentes familiares de hipertensión y cardiopatías. Sin hospitalizaciones previas.',
 'Arritmia sinusal leve detectada en control general. Requiere seguimiento cada 6 meses.',
 '2025-03-22', CURDATE()),

(4, 6,
 'Piel sensible y episodios de dermatitis por contacto con químicos.',
 'Dermatitis irritativa leve. Tratamiento con hidratación intensiva y evitar agentes irritantes.',
 '2025-04-10', CURDATE()),

(5, 4,
 'Vacunación completa. Episodios de fiebre en la niñez sin complicaciones.',
 'Cuadro febril leve de origen viral. Se descarta infección bacteriana.',
 '2025-04-18', CURDATE()),

(6, 1,
 'Cefaleas tensionales por estrés laboral. No cirugías. Sin alergias conocidas.',
 'Cefalea tensional recurrente. Se recomienda manejo del estrés y analgesia básica.',
 '2025-05-02', CURDATE()),

(7, 5,
 'Antecedentes de ansiedad en épocas académicas intensas.',
 'Crisis de ansiedad con taquicardia. Manejo con terapia psicológica y técnicas de respiración.',
 '2025-05-14', CURDATE()),

(8, 6,
 'Alergia estacional leve. No antecedentes quirúrgicos.',
 'Rinitis alérgica con lagrimeo y estornudos constantes. Tratamiento antihistamínico.',
 '2025-06-01', CURDATE());
