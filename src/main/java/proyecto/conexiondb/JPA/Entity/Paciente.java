package proyecto.conexiondb.JPA.Entity;

// Importaciones necesarias para JPA y manejo de fechas
import jakarta.persistence.*;
import java.util.Date;
import java.io.Serializable;

/**
 * ENTIDAD PACIENTE - Representa a los usuarios pacientes del sistema
 * 
 * Esta clase mapea la tabla 'pacientes' en la base de datos y contiene
 * toda la información personal, médica y de contacto de los pacientes.
 * Los pacientes pueden agendar citas y tener historiales clínicos.
 */
@Entity
@Table(name = "pacientes")
public class Paciente implements Serializable {
    private static final long serialVersionUID = 1L; // Versión para serialización

    // =============== CAMPOS DE IDENTIFICACIÓN ===============
    
    /**
     * ID único del paciente - Clave primaria autoincremental
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Long idPaciente;

    /**
     * Tipo de documento de identidad (CC, TI, CE, PA, etc.)
     * Campo obligatorio con longitud máxima de 20 caracteres
     */
    @Column(name = "tipo_doc", length = 20, nullable = false)
    private String tipoDoc;

    /**
     * Número de documento de identidad
     * Campo obligatorio, único en el sistema (no puede haber duplicados)
     */
    @Column(name = "numero_doc", length = 30, nullable = false, unique = true)
    private String numeroDoc;

    // =============== INFORMACIÓN PERSONAL ===============
    
    /**
     * Nombre del paciente - Campo obligatorio
     */
    @Column(name = "nombre", length = 100, nullable = false)
    private String nombre;

    /**
     * Apellido del paciente - Campo obligatorio
     */
    @Column(name = "apellido", length = 100, nullable = false)
    private String apellido;

    /**
     * Género del paciente (M, F, Otro)
     */
    @Column(name = "genero", nullable = false)
    private String genero;

    /**
     * Fecha de nacimiento del paciente
     * Formato: yyyy-MM-dd para compatibilidad con formularios HTML
     */
    @Column(name = "fecha_nacimiento", nullable = false)
    @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date fechaNacimiento;

    // =============== INFORMACIÓN MÉDICA ===============
    
    /**
     * Tipo de sangre del paciente (O+, O-, A+, A-, B+, B-, AB+, AB-)
     * Campo opcional pero importante para emergencias médicas
     */
    @Column(name = "tipo_sangre", length = 5)
    private String tipoSangre;

    // =============== INFORMACIÓN DE CONTACTO ===============
    
    /**
     * Dirección de residencia del paciente - Campo opcional
     */
    @Column(length = 255)
    private String direccion;

    /**
     * Número de teléfono del paciente - Campo opcional
     */
    @Column(length = 20)
    private String telefono;

    /**
     * Correo electrónico del paciente
     * Campo obligatorio y único (usado para login y comunicaciones)
     */
    @Column(length = 255, nullable = false, unique = true)
    private String correo;

    // ======= SEGURIDAD Y ESTADO =======
    
    /**
     * Contraseña encriptada del paciente para acceso al sistema
     */
    @Column(name = "contraseña", length = 255, nullable = false)
    private String contrasena;

    /**
     * Estado del paciente en el sistema:
     * 0 = Activo (puede usar el sistema normalmente)
     * 1 = Inactivo (cuenta deshabilitada)
     */
    @Column(name = "estado", nullable = false)
    private int estado = 0; // Por defecto los pacientes se crean activos

    // =============== CONSTRUCTORES ===============

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
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

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

 
    // private LocalDateTime created_at;
    // private LocalDateTime updated_at;

    // RELACIÓN con agendamientos / historial se agrega después
}
