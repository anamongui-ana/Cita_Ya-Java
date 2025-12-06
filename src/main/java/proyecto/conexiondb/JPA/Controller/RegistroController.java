package proyecto.conexiondb.JPA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Service.PacienteService;

@Controller
public class RegistroController {

    @Autowired
    private PacienteService pacienteService;

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrarPaciente(@ModelAttribute Paciente paciente, Model model) {
        try {
            // Validación básica
            if (paciente.getFechaNacimiento() == null) {
                model.addAttribute("error", "La fecha de nacimiento es obligatoria");
                return "auth/registro";
            }

            // Registra el paciente (la contraseña se encripta automáticamente)
            pacienteService.registrarPaciente(paciente);
            
            return "redirect:/login?registro=exitoso";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar: " + e.getMessage());
            model.addAttribute("paciente", paciente);
            return "auth/registro";
        }
    }
}