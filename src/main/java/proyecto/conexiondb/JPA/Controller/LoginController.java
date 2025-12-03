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
            
            if (administrador.getContraseña().equals(contrasena)) {
                session.setAttribute("usuario", administrador);
                session.setAttribute("tipoUsuario", "administrador");
                session.setAttribute("nombreCompleto", administrador.getNombre() + " " + administrador.getApellido());
                System.out.println("Login exitoso - Redirigiendo a dashboard administrador");
                return "redirect:/dashboard/administrador";
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
            
            if (medico.getContraseña().equals(contrasena)) {
                session.setAttribute("usuario", medico);
                session.setAttribute("tipoUsuario", "medico");
                session.setAttribute("nombreCompleto", medico.getNombre() + " " + medico.getApellido());
                System.out.println("Login exitoso - Redirigiendo a dashboard médico");
                return "redirect:/dashboard/medico";
            } else {
                model.addAttribute("error", "Contraseña incorrecta.");
                return "auth/inicioSesion";
            }
        }

        // Buscar en Pacientes
        Paciente paciente = pacienteRepository.findByNumeroDoc(numeroDoc);
        if (paciente != null) {
            System.out.println("Paciente encontrado: " + paciente.getNombre());
            System.out.println("Contraseña BD: [" + paciente.getContrasena() + "]");
            System.out.println("Contraseña ingresada: [" + contrasena + "]");
            
            if (paciente.getContrasena().equals(contrasena)) {
                session.setAttribute("usuario", paciente);
                session.setAttribute("tipoUsuario", "paciente");
                session.setAttribute("nombreCompleto", paciente.getNombre() + " " + paciente.getApellido());
                System.out.println("Login exitoso - Redirigiendo a dashboard paciente");
                return "redirect:/dashboard/paciente";
            } else {
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
    
    // Endpoint de prueba para verificar datos
    @GetMapping("/test-login")
    @ResponseBody
    public String testLogin() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h1>Test de Base de Datos</h1>");
        
        // Contar administradores
        long countAdmin = administradorRepository.count();
        sb.append("<p>Administradores en BD: ").append(countAdmin).append("</p>");
        
        // Contar médicos
        long countMedico = medicoRepository.count();
        sb.append("<p>Médicos en BD: ").append(countMedico).append("</p>");
        
        // Contar pacientes
        long countPaciente = pacienteRepository.count();
        sb.append("<p>Pacientes en BD: ").append(countPaciente).append("</p>");
        
        // Listar algunos administradores
        sb.append("<h2>Administradores:</h2>");
        administradorRepository.findAll().forEach(admin -> {
            sb.append("<p>Doc: ").append(admin.getNumeroDoc())
              .append(" - Nombre: ").append(admin.getNombre())
              .append(" - Pass: ").append(admin.getContraseña())
              .append("</p>");
        });
        
        return sb.toString();
    }
}
