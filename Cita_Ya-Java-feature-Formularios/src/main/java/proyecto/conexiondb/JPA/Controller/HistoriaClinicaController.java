package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import proyecto.conexiondb.JPA.Entity.Historia_Clinica;
import proyecto.conexiondb.JPA.Repository.HistoriaClinicaRepository;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;
import proyecto.conexiondb.JPA.Repository.MedicoRepository;

@Controller
@RequestMapping("/historiales")
public class HistoriaClinicaController {

    private final HistoriaClinicaRepository historiaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public HistoriaClinicaController(HistoriaClinicaRepository historiaRepository,
            PacienteRepository pacienteRepository,
            MedicoRepository medicoRepository) {
        this.historiaRepository = historiaRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("historias", historiaRepository.findAll());
        return "historia/formulario";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("historia", new Historia_Clinica());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "historia/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Historia_Clinica historia) {
        historiaRepository.save(historia);
        return "redirect:/historiales";
    }
}
