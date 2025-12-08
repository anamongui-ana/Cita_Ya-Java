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

@Entity
@Table(name="agendamiento")
public class Agendamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "id_agendamiento")
    private Long IdAgendamiento;

    @Column( name ="cita",length = 255, nullable = false)
    private String cita;

    @Column(name="fecha",nullable = false)
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fecha;

    @Column(name="hora",nullable = false)
    private String hora;

    @Column(name="estado", length = 50, nullable = false)
    private String estado; // Programada, En Curso, Completada, Cancelada, No Asistió

    // RELACIÓN con paciente
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;
    
    // RELACIÓN con médico (opcional para compatibilidad con citas antiguas)
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
