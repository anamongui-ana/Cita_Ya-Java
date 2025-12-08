# Cambios Implementados: Sistema de Estados de Citas

## Resumen
Se implementó un sistema completo de gestión de estados para las citas médicas, permitiendo rastrear el ciclo de vida de cada cita y proporcionando acciones para que los médicos puedan atender y cancelar citas.

## Cambios Realizados

### 1. Base de Datos
**Archivo:** `migration_add_estado_column.sql`

- Se agregó la columna `estado` a la tabla `agendamiento`
- Estados posibles: `Programada`, `En Curso`, `Completada`, `Cancelada`, `No Asistió`
- Script de migración que actualiza registros existentes basándose en fecha/hora

**IMPORTANTE:** Ejecutar el script SQL antes de iniciar la aplicación:
```sql
mysql -u usuario -p nombre_base_datos < migration_add_estado_column.sql
```

### 2. Entidad Agendamiento
**Archivo:** `src/main/java/proyecto/conexiondb/JPA/Entity/Agendamiento.java`

- Agregado campo `estado` (String, NOT NULL)
- Agregados getters y setters para el estado

### 3. Repositorio
**Archivo:** `src/main/java/proyecto/conexiondb/JPA/Repository/AgendamientoRepository.java`

Nuevos métodos agregados:
- `countByMedicoAndFechaAndEstado()` - Contar citas por médico, fecha y estado
- `countByMedicoAndMesAndAnioAndEstado()` - Contar citas por médico, mes, año y estado

### 4. Controlador del Médico
**Archivo:** `src/main/java/proyecto/conexiondb/JPA/Controller/MedicoDashboardController.java`

#### Nuevas funcionalidades:

**a) Actualización automática de estados:**
- Método `actualizarEstadosCitas()` - Actualiza todas las citas del médico
- Método `actualizarEstadoCita()` - Actualiza una cita individual basándose en:
  - Si es futura → `Programada`
  - Si está dentro de los 30 minutos → `En Curso`
  - Si pasaron más de 30 minutos sin atender → `No Asistió`

**b) Dashboard mejorado:**
- Muestra citas del día (solo Programadas)
- Muestra citas completadas del mes
- Muestra citas "No Asistió" del mes
- Calcula porcentaje de asistencia
- Filtra próximas citas (solo Programadas o En Curso)

**c) Lista de citas:**
- Agregado filtro por estado
- Actualiza estados antes de mostrar la lista

**d) Detalle de cita:**
- Actualiza el estado de la cita al verla
- Muestra el estado actual con badge de color

**e) Nueva acción - Cancelar cita:**
- Endpoint POST `/medico/dashboard/citas/{id}/cancelar`
- Solo permite cancelar citas Programadas o En Curso
- Requiere confirmación del usuario

**f) Atender paciente:**
- Modificado `guardarHistoria()` para recibir parámetro `citaId`
- Cuando se guarda una historia desde una cita, marca automáticamente la cita como `Completada`

### 5. Controlador de Agendamiento
**Archivo:** `src/main/java/proyecto/conexiondb/JPA/Controller/AgendamientoController.java`

- Modificado método `store()` - Las nuevas citas se crean con estado `Programada`
- Modificado método `guardar()` - Las nuevas citas se crean con estado `Programada`

### 6. Vistas

#### a) Detalle de Cita
**Archivo:** `src/main/resources/templates/medico/cita-detalle.html`

Cambios:
- Agregado badge de estado con colores:
  - Azul: Programada
  - Naranja: En Curso
  - Verde: Completada
  - Gris: Cancelada
  - Rojo: No Asistió
- Agregado botón "Atender Paciente" (visible solo si está Programada o En Curso)
- Agregado botón "Cancelar Cita" con confirmación (visible solo si está Programada o En Curso)
- Mensajes informativos para citas Completadas, Canceladas o No Asistió

#### b) Lista de Citas
**Archivo:** `src/main/resources/templates/medico/citas.html`

Cambios:
- Agregada columna "Estado" con badges de colores
- Agregado filtro desplegable de estado (Todas, Programada, En Curso, Completada, Cancelada, No Asistió)
- Estilos CSS para los badges de estado

#### c) Nueva Historia Clínica
**Archivo:** `src/main/resources/templates/medico/nueva-historia.html`

Cambios:
- Agregado campo oculto `citaId` para vincular la historia con una cita
- Cuando se accede desde "Atender Paciente", se pasa el ID de la cita

## Flujo de Uso

### Para el Médico:

1. **Ver citas:**
   - Accede a "Mis Citas"
   - Ve el estado de cada cita con colores
   - Puede filtrar por estado

2. **Atender una cita:**
   - Hace clic en "Ver Detalle" de una cita
   - Si está Programada o En Curso, ve el botón "Atender Paciente"
   - Hace clic en "Atender Paciente"
   - Llena el formulario de historia clínica
   - Al guardar, la cita se marca automáticamente como "Completada"

3. **Cancelar una cita:**
   - Hace clic en "Ver Detalle" de una cita
   - Si está Programada o En Curso, ve el botón "Cancelar Cita"
   - Hace clic y confirma
   - La cita se marca como "Cancelada"

4. **Actualización automática:**
   - Cuando el médico accede al dashboard o lista de citas
   - El sistema actualiza automáticamente los estados:
     - Citas que pasaron más de 30 minutos sin atender → "No Asistió"
     - Citas dentro de los 30 minutos → "En Curso"

## Estados de Cita

| Estado | Descripción | Color |
|--------|-------------|-------|
| Programada | Cita futura, aún no ha llegado la hora | Azul |
| En Curso | La hora de la cita es ahora (dentro de 30 min) | Naranja |
| Completada | El médico atendió al paciente | Verde |
| Cancelada | La cita fue cancelada por el médico | Gris |
| No Asistió | Pasaron más de 30 min y no fue atendida | Rojo |

## Reglas de Negocio

1. Las nuevas citas se crean con estado "Programada"
2. Una cita pasa a "En Curso" cuando falta menos de 30 minutos para su hora
3. Una cita pasa a "No Asistió" si pasaron más de 30 minutos sin ser atendida
4. Solo se pueden cancelar citas "Programadas" o "En Curso"
5. Al crear una historia clínica desde una cita, esta se marca como "Completada"
6. Los estados "Completada", "Cancelada" y "No Asistió" son finales (no se actualizan automáticamente)

## Archivos Modificados

1. `src/main/java/proyecto/conexiondb/JPA/Entity/Agendamiento.java`
2. `src/main/java/proyecto/conexiondb/JPA/Repository/AgendamientoRepository.java`
3. `src/main/java/proyecto/conexiondb/JPA/Controller/MedicoDashboardController.java`
4. `src/main/java/proyecto/conexiondb/JPA/Controller/AgendamientoController.java`
5. `src/main/resources/templates/medico/cita-detalle.html`
6. `src/main/resources/templates/medico/citas.html`
7. `src/main/resources/templates/medico/nueva-historia.html`

## Archivos Nuevos

1. `migration_add_estado_column.sql` - Script de migración de base de datos
2. `CAMBIOS_ESTADO_CITAS.md` - Este archivo de documentación
