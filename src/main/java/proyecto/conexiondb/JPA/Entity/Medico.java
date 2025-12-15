package proyecto.conexiondb.JPA.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;

/**
 * ENTIDAD MÉDICO - Representa a los profesionales médicos del sistema
 * 
 * Esta clase mapea la tabla 'medicos' en la base de datos y contiene
 * la información profesional y personal de los médicos que atienden
 * en el sistema de citas médicas.
 * 
 * FUNCIONALIDADES DEL MÉDICO:
 * - Gestionar sus citas programadas
 * - Atender pacientes y crear historias clínicas
 * - Ver lista de sus pacientes
 * - Cambiar estados de las citas
 * - Agendar citas para pacientes
 */
@Entity
@Table(name="medicos")
public class Medico implements Serializable {
    private static final long serialVersionUID = 1L;

    // =============== IDENTIFICACIÓN ===============
    
    /**
     * ID único del médico - Clave primaria autoincremental
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medico")
    private Long idMedico;

    /**
     * Tipo de documento de identidad (CC, CE, PA, etc.)
     */
    @Column(name = "tipo_doc", length = 20, nullable = false)
    private String tipoDoc;

    /**
     * Número de documento de identidad
     * Campo único - no puede haber médicos con el mismo documento
     */
    @Column(name = "numero_doc", length = 30, nullable = false, unique = true)
    private String numeroDoc;

    // =============== INFORMACIÓN PERSONAL ===============
    
    /**
     * Nombre del médico
     */
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    /**
     * Apellido del médico
     */
    @Column(name = "apellido", length = 100, nullable = false)
    private String apellido;

    /**
     * Género del médico (M, F, Otro)
     */
    @Column(name = "genero", nullable = false)
    private String genero;

    // =============== INFORMACIÓN PROFESIONAL ===============
    
    /**
     * Especialidad médica del profesional
     * Ejemplos: "Medicina General", "Cardiología", "Pediatría", "Ginecología"
     * 
     * IMPORTANTE: Este campo se usa para:
     * - Filtrar médicos disponibles por especialidad en agendamiento
     * - Mostrar la especialidad en listados y reportes
     * - Agrupar médicos por área de atención
     */
    @Column(name = "especialidad", length = 100, nullable = false)
    private String especialidad;

    // =============== INFORMACIÓN DE CONTACTO ===============
    
    /**
     * Número de teléfono del médico - Campo opcional
     */
    @Column(length = 20)
    private String telefono;

    /**
     * Correo electrónico del médico
     * Campo único - usado para login y comunicaciones
     */
    @Column(length = 255, nullable = false, unique = true)
    private String correo;

    // =============== SEGURIDAD Y ESTADO ===============
    
    /**
     * Contraseña del médico para acceso al sistema
     * NOTA: Se almacena en texto plano (no recomendado para producción)
     */
    @Column(name = "contraseña", length = 255, nullable = false)
    private String contraseña;

    /**
     * Estado del médico en el sistema:
     * 1 = Activo (puede recibir citas y usar el sistema)
     * 0 = Inactivo (no puede recibir nuevas citas)
     * 
     * IMPORTANTE: Solo médicos activos aparecen en el formulario de agendamiento
     */
    @Column(nullable = false)
    private Integer estado = 1; // Por defecto los médicos se crean activos

    public Long getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(Long idMedico) {
        this.idMedico = idMedico;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getNumeroDoc() {
        return numeroDoc;
    }

    public void setNumeroDoc(String numeroDoc) {
        this.numeroDoc = numeroDoc;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }


    
}


