package proyecto.conexiondb.JPA.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.conexiondb.JPA.Entity.Agendamiento;
import proyecto.conexiondb.JPA.Entity.Medico;
import proyecto.conexiondb.JPA.Entity.Paciente;

import java.sql.Date;
import java.util.List;

public interface AgendamientoRepository extends JpaRepository<Agendamiento, Long> {
    
    // Buscar agendamientos por paciente
    List<Agendamiento> findByPaciente(Paciente paciente);
    
    // Buscar agendamientos por médico
    List<Agendamiento> findByMedico(Medico medico);
    
    // Buscar agendamientos por fecha
    List<Agendamiento> findByFecha(Date fecha);
    
    // Contar agendamientos en una fecha y hora específica
    @Query("SELECT COUNT(a) FROM Agendamiento a WHERE a.fecha = :fecha AND a.hora = :hora")
    long countByFechaAndHora(@Param("fecha") Date fecha, @Param("hora") String hora);
    
    // Contar agendamientos de un médico específico en una fecha y hora
    @Query("SELECT COUNT(a) FROM Agendamiento a WHERE a.medico.idMedico = :idMedico AND a.fecha = :fecha AND a.hora = :hora")
    long countByMedicoAndFechaAndHora(@Param("idMedico") Long idMedico, @Param("fecha") Date fecha, @Param("hora") String hora);
    
    // Buscar médicos disponibles por especialidad
    @Query("SELECT m FROM Medico m WHERE m.especialidad = :especialidad AND m.estado = 1")
    java.util.List<proyecto.conexiondb.JPA.Entity.Medico> findMedicosByEspecialidad(@Param("especialidad") String especialidad);
    
    // Buscar solo agendamientos con médico asignado
    @Query("SELECT a FROM Agendamiento a WHERE a.medico IS NOT NULL")
    java.util.List<Agendamiento> findAllWithMedico();
    
    // ========== MÉTODOS PARA DASHBOARD DEL MÉDICO ==========
    
    // Buscar todas las citas de un médico ordenadas por fecha y hora
    List<Agendamiento> findByMedicoOrderByFechaAscHoraAsc(Medico medico);
    
    // Buscar citas de un médico en una fecha específica
    List<Agendamiento> findByMedicoAndFecha(Medico medico, Date fecha);
    
    // Contar citas de un médico en una fecha
    long countByMedicoAndFecha(Medico medico, Date fecha);
    
    // Contar citas de un médico en un mes específico
    @Query("SELECT COUNT(a) FROM Agendamiento a WHERE a.medico = :medico AND MONTH(a.fecha) = :mes AND YEAR(a.fecha) = :anio")
    long countByMedicoAndMesAndAnio(@Param("medico") Medico medico, @Param("mes") int mes, @Param("anio") int anio);
    
    // Obtener pacientes únicos de un médico
    @Query("SELECT DISTINCT a.paciente FROM Agendamiento a WHERE a.medico = :medico ORDER BY a.paciente.nombre ASC")
    List<Paciente> findDistinctPacientesByMedico(@Param("medico") Medico medico);
    
    // Contar citas por médico, fecha y estado
    long countByMedicoAndFechaAndEstado(Medico medico, Date fecha, String estado);
    
    // Contar citas por médico, mes, año y estado
    @Query("SELECT COUNT(a) FROM Agendamiento a WHERE a.medico = :medico AND MONTH(a.fecha) = :mes AND YEAR(a.fecha) = :anio AND a.estado = :estado")
    long countByMedicoAndMesAndAnioAndEstado(@Param("medico") Medico medico, @Param("mes") int mes, @Param("anio") int anio, @Param("estado") String estado);
}