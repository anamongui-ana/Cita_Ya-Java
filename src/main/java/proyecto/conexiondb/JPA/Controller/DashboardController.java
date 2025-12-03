package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    // Dashboard de Administrador
    @GetMapping("/administrador")
    public String dashboardAdministrador(HttpSession session, Model model) {
        // Verificar si hay sesión activa
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        // Verificar que sea administrador
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"administrador".equals(tipoUsuario)) {
            return "redirect:/login";
        }
        
        model.addAttribute("nombreCompleto", session.getAttribute("nombreCompleto"));
        return "Dashboard/administrador";
    }

    // Dashboard de Médico
    @GetMapping("/medico")
    public String dashboardMedico(HttpSession session, Model model) {
        // Verificar si hay sesión activa
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        // Verificar que sea médico
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"medico".equals(tipoUsuario)) {
            return "redirect:/login";
        }
        
        model.addAttribute("nombreCompleto", session.getAttribute("nombreCompleto"));
        return "Dashboard/medico";
    }

    // Dashboard de Paciente
    @GetMapping("/paciente")
    public String dashboardPaciente(HttpSession session, Model model) {
        // Verificar si hay sesión activa
        if (session.getAttribute("usuario") == null) {
            return "redirect:/login";
        }
        
        // Verificar que sea paciente
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        if (!"paciente".equals(tipoUsuario)) {
            return "redirect:/login";
        }
        
        model.addAttribute("nombreCompleto", session.getAttribute("nombreCompleto"));
        return "Dashboard/paciente";
    }
}
