package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import proyecto.conexiondb.JPA.Entity.Medico;
import proyecto.conexiondb.JPA.Repository.MedicoRepository;

@Controller
@RequestMapping("/medico")
public class MedicoController {

    private final MedicoRepository medicoRepository;

    public MedicoController(MedicoRepository medicoRepository) {
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicos", medicoRepository.findAll());
        return "medicos/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("medico", new Medico());
        return "medico/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Medico medico) {
        medicoRepository.save(medico);
        return "redirect:/medico";
    }
}
