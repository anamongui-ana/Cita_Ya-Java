package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

@Controller
@RequestMapping("/paciente")
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    public PacienteController(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "pacientes/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "paciente/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Paciente paciente) {
        pacienteRepository.save(paciente);
        return "redirect:/paciente";
    }
}
