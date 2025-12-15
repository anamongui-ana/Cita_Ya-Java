package proyecto.conexiondb.JPA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Entity.Medico;
import proyecto.conexiondb.JPA.Entity.Administrador;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;
import proyecto.conexiondb.JPA.Repository.MedicoRepository;
import proyecto.conexiondb.JPA.Repository.AdministradorRepository;

import jakarta.servlet.http.HttpSession;

/**
 * CONTROLADOR DE LOGIN - Maneja la autenticación de usuarios en el sistema
 * 
 * Este controlador gestiona:
 * - Mostrar el formulario de login
 * - Procesar credenciales de login (documento + contraseña)
 * - Autenticar usuarios de los 3 tipos: Pacientes, Médicos, Administradores
 * - Crear sesiones de usuario autenticado
 * - Cerrar sesión (logout)
 * - Endpoints de debug para desarrollo
 * 
 * FLUJO DE AUTENTICACIÓN:
 * 1. Usuario ingresa número de documento y contraseña
 * 2. Sistema busca en las 3 tablas (administradores, médicos, pacientes)
 * 3. Si encuentra coincidencia, valida la contraseña
 * 4. Si es correcta, crea sesión y redirige al dashboard correspondiente
 * 5. Si no, muestra mensaje de error
 * 
 * NOTA: Las contraseñas se almacenan en texto plano (no recomendado para producción)
 */
@Controller
public class LoginController {

    // =============== INYECCIÓN DE REPOSITORIOS ===============
    
    /**
     * Repositorio para acceder a la tabla de pacientes
     */
    @Autowired
    private PacienteRepository pacienteRepository;
    
    /**
     * Repositorio para acceder a la tabla de médicos
     */
    @Autowired
    private MedicoRepository medicoRepository;
    
    /**
     * Repositorio para acceder a la tabla de administradores
     */
    @Autowired
    private AdministradorRepository administradorRepository;

    // =============== MOSTRAR FORMULARIO DE LOGIN ===============
    
    /**
     * ENDPOINT: GET /login
     * 
     * Muestra la página de inicio de sesión
     * Accesible para usuarios no autenticados
     */
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/inicioSesion";
    }

    // =============== PROCESAR AUTENTICACIÓN ===============
    
    /**
     * ENDPOINT: POST /login
     * 
     * Procesa las credenciales de login y autentica al usuario
     * 
     * PROCESO DE AUTENTICACIÓN:
     * 1. Valida que los campos no estén vacíos
     * 2. Busca el usuario por número de documento en las 3 tablas
     * 3. Verifica la contraseña (comparación de texto plano)
     * 4. Si es correcta, crea la sesión y redirige al dashboard
     * 5. Si no, muestra mensaje de error
     * 
     * @param numeroDoc Número de documento del usuario
     * @param contrasena Contraseña en texto plano
     * @param model Modelo para pasar datos a la vista
     * @param session Sesión HTTP para almacenar datos del usuario autenticado
     * @return Redirección al dashboard correspondiente o vuelta al login con error
     */
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("numerodoc") String numeroDoc,
            @RequestParam("contrasena") String contrasena,
            Model model,
            HttpSession session) {

        // =============== VALIDACIONES DE ENTRADA ===============
        
        // Validar que el número de documento no esté vacío
        if (numeroDoc == null || numeroDoc.trim().isEmpty()) {
            model.addAttribute("error", "El número de documento es requerido.");
            return "auth/inicioSesion";
        }

        // Validar que la contraseña no esté vacía
        if (contrasena == null || contrasena.trim().isEmpty()) {
            model.addAttribute("error", "La contraseña es requerida.");
            return "auth/inicioSesion";
        }

        System.out.println("Intentando login con documento: " + numeroDoc);

        // =============== BÚSQUEDA EN TABLA ADMINISTRADORES ===============
        
        /**
         * PRIORIDAD 1: Buscar en administradores
         * Los administradores tienen acceso completo al sistema
         */
        Administrador administrador = administradorRepository.findByNumeroDoc(numeroDoc);
        if (administrador != null) {
            System.out.println("Administrador encontrado: " + administrador.getNombre());
            System.out.println("Contraseña BD: [" + administrador.getContraseña() + "]");
            System.out.println("Contraseña ingresada: [" + contrasena + "]");
            
            // Verificar contraseña (comparación de texto plano)
            if (administrador.getContraseña() != null && administrador.getContraseña().trim().equals(contrasena.trim())) {
                // CREAR SESIÓN DE ADMINISTRADOR
                session.setAttribute("usuario", administrador);
                session.setAttribute("tipoUsuario", "administrador");
                session.setAttribute("nombreCompleto", administrador.getNombre() + " " + administrador.getApellido());
                System.out.println("Login exitoso - Redirigiendo a layouts administrador");
                return "redirect:/layouts/administrador";
            } else {
                model.addAttribute("error", "Contraseña incorrecta.");
                return "auth/inicioSesion";
            }
        }

        // =============== BÚSQUEDA EN TABLA MÉDICOS ===============
        
        /**
         * PRIORIDAD 2: Buscar en médicos
         * Los médicos pueden gestionar sus citas y pacientes asignados
         */
        Medico medico = medicoRepository.findByNumeroDoc(numeroDoc);
        if (medico != null) {
            System.out.println("Médico encontrado: " + medico.getNombre());
            System.out.println("Contraseña BD: [" + medico.getContraseña() + "]");
            System.out.println("Contraseña ingresada: [" + contrasena + "]");
            
            // Verificar contraseña (comparación de texto plano)
            if (medico.getContraseña() != null && medico.getContraseña().trim().equals(contrasena.trim())) {
                // CREAR SESIÓN DE MÉDICO
                session.setAttribute("usuario", medico);
                session.setAttribute("tipoUsuario", "medico");
                session.setAttribute("nombreCompleto", medico.getNombre() + " " + medico.getApellido());
                System.out.println("Login exitoso - Redirigiendo a layouts médico");
                return "redirect:/layouts/medico";
            } else {
                model.addAttribute("error", "Contraseña incorrecta.");
                return "auth/inicioSesion";
            }
        }

        // =============== BÚSQUEDA EN TABLA PACIENTES ===============
        
        /**
         * PRIORIDAD 3: Buscar en pacientes
         * Los pacientes pueden agendar citas y ver su información personal
         */
        Paciente paciente = pacienteRepository.findByNumeroDoc(numeroDoc);
        if (paciente != null) {
            System.out.println("=== PACIENTE ENCONTRADO ===");
            System.out.println("Nombre: " + paciente.getNombre() + " " + paciente.getApellido());
            System.out.println("Contraseña BD: [" + paciente.getContrasena() + "]");
            System.out.println("Contraseña BD (length): " + (paciente.getContrasena() != null ? paciente.getContrasena().length() : "null"));
            System.out.println("Contraseña BD (trimmed): [" + (paciente.getContrasena() != null ? paciente.getContrasena().trim() : "null") + "]");
            System.out.println("Contraseña ingresada: [" + contrasena + "]");
            System.out.println("Contraseña ingresada (length): " + contrasena.length());
            System.out.println("Contraseña ingresada (trimmed): [" + contrasena.trim() + "]");
            System.out.println("¿Son iguales? " + (paciente.getContrasena() != null && paciente.getContrasena().trim().equals(contrasena.trim())));
            
            // Verificar contraseña (comparación de texto plano)
            if (paciente.getContrasena() != null && paciente.getContrasena().trim().equals(contrasena.trim())) {
                // CREAR SESIÓN DE PACIENTE
                session.setAttribute("usuario", paciente);
                session.setAttribute("tipoUsuario", "paciente");
                session.setAttribute("nombreCompleto", paciente.getNombre() + " " + paciente.getApellido());
                System.out.println("Login exitoso - Redirigiendo a layouts paciente");
                return "redirect:/layouts/paciente";
            } else {
                System.out.println("ERROR: Las contraseñas no coinciden");
                model.addAttribute("error", "Contraseña incorrecta.");
                return "auth/inicioSesion";
            }
        }

        // =============== USUARIO NO ENCONTRADO ===============
        
        // Si no se encuentra el documento en ninguna de las 3 tablas
        System.out.println("Usuario no encontrado en ninguna tabla");
        model.addAttribute("error", "El usuario no existe.");
        return "auth/inicioSesion";
    }

    // =============== CERRAR SESIÓN ===============
    
    /**
     * ENDPOINT: GET /logout
     * 
     * Cierra la sesión del usuario actual
     * Invalida completamente la sesión HTTP y redirige al login
     * 
     * @param session Sesión HTTP a invalidar
     * @return Redirección a la página de login
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Elimina todos los datos de la sesión
        return "redirect:/login";
    }
    
    // Endpoint de emergencia - login sin contraseña (SOLO PARA DEBUG)
    @GetMapping("/login-debug")
    public String loginDebug(@RequestParam("doc") String numeroDoc, HttpSession session, Model model) {
        // Buscar paciente
        Paciente paciente = pacienteRepository.findByNumeroDoc(numeroDoc);
        if (paciente != null) {
            session.setAttribute("usuario", paciente);
            session.setAttribute("tipoUsuario", "paciente");
            session.setAttribute("nombreCompleto", paciente.getNombre() + " " + paciente.getApellido());
            return "redirect:/layouts/paciente";
        }
        
        // Buscar médico
        Medico medico = medicoRepository.findByNumeroDoc(numeroDoc);
        if (medico != null) {
            session.setAttribute("usuario", medico);
            session.setAttribute("tipoUsuario", "medico");
            session.setAttribute("nombreCompleto", medico.getNombre() + " " + medico.getApellido());
            return "redirect:/layouts/medico";
        }
        
        model.addAttribute("error", "Usuario no encontrado");
        return "auth/inicioSesion";
    }
    
    // Endpoint de prueba para verificar datos
    @GetMapping("/test-login")
    @ResponseBody
    public String testLogin() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Test de Base de Datos</h1>");
        sb.append("<style>table {border-collapse: collapse;} th, td {border: 1px solid black; padding: 8px;}</style>");
        
        // Contar registros
        long countAdmin = administradorRepository.count();
        long countMedico = medicoRepository.count();
        long countPaciente = pacienteRepository.count();
        
        sb.append("<p><strong>Total Administradores:</strong> ").append(countAdmin).append("</p>");
        sb.append("<p><strong>Total Médicos:</strong> ").append(countMedico).append("</p>");
        sb.append("<p><strong>Total Pacientes:</strong> ").append(countPaciente).append("</p>");
        
        // Listar pacientes con detalles
        sb.append("<h2>Pacientes (primeros 5):</h2>");
        sb.append("<table><tr><th>ID</th><th>Documento</th><th>Nombre</th><th>Apellido</th><th>Contraseña</th><th>Longitud</th></tr>");
        pacienteRepository.findAll().stream().limit(5).forEach(pac -> {
            String pass = pac.getContrasena();
            sb.append("<tr>")
              .append("<td>").append(pac.getIdPaciente()).append("</td>")
              .append("<td>").append(pac.getNumeroDoc()).append("</td>")
              .append("<td>").append(pac.getNombre()).append("</td>")
              .append("<td>").append(pac.getApellido()).append("</td>")
              .append("<td>").append(pass).append("</td>")
              .append("<td>").append(pass != null ? pass.length() : "null").append("</td>")
              .append("</tr>");
        });
        sb.append("</table>");
        
        // Listar médicos con detalles
        sb.append("<h2>Médicos (primeros 5):</h2>");
        sb.append("<table><tr><th>ID</th><th>Documento</th><th>Nombre</th><th>Apellido</th><th>Contraseña</th><th>Longitud</th></tr>");
        medicoRepository.findAll().stream().limit(5).forEach(med -> {
            String pass = med.getContraseña();
            sb.append("<tr>")
              .append("<td>").append(med.getIdMedico()).append("</td>")
              .append("<td>").append(med.getNumeroDoc()).append("</td>")
              .append("<td>").append(med.getNombre()).append("</td>")
              .append("<td>").append(med.getApellido()).append("</td>")
              .append("<td>").append(pass).append("</td>")
              .append("<td>").append(pass != null ? pass.length() : "null").append("</td>")
              .append("</tr>");
        });
        sb.append("</table>");
        
        // Listar administradores
        sb.append("<h2>Administradores:</h2>");
        sb.append("<table><tr><th>ID</th><th>Documento</th><th>Nombre</th><th>Apellido</th><th>Contraseña</th><th>Longitud</th></tr>");
        administradorRepository.findAll().forEach(admin -> {
            String pass = admin.getContraseña();
            sb.append("<tr>")
              .append("<td>").append(admin.getId()).append("</td>")
              .append("<td>").append(admin.getNumeroDoc()).append("</td>")
              .append("<td>").append(admin.getNombre()).append("</td>")
              .append("<td>").append(admin.getApellido()).append("</td>")
              .append("<td>").append(pass).append("</td>")
              .append("<td>").append(pass != null ? pass.length() : "null").append("</td>")
              .append("</tr>");
        });
        sb.append("</table>");
        
        return sb.toString();
    }
}
