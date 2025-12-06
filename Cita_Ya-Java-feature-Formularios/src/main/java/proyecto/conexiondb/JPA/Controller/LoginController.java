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

@Controller
public class LoginController {

    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Autowired
    private MedicoRepository medicoRepository;
    
    @Autowired
    private AdministradorRepository administradorRepository;

    // Mostrar login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "auth/inicioSesion";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("numerodoc") String numeroDoc,
            @RequestParam("contrasena") String contrasena,
            Model model,
            HttpSession session) {

        // Validar que los campos no estén vacíos
        if (numeroDoc == null || numeroDoc.trim().isEmpty()) {
            model.addAttribute("error", "El número de documento es requerido.");
            return "auth/inicioSesion";
        }

        if (contrasena == null || contrasena.trim().isEmpty()) {
            model.addAttribute("error", "La contraseña es requerida.");
            return "auth/inicioSesion";
        }

        System.out.println("Intentando login con documento: " + numeroDoc);

        // Buscar en Administradores
        Administrador administrador = administradorRepository.findByNumeroDoc(numeroDoc);
        if (administrador != null) {
            System.out.println("Administrador encontrado: " + administrador.getNombre());
            System.out.println("Contraseña BD: [" + administrador.getContraseña() + "]");
            System.out.println("Contraseña ingresada: [" + contrasena + "]");
            
            if (administrador.getContraseña() != null && administrador.getContraseña().trim().equals(contrasena.trim())) {
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

        // Buscar en Médicos
        Medico medico = medicoRepository.findByNumeroDoc(numeroDoc);
        if (medico != null) {
            System.out.println("Médico encontrado: " + medico.getNombre());
            System.out.println("Contraseña BD: [" + medico.getContraseña() + "]");
            System.out.println("Contraseña ingresada: [" + contrasena + "]");
            
            if (medico.getContraseña() != null && medico.getContraseña().trim().equals(contrasena.trim())) {
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

        // Buscar en Pacientes
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
            
            if (paciente.getContrasena() != null && paciente.getContrasena().trim().equals(contrasena.trim())) {
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

        // Si no se encuentra en ninguna tabla
        System.out.println("Usuario no encontrado en ninguna tabla");
        model.addAttribute("error", "El usuario no existe.");
        return "auth/inicioSesion";
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
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
