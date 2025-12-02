package proyecto.conexiondb.JPA.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
    public String registrarPaciente(Paciente paciente, Model model) {
        try {
            pacienteService.registrarPaciente(paciente);
            return "redirect:/login?registro=exitoso";
        } catch (Exception e) {
            model.addAttribute("error", "Error al registrar: " + e.getMessage());
            return "auth/registro";
        }
    }
}