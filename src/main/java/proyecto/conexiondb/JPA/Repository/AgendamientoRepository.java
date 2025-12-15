package proyecto.conexiondb.JPA.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.conexiondb.JPA.Entity.Agendamiento;
import proyecto.conexiondb.JPA.Entity.Medico;
import proyecto.conexiondb.JPA.Entity.Paciente;

import java.sql.Date;
import java.util.List;

/**
 * REPOSITORIO DE AGENDAMIENTOS - Interfaz para operaciones de base de datos
 * 
 * Este repositorio extiende JpaRepository para obtener métodos CRUD básicos
 * y define métodos personalizados para consultas específicas del negocio:
 * 
 * - Búsquedas por paciente, médico, fecha
 * - Validaciones de disponibilidad de médicos
 * - Estadísticas para dashboards
 * - Consultas para reportes
 * 
 * HERENCIA: JpaRepository<Agendamiento, Long>
 * - Agendamiento: Entidad que maneja
 * - Long: Tipo de la clave primaria (idAgendamiento)
 */
public interface AgendamientoRepository extends JpaRepository<Agendamiento, Long> {
    
    // =============== BÚSQUEDAS BÁSICAS POR ENTIDAD ===============
    
    /**
     * Busca todos los agendamientos de un paciente específico
     * Usado para mostrar el historial de citas del paciente
     */
    List<Agendamiento> findByPaciente(Paciente paciente);
    
    /**
     * Busca todos los agendamientos asignados a un médico específico
     * Usado en el dashboard del médico para ver sus citas
     */
    List<Agendamiento> findByMedico(Medico medico);
    
    /**
     * Busca todos los agendamientos en una fecha específica
     * Útil para ver la agenda diaria del sistema
     */
    List<Agendamiento> findByFecha(Date fecha);
    
    // =============== VALIDACIONES DE DISPONIBILIDAD ===============
    
    /**
     * Cuenta cuántos agendamientos hay en una fecha y hora específica
     * Usado para validar disponibilidad general del sistema
     */
    @Query("SELECT COUNT(a) FROM Agendamiento a WHERE a.fecha = :fecha AND a.hora = :hora")
    long countByFechaAndHora(@Param("fecha") Date fecha, @Param("hora") String hora);
    
    /**
     * VALIDACIÓN CRÍTICA: Cuenta agendamientos de un médico en fecha/hora específica
     * 
     * REGLA DE NEGOCIO: Un médico solo puede tener 1 cita por hora
     * Este método es fundamental para evitar doble agendamiento
     * 
     * @param idMedico ID del médico a verificar
     * @param fecha Fecha de la cita
     * @param hora Hora de la cita (formato HH:mm)
     * @return Número de citas (debe ser 0 para permitir nueva cita)
     */
    @Query("SELECT COUNT(a) FROM Agendamiento a WHERE a.medico.idMedico = :idMedico AND a.fecha = :fecha AND a.hora = :hora")
    long countByMedicoAndFechaAndHora(@Param("idMedico") Long idMedico, @Param("fecha") Date fecha, @Param("hora") String hora);
    
    // =============== CONSULTAS PARA SELECCIÓN DE MÉDICOS ===============
    
    /**
     * Busca médicos activos por especialidad
     * Usado en el formulario de agendamiento para mostrar médicos disponibles
     * 
     * NOTA: estado = 1 significa médico activo
     */
    @Query("SELECT m FROM Medico m WHERE m.especialidad = :especialidad AND m.estado = 1")
    java.util.List<proyecto.conexiondb.JPA.Entity.Medico> findMedicosByEspecialidad(@Param("especialidad") String especialidad);
    
    /**
     * Busca solo agendamientos que tienen médico asignado
     * Usado para compatibilidad con citas antiguas que podrían no tener médico
     */
    @Query("SELECT a FROM Agendamiento a WHERE a.medico IS NOT NULL")
    java.util.List<Agendamiento> findAllWithMedico();
    
    // =============== MÉTODOS PARA DASHBOARD DEL MÉDICO ===============
    
    /**
     * Busca todas las citas de un médico ordenadas cronológicamente
     * Usado en la vista de citas del médico para mostrar agenda ordenada
     */
    List<Agendamiento> findByMedicoOrderByFechaAscHoraAsc(Medico medico);
    
    /**
     * Busca citas de un médico en una fecha específica
     * Usado para filtrar la agenda diaria del médico
     */
    List<Agendamiento> findByMedicoAndFecha(Medico medico, Date fecha);
    
    /**
     * Cuenta citas de un médico en una fecha específica
     * Usado para estadísticas del dashboard médico
     */
    long countByMedicoAndFecha(Medico medico, Date fecha);
    
    /**
     * Cuenta citas de un médico en un mes específico
     * Usado para estadísticas mensuales en el dashboard
     * 
     * @param medico Médico a consultar
     * @param mes Mes (1-12)
     * @param anio Año (ej: 2024)
     * @return Número total de citas en ese mes
     */
    @Query("SELECT COUNT(a) FROM Agendamiento a WHERE a.medico = :medico AND MONTH(a.fecha) = :mes AND YEAR(a.fecha) = :anio")
    long countByMedicoAndMesAndAnio(@Param("medico") Medico medico, @Param("mes") int mes, @Param("anio") int anio);
    
    /**
     * Obtiene lista única de pacientes atendidos por un médico
     * Usado en la vista "Mis Pacientes" del dashboard médico
     * Evita duplicados y ordena alfabéticamente
     */
    @Query("SELECT DISTINCT a.paciente FROM Agendamiento a WHERE a.medico = :medico ORDER BY a.paciente.nombre ASC")
    List<Paciente> findDistinctPacientesByMedico(@Param("medico") Medico medico);
    
    /**
     * Cuenta citas por médico, fecha y estado específico
     * Usado para estadísticas detalladas (ej: cuántas completadas hoy)
     */
    long countByMedicoAndFechaAndEstado(Medico medico, Date fecha, String estado);
    
    /**
     * Cuenta citas por médico, mes, año y estado específico
     * Usado para reportes mensuales detallados por estado
     * 
     * @param medico Médico a consultar
     * @param mes Mes (1-12)
     * @param anio Año (ej: 2024)
     * @param estado Estado de la cita ("Completada", "Cancelada", etc.)
     * @return Número de citas con ese estado en ese mes
     */
    @Query("SELECT COUNT(a) FROM Agendamiento a WHERE a.medico = :medico AND MONTH(a.fecha) = :mes AND YEAR(a.fecha) = :anio AND a.estado = :estado")
    long countByMedicoAndMesAndAnioAndEstado(@Param("medico") Medico medico, @Param("mes") int mes, @Param("anio") int anio, @Param("estado") String estado);
}