package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto.conexiondb.JPA.Entity.Historia_Clinica;
import proyecto.conexiondb.JPA.Entity.Paciente;
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
        return "historiales/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("historia", new Historia_Clinica());
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "historiales/create";
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam("paciente") Long idPaciente,
                         @RequestParam("medico") Long idMedico,
                         @RequestParam("fechaCreacion") String fechaCreacionStr,
                         @RequestParam("fechaAtencion") String fechaAtencionStr,
                         @RequestParam(value = "antecedentes", required = false) String antecedentes,
                         @RequestParam("diagnostico") String diagnostico,
                         @RequestParam(value = "tratamiento", required = false) String tratamiento,
                         @RequestParam(value = "observaciones", required = false) String observaciones,
                         RedirectAttributes redirectAttrs) {
        try {
            Historia_Clinica historia = new Historia_Clinica();
            
            // Buscar paciente y médico
            Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            proyecto.conexiondb.JPA.Entity.Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
            
            // Convertir fechas
            java.sql.Date fechaCreacion = java.sql.Date.valueOf(fechaCreacionStr);
            java.sql.Date fechaAtencion = java.sql.Date.valueOf(fechaAtencionStr);
            
            // Asignar valores
            historia.setPaciente(paciente);
            historia.setMedico(medico);
            historia.setFechaCreacion(fechaCreacion);
            historia.setFechaAtencion(fechaAtencion);
            historia.setAntecedentes(antecedentes);
            historia.setDiagnostico(diagnostico);
            historia.setTratamiento(tratamiento);
            historia.setObservaciones(observaciones);
            
            historiaRepository.save(historia);
            redirectAttrs.addFlashAttribute("success", "Historial clínico guardado exitosamente");
            return "redirect:/historiales";
            
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al guardar el historial: " + e.getMessage());
            return "redirect:/historiales/nuevo";
        }
    }
    
    @GetMapping("/{id}")
    public String ver(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Historia_Clinica historia = historiaRepository.findById(id).orElse(null);
        if (historia == null) {
            redirectAttrs.addFlashAttribute("error", "Historial no encontrado");
            return "redirect:/historiales";
        }
        model.addAttribute("historia", historia);
        return "historiales/show";
    }
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Historia_Clinica historia = historiaRepository.findById(id).orElse(null);
        if (historia == null) {
            redirectAttrs.addFlashAttribute("error", "Historial no encontrado");
            return "redirect:/historiales";
        }
        model.addAttribute("historia", historia);
        model.addAttribute("pacientes", pacienteRepository.findAll());
        model.addAttribute("medicos", medicoRepository.findAll());
        return "historiales/edit";
    }
    
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable("id") Long id,
                            @RequestParam("paciente") Long idPaciente,
                            @RequestParam("medico") Long idMedico,
                            @RequestParam("fechaCreacion") String fechaCreacionStr,
                            @RequestParam("fechaAtencion") String fechaAtencionStr,
                            @RequestParam(value = "antecedentes", required = false) String antecedentes,
                            @RequestParam("diagnostico") String diagnostico,
                            @RequestParam(value = "tratamiento", required = false) String tratamiento,
                            @RequestParam(value = "observaciones", required = false) String observaciones,
                            RedirectAttributes redirectAttrs) {
        try {
            Historia_Clinica historia = historiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Historial no encontrado"));
            
            // Buscar paciente y médico
            Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            proyecto.conexiondb.JPA.Entity.Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
            
            // Convertir fechas
            java.sql.Date fechaCreacion = java.sql.Date.valueOf(fechaCreacionStr);
            java.sql.Date fechaAtencion = java.sql.Date.valueOf(fechaAtencionStr);
            
            // Actualizar valores
            historia.setPaciente(paciente);
            historia.setMedico(medico);
            historia.setFechaCreacion(fechaCreacion);
            historia.setFechaAtencion(fechaAtencion);
            historia.setAntecedentes(antecedentes);
            historia.setDiagnostico(diagnostico);
            historia.setTratamiento(tratamiento);
            historia.setObservaciones(observaciones);
            
            historiaRepository.save(historia);
            redirectAttrs.addFlashAttribute("success", "Historial actualizado exitosamente");
            return "redirect:/historiales";
            
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al actualizar el historial: " + e.getMessage());
            return "redirect:/historiales/editar/" + id;
        }
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        try {
            historiaRepository.deleteById(id);
            redirectAttrs.addFlashAttribute("success", "Historial eliminado exitosamente");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al eliminar el historial: " + e.getMessage());
        }
        return "redirect:/historiales";
    }
    
    @GetMapping("/reporte")
    public String reporte(Model model) {
        model.addAttribute("historias", historiaRepository.findAll());
        return "historiales/reporte";
    }
}
