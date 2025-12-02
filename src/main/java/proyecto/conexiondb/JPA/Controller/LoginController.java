package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final PacienteRepository pacienteRepository;

    public LoginController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    // Mostrar login
    @GetMapping("/login")
    public String mostrarLogin() {
        return "login"; // login.html
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("numero_doc") String numeroDoc,
            @RequestParam("contrasena") String contrasena,
            Model model,
            HttpSession session) {

        // Buscar por documento
        Paciente paciente = pacienteRepository.findByNumeroDoc(numeroDoc);

        if (paciente == null) {
            model.addAttribute("error", "El usuario no existe.");
            return "login";
        }

        // Verificar contraseña
        if (!paciente.getContrasena().equals(contrasena)) {
            model.addAttribute("error", "Contraseña incorrecta.");
            return "login";
        }

        // Guardar sesión
        session.setAttribute("usuario", paciente);

        // Redirigir al dashboard o inicio
        return "redirect:/index";
    }

    // Cerrar sesión
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
