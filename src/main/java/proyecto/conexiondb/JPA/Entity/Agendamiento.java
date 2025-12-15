package proyecto.conexiondb.JPA.Entity;

import java.sql.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * ENTIDAD AGENDAMIENTO - Representa las citas médicas en el sistema
 * 
 * Esta clase mapea la tabla 'agendamiento' y gestiona toda la información
 * relacionada con las citas médicas: fecha, hora, tipo de consulta, 
 * paciente asignado, médico asignado y estado actual de la cita.
 * 
 * FLUJO DE ESTADOS:
 * Programada -> En Curso -> Completada
 *            -> Cancelada
 *            -> No Asistió
 */
@Entity
@Table(name="agendamiento")
public class Agendamiento {

    // ===== IDENTIFICACIÓN ====
    
    /**
     * ID único del agendamiento - Clave primaria autoincremental
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_agendamiento")
    private Long IdAgendamiento;

    // ===== INFORMACIÓN DE LA CITA =====    
    /**
     * Tipo de cita o especialidad médica solicitada
     * Ejemplos: "Medicina General", "Cardiología", "Pediatría", etc.
     */
    @Column( name ="cita",length = 255, nullable = false)
    private String cita;

    /**
     * Fecha programada para la cita médica
     * Formato: yyyy-MM-dd (compatible con input type="date")
     * RESTRICCIÓN: No se pueden agendar citas para hoy o fechas pasadas
     */
    @Column(name="fecha",nullable = false)
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha;

    /**
     * Hora programada para la cita en formato HH:mm
     * Ejemplos: "08:00", "14:30", "16:00"
     * RESTRICCIÓN: Un médico solo puede tener 1 cita por hora
     */
    @Column(name="hora",nullable = false)
    private String hora;

    /**
     * Estado actual de la cita médica:
     Programada Cita agendada, esperando el dia de atención
     En Curso Paciente está siendo atendido actualmente
     Completad Cita finalizada exitosamente
     Cancelada Cita cancelada por paciente o médico
     No Asistio Paciente no se presentó a la cita
     */
    @Column(name="estado", length = 50, nullable = false)
    private String estado; 

    // ===== RELACIONES CON OTRAS ENTIDADES =====
    
    /**
     * RELACIÓN MANY-TO-ONE con Paciente
     * Un paciente puede tener múltiples citas, pero cada cita pertenece a un solo paciente
     * Campo obligatorio - toda cita debe tener un paciente asignado
     */
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;
    
    /**
     * RELACIÓN MANY-TO-ONE con Médico
     * Un médico puede tener múltiples citas, pero cada cita es atendida por un solo médico
     * Campo opcional para mantener compatibilidad con citas antiguas que no tenían médico asignado
     */
    @ManyToOne
    @JoinColumn(name = "id_medico", nullable = true)
    private Medico medico;

    

    public Long getIdAgendamiento() {
        return IdAgendamiento;
    }

    public void setIdAgendamiento(Long idAgendamiento) {
        IdAgendamiento = idAgendamiento;
    }

    public String getCita() {
        return cita;
    }

    public void setCita(String cita) {
        this.cita = cita;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
}
