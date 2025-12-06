package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import proyecto.conexiondb.JPA.Entity.Agendamiento;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.AgendamientoRepository;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/agendamientos")
public class AgendamientoController {

    private final AgendamientoRepository agendamientoRepository;
    private final PacienteRepository pacienteRepository;
    private final proyecto.conexiondb.JPA.Repository.MedicoRepository medicoRepository;

    public AgendamientoController(AgendamientoRepository agendamientoRepository,
            PacienteRepository pacienteRepository,
            proyecto.conexiondb.JPA.Repository.MedicoRepository medicoRepository) {
        this.agendamientoRepository = agendamientoRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        List<Agendamiento> agendamientos = agendamientoRepository.findByPaciente(paciente);
        model.addAttribute("agendamientos", agendamientos);
        return "agendamientos/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        try {
            // Obtener solo las citas que tienen médico asignado
            List<Agendamiento> todasLasCitas = agendamientoRepository.findAllWithMedico();
                
            Map<Date, List<Agendamiento>> citasPorFecha = todasLasCitas.stream()
                .collect(Collectors.groupingBy(Agendamiento::getFecha));
            
            model.addAttribute("agendamiento", new Agendamiento());
            model.addAttribute("citasPorFecha", citasPorFecha);
            model.addAttribute("fechaMinima", LocalDate.now().plusDays(1).toString());
        } catch (Exception e) {
            // Si hay error al cargar citas, continuar sin mostrarlas
            model.addAttribute("agendamiento", new Agendamiento());
            model.addAttribute("citasPorFecha", new java.util.HashMap<>());
            model.addAttribute("fechaMinima", LocalDate.now().plusDays(1).toString());
        }
        
        return "agendamientos/create";
    }
    
    @GetMapping("/medicos-por-especialidad")
    @ResponseBody
    public List<Map<String, Object>> getMedicosPorEspecialidad(@RequestParam("especialidad") String especialidad,
                                                                 @RequestParam("fecha") String fechaStr,
                                                                 @RequestParam("hora") String hora) {
        try {
            LocalDate fechaLocal = LocalDate.parse(fechaStr);
            Date fecha = Date.valueOf(fechaLocal);
            
            List<proyecto.conexiondb.JPA.Entity.Medico> medicos = 
                medicoRepository.findByEspecialidadAndEstado(especialidad, 1);
            
            List<Map<String, Object>> medicosDisponibilidad = new java.util.ArrayList<>();
            
            for (proyecto.conexiondb.JPA.Entity.Medico medico : medicos) {
                long citasAgendadas = agendamientoRepository.countByMedicoAndFechaAndHora(
                    medico.getIdMedico(), fecha, hora
                );
                
                Map<String, Object> medicoInfo = new java.util.HashMap<>();
                medicoInfo.put("id", medico.getIdMedico());
                medicoInfo.put("nombre", medico.getNombre() + " " + medico.getApellido());
                medicoInfo.put("disponible", citasAgendadas == 0); // Solo disponible si NO tiene citas
                medicoInfo.put("citasAgendadas", citasAgendadas);
                
                medicosDisponibilidad.add(medicoInfo);
            }
            
            return medicosDisponibilidad;
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam("cita") String tipoCita,
                         @RequestParam("fecha") String fechaStr,
                         @RequestParam("hora") String hora,
                         @RequestParam("idMedico") Long idMedico,
                         HttpSession session,
                         RedirectAttributes redirectAttrs) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        try {
            // Convertir la fecha de String a java.sql.Date
            LocalDate fechaLocal = LocalDate.parse(fechaStr);
            Date fecha = Date.valueOf(fechaLocal);
            
            // Validar que la fecha no sea hoy ni en el pasado
            LocalDate manana = LocalDate.now().plusDays(1);
            
            if (fechaLocal.isBefore(manana)) {
                redirectAttrs.addFlashAttribute("error", "No se pueden agendar citas para hoy o fechas pasadas. Debe agendar con al menos 1 día de anticipación.");
                return "redirect:/agendamientos/nuevo";
            }
            
            // Verificar disponibilidad del médico específico (SOLO 1 CITA POR HORA)
            long citasDelMedico = agendamientoRepository.countByMedicoAndFechaAndHora(idMedico, fecha, hora);
            
            if (citasDelMedico >= 1) {
                redirectAttrs.addFlashAttribute("error", "Lo sentimos, este médico ya tiene una cita agendada para esa fecha y hora. Por favor seleccione otro horario o médico.");
                return "redirect:/agendamientos/nuevo";
            }
            
            // Buscar el médico
            proyecto.conexiondb.JPA.Entity.Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
            
            // Crear el agendamiento
            Agendamiento agendamiento = new Agendamiento();
            agendamiento.setCita(tipoCita);
            agendamiento.setFecha(fecha);
            agendamiento.setHora(hora);
            agendamiento.setPaciente(paciente);
            agendamiento.setMedico(medico);
            
            agendamientoRepository.save(agendamiento);
            
            redirectAttrs.addFlashAttribute("success", "Cita agendada exitosamente con " + medico.getNombre() + " " + medico.getApellido());
            return "redirect:/Layouts/paciente";
            
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al agendar la cita. Por favor intente nuevamente.");
            return "redirect:/agendamientos/nuevo";
        }
    }
    
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") Long id, 
                        Model model, 
                        HttpSession session,
                        RedirectAttributes redirectAttrs) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        Agendamiento agendamiento = agendamientoRepository.findById(id)
            .orElse(null);
            
        if (agendamiento == null) {
            redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
            return "redirect:/agendamientos";
        }
        
        if (!agendamiento.getPaciente().getIdPaciente().equals(paciente.getIdPaciente())) {
            redirectAttrs.addFlashAttribute("error", "No tiene permiso para editar esta cita");
            return "redirect:/agendamientos";
        }
        
        try {
            // Obtener todas las citas con médico asignado
            List<Agendamiento> todasLasCitas = agendamientoRepository.findAllWithMedico();
            
            Map<Date, List<Agendamiento>> citasPorFecha = todasLasCitas.stream()
                .collect(Collectors.groupingBy(Agendamiento::getFecha));
            
            model.addAttribute("agendamiento", agendamiento);
            model.addAttribute("citasPorFecha", citasPorFecha);
            model.addAttribute("fechaMinima", LocalDate.now().plusDays(1).toString());
        } catch (Exception e) {
            model.addAttribute("agendamiento", agendamiento);
            model.addAttribute("citasPorFecha", new java.util.HashMap<>());
            model.addAttribute("fechaMinima", LocalDate.now().plusDays(1).toString());
        }
        
        return "agendamientos/edit";
    }
    
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable("id") Long id,
                            @RequestParam("cita") String tipoCita,
                            @RequestParam("fecha") String fechaStr,
                            @RequestParam("hora") String hora,
                            @RequestParam("idMedico") Long idMedico,
                            HttpSession session,
                            RedirectAttributes redirectAttrs) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        try {
            Agendamiento agendamiento = agendamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            if (!agendamiento.getPaciente().getIdPaciente().equals(paciente.getIdPaciente())) {
                redirectAttrs.addFlashAttribute("error", "No tiene permiso para editar esta cita");
                return "redirect:/agendamientos";
            }
            
            // Convertir la fecha
            LocalDate fechaLocal = LocalDate.parse(fechaStr);
            Date fecha = Date.valueOf(fechaLocal);
            
            // Validar que la fecha no sea hoy ni en el pasado
            LocalDate manana = LocalDate.now().plusDays(1);
            
            if (fechaLocal.isBefore(manana)) {
                redirectAttrs.addFlashAttribute("error", "No se pueden agendar citas para hoy o fechas pasadas. Debe agendar con al menos 1 día de anticipación.");
                return "redirect:/agendamientos/editar/" + id;
            }
            
            // Verificar disponibilidad del médico (excluyendo la cita actual)
            long citasDelMedico = agendamientoRepository.countByMedicoAndFechaAndHora(idMedico, fecha, hora);
            
            // Si hay citas y no es la misma cita que estamos editando
            if (citasDelMedico >= 1) {
                // Verificar si la cita existente es la misma que estamos editando
                boolean esMismaCita = agendamiento.getMedico().getIdMedico().equals(idMedico) 
                    && agendamiento.getFecha().equals(fecha) 
                    && agendamiento.getHora().equals(hora);
                
                if (!esMismaCita) {
                    redirectAttrs.addFlashAttribute("error", "Lo sentimos, este médico ya tiene una cita agendada para esa fecha y hora. Por favor seleccione otro horario o médico.");
                    return "redirect:/agendamientos/editar/" + id;
                }
            }
            
            // Buscar el médico
            proyecto.conexiondb.JPA.Entity.Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
            
            // Actualizar el agendamiento
            agendamiento.setCita(tipoCita);
            agendamiento.setFecha(fecha);
            agendamiento.setHora(hora);
            agendamiento.setMedico(medico);
            
            agendamientoRepository.save(agendamiento);
            
            redirectAttrs.addFlashAttribute("success", "Cita actualizada exitosamente");
            return "redirect:/agendamientos";
            
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al actualizar la cita. Por favor intente nuevamente.");
            return "redirect:/agendamientos/editar/" + id;
        }
    }
    
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") Long id, 
                          HttpSession session,
                          RedirectAttributes redirectAttrs) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        agendamientoRepository.findById(id).ifPresent(agendamiento -> {
            if (agendamiento.getPaciente().getIdPaciente().equals(paciente.getIdPaciente())) {
                agendamientoRepository.deleteById(id);
                redirectAttrs.addFlashAttribute("success", "Cita cancelada exitosamente");
            } else {
                redirectAttrs.addFlashAttribute("error", "No tiene permiso para cancelar esta cita");
            }
        });
        
        return "redirect:/agendamientos";
    }
}
