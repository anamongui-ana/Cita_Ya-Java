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
        return "auth/inicioSesion";
    }

    // Procesar login
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("numerodoc") String numeroDoc,
            @RequestParam("contrasena") String contrasena,
            Model model,
            HttpSession session) {

        // Validar documento (6-10 dígitos)
        if (numeroDoc == null || !numeroDoc.matches("\\d{6,10}")) {
            model.addAttribute("error", "El número de documento debe tener entre 6 y 10 dígitos.");
            return "auth/inicioSesion";
        }

        // Validar contraseña (mínimo 8 caracteres, mayúsculas, minúsculas, números y caracteres especiales)
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&.#-])[A-Za-z\\d@$!%*?&.#-]{8,}$";
        if (contrasena == null || !contrasena.matches(passwordRegex)) {
            model.addAttribute("error", "La contraseña debe tener mínimo 8 caracteres con mayúsculas, minúsculas, números y caracteres especiales.");
            return "auth/inicioSesion";
        }

        // Buscar por documento
        Paciente paciente = pacienteRepository.findByNumeroDoc(numeroDoc);

        if (paciente == null) {
            model.addAttribute("error", "El usuario no existe.");
            return "auth/inicioSesion";
        }

        // Verificar contraseña
        if (!paciente.getContrasena().equals(contrasena)) {
            model.addAttribute("error", "Contraseña incorrecta.");
            return "auth/inicioSesion";
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
