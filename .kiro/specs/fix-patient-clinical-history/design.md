# Design Document

## Overview

Este diseño corrige el problema donde todos los pacientes ven historiales clínicos hardcodeados en su dashboard. La solución implica modificar el controlador `PacienteController` para obtener los historiales clínicos reales del paciente desde la base de datos, y actualizar el template `paciente.html` para renderizar estos datos dinámicamente usando Thymeleaf.

## Architecture

La arquitectura sigue el patrón MVC existente en la aplicación:

- **Controller**: `PacienteController` - maneja la petición del dashboard y prepara los datos
- **Repository**: `HistoriaClinicaRepository` - proporciona acceso a los historiales clínicos
- **View**: `layouts/paciente.html` - renderiza el dashboard con datos dinámicos
- **Entity**: `Historia_Clinica` - modelo de datos ya existente

## Components and Interfaces

### PacienteController

**Método a modificar**: `dashboard(Model model, HttpSession session)`

**Responsabilidades**:
- Obtener el paciente autenticado desde la sesión
- Consultar los historiales clínicos del paciente usando el repositorio
- Agregar los historiales al modelo para que estén disponibles en la vista
- Calcular el conteo de historiales

**Dependencias**:
- `HistoriaClinicaRepository` - para consultar historiales

### HistoriaClinicaRepository

**Método existente a usar**: `findByPacienteOrderByFechaAtencionDesc(Paciente paciente)`

Este método ya existe en el repositorio y retorna una lista de historiales clínicos ordenados por fecha de atención descendente.

### Template paciente.html

**Sección a modificar**: Historial Clínico

**Cambios**:
- Reemplazar los `<div class="history_card">` hardcodeados con un bucle `th:each`
- Usar expresiones Thymeleaf para mostrar datos dinámicos
- Agregar manejo para el caso cuando no hay historiales

## Data Models

### Historia_Clinica (existente)

```java
- idHistorial: Long
- fechaCreacion: Date
- fechaAtencion: Date
- antecedentes: String
- diagnostico: String
- tratamiento: String
- observaciones: String
- paciente: Paciente (ManyToOne)
- medico: Medico (ManyToOne)
```

### Datos en el Modelo

El controlador agregará al modelo:
- `historiales`: List<Historia_Clinica> - lista de historiales del paciente
- `totalHistoriales`: int - conteo de historiales (para actualizar el contador en el dashboard)

