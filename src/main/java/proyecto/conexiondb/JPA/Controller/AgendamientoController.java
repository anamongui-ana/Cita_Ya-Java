package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import proyecto.conexiondb.JPA.Entity.Agendamiento;
import proyecto.conexiondb.JPA.Repository.AgendamientoRepository;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

@Controller
@RequestMapping("/agendamiento")
public class AgendamientoController {

    private final AgendamientoRepository agendamientoRepository;
    private final PacienteRepository pacienteRepository;

    public AgendamientoController(AgendamientoRepository agendamientoRepository,
            PacienteRepository pacienteRepository) {
        this.agendamientoRepository = agendamientoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("agendamientos", agendamientoRepository.findAll());
        return "agendamiento/formulario";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("agendamiento", new Agendamiento());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "agendamiento/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Agendamiento agendamiento) {
        agendamientoRepository.save(agendamiento);
        return "redirect:/agendamiento";
    }
}
