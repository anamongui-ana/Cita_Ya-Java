# Requirements Document

## Introduction

Este documento describe los requisitos para corregir el problema donde todos los pacientes ven el mismo historial clínico hardcodeado en su dashboard, en lugar de ver sus propios historiales clínicos almacenados en la base de datos.

## Glossary

- **Sistema**: La aplicación web Cita Ya
- **Paciente**: Usuario registrado que puede agendar citas y tiene historiales clínicos
- **Historial Clínico**: Registro médico que contiene información sobre consultas, diagnósticos y tratamientos de un paciente específico
- **Dashboard**: Panel principal que ve el paciente al iniciar sesión
- **Controlador**: Componente del sistema que maneja las peticiones HTTP y prepara los datos para las vistas

## Requirements

### Requirement 1

**User Story:** Como paciente, quiero ver únicamente mis propios historiales clínicos en mi dashboard, para que pueda revisar mi información médica personal.

#### Acceptance Criteria

1. WHEN el sistema carga el dashboard del paciente THEN el sistema SHALL obtener los historiales clínicos asociados al paciente autenticado desde la base de datos
2. WHEN el sistema muestra los historiales clínicos THEN el sistema SHALL mostrar únicamente los historiales que pertenecen al paciente autenticado
3. WHEN un paciente no tiene historiales clínicos THEN el sistema SHALL mostrar un mensaje indicando que no hay historiales disponibles
4. WHEN el sistema muestra un historial clínico THEN el sistema SHALL mostrar la fecha de atención, el médico que lo atendió y la especialidad o tipo de consulta
5. WHEN el sistema cuenta los historiales clínicos THEN el sistema SHALL mostrar el número correcto de historiales del paciente autenticado

### Requirement 2

**User Story:** Como desarrollador, quiero que el template del dashboard use datos dinámicos de la base de datos, para que cada paciente vea información personalizada.

#### Acceptance Criteria

1. WHEN el template renderiza la sección de historiales clínicos THEN el template SHALL usar datos proporcionados por el controlador mediante Thymeleaf
2. WHEN no existen datos hardcodeados en el template THEN el template SHALL obtener todos los datos de forma dinámica del modelo
3. WHEN el template itera sobre los historiales THEN el template SHALL usar directivas de Thymeleaf para generar elementos HTML dinámicamente
4. WHEN el template muestra información de un historial THEN el template SHALL formatear las fechas correctamente usando utilidades de Thymeleaf
