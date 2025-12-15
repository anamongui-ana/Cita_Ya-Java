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

/**
  Gestiona todas las operaciones relacionadas con citas médicas
 * 
 Este controlador maneja:
  Listado de citas (filtrado por tipo de usuario)
  Creación de nuevas citas (con validaciones de disponibilidad)
  Edición y actualización de citas existentes
  Cancelación/eliminación de citas
  Verificación de disponibilidad de médicos
  Generación de reportes de agendamientos
ROLES Y PERMISOS
  Administradores Acceso total a todas las citas
  Médicos Solo sus propias citas + pueden crear citas para pacientes
  Pacientes:Solo sus propias citas
 */
@Controller
@RequestMapping("/agendamientos")
public class AgendamientoController {

    // === INYECCIÓN DE DEPENDENCIAS =====
    
    /**
     * Repositorio para operaciones CRUD de agendamientos
     */
    private final AgendamientoRepository agendamientoRepository;
    
    /**
     Repositorio para operaciones con pacientes
     */
    private final PacienteRepository pacienteRepository;
    
    /**
     * Repositorio para operaciones con médicos
     */
    private final proyecto.conexiondb.JPA.Repository.MedicoRepository medicoRepository;

    /**
     * Constructor con inyección de dependencias
     * Spring automáticamente inyecta los repositorios necesarios
     */
    public AgendamientoController(AgendamientoRepository agendamientoRepository,
            PacienteRepository pacienteRepository,
            proyecto.conexiondb.JPA.Repository.MedicoRepository medicoRepository) {
        this.agendamientoRepository = agendamientoRepository;
        this.pacienteRepository = pacienteRepository;
        this.medicoRepository = medicoRepository;
    }

    // ==== LISTADO DE AGENDAMIENTOS =====
    
    /**
      
      Lista los agendamientos según el tipo de usuario logueado
    Administradores: Ven TODAS las citas del sistema
    Pacientes: Solo ven SUS propias citas
    Médicos: Solo ven las citas asignadas a ellos
      
     Verifica que el usuario esté logueado antes de mostrar datos
     */
    @GetMapping
    public String listar(Model model, HttpSession session) {
        // Obtener información del usuario desde la sesión
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        // Redirigir al login si no hay usuario logueado
        if (usuario == null) {
            return "redirect:/login";
        }
        
        List<Agendamiento> agendamientos;
        
        // FILTRADO POR TIPO DE USUARIO
        
        // ADMINISTRADORES: Acceso completo a todas las citas del sistema
        if ("administrador".equals(tipoUsuario)) {
            agendamientos = agendamientoRepository.findAll();
        } 
        // Si es paciente, mostrar solo sus citas
        else if ("paciente".equals(tipoUsuario)) {
            Paciente paciente = (Paciente) usuario;
            agendamientos = agendamientoRepository.findByPaciente(paciente);
        }
        // Si es médico, mostrar solo sus citas
        else if ("medico".equals(tipoUsuario)) {
            proyecto.conexiondb.JPA.Entity.Medico medico = (proyecto.conexiondb.JPA.Entity.Medico) usuario;
            agendamientos = agendamientoRepository.findByMedico(medico);
        }
        else {
            return "redirect:/login";
        }
        
        model.addAttribute("agendamientos", agendamientos);
        model.addAttribute("tipoUsuario", tipoUsuario);
        return "agendamientos/index";
    }

    @GetMapping("/create")
    public String create(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo administradores pueden crear citas desde esta ruta
        if (!"administrador".equals(tipoUsuario)) {
            return "redirect:/agendamientos";
        }
        
        try {
            // Obtener todos los pacientes y médicos para el formulario
            model.addAttribute("pacientes", pacienteRepository.findAll());
            model.addAttribute("medicos", medicoRepository.findAll());
            model.addAttribute("agendamiento", new Agendamiento());
            model.addAttribute("fechaMinima", LocalDate.now().plusDays(1).toString());
        } catch (Exception e) {
            model.addAttribute("agendamiento", new Agendamiento());
            model.addAttribute("fechaMinima", LocalDate.now().plusDays(1).toString());
        }
        
        return "agendamientos/create";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        // Permitir acceso a pacientes y médicos
        if (usuario == null || (!("paciente".equals(tipoUsuario) || "medico".equals(tipoUsuario)))) {
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
            model.addAttribute("tipoUsuario", tipoUsuario);
            
            // Si es médico, agregar lista de todos los pacientes para búsqueda
            if ("medico".equals(tipoUsuario)) {
                model.addAttribute("pacientes", pacienteRepository.findAll());
            }
        } catch (Exception e) {
            // Si hay error al cargar citas, continuar sin mostrarlas
            model.addAttribute("agendamiento", new Agendamiento());
            model.addAttribute("citasPorFecha", new java.util.HashMap<>());
            model.addAttribute("fechaMinima", LocalDate.now().plusDays(1).toString());
            model.addAttribute("tipoUsuario", tipoUsuario);
            
            if ("medico".equals(tipoUsuario)) {
                model.addAttribute("pacientes", pacienteRepository.findAll());
            }
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

    @PostMapping("/store")
    public String store(@RequestParam("cita") String tipoCita,
                       @RequestParam("fecha") String fechaStr,
                       @RequestParam("hora") String hora,
                       @RequestParam("idMedico") Long idMedico,
                       @RequestParam("idPaciente") Long idPaciente,
                       HttpSession session,
                       RedirectAttributes redirectAttrs) {
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        // Solo administradores pueden crear citas desde esta ruta
        if (!"administrador".equals(tipoUsuario)) {
            return "redirect:/agendamientos";
        }
        
        try {
            // Convertir la fecha de String a java.sql.Date
            LocalDate fechaLocal = LocalDate.parse(fechaStr);
            Date fecha = Date.valueOf(fechaLocal);
            
            // Validar que la fecha no sea hoy ni en el pasado
            LocalDate manana = LocalDate.now().plusDays(1);
            
            if (fechaLocal.isBefore(manana)) {
                redirectAttrs.addFlashAttribute("error", "No se pueden agendar citas para hoy o fechas pasadas. Debe agendar con al menos 1 día de anticipación.");
                return "redirect:/agendamientos/create";
            }
            
            // Verificar disponibilidad del médico específico (SOLO 1 CITA POR HORA)
            long citasDelMedico = agendamientoRepository.countByMedicoAndFechaAndHora(idMedico, fecha, hora);
            
            if (citasDelMedico >= 1) {
                redirectAttrs.addFlashAttribute("error", "Lo sentimos, este médico ya tiene una cita agendada para esa fecha y hora. Por favor seleccione otro horario o médico.");
                return "redirect:/agendamientos/create";
            }
            
            // Buscar el médico y paciente
            proyecto.conexiondb.JPA.Entity.Medico medico = medicoRepository.findById(idMedico)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
            
            Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
            
            // Crear el agendamiento
            Agendamiento agendamiento = new Agendamiento();
            agendamiento.setCita(tipoCita);
            agendamiento.setFecha(fecha);
            agendamiento.setHora(hora);
            agendamiento.setPaciente(paciente);
            agendamiento.setMedico(medico);
            agendamiento.setEstado("Programada"); // Estado inicial
            
            agendamientoRepository.save(agendamiento);
            
            redirectAttrs.addFlashAttribute("success", "Cita creada exitosamente para " + paciente.getNombre() + " " + paciente.getApellido() + " con " + medico.getNombre() + " " + medico.getApellido());
            return "redirect:/agendamientos";
            
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("error", "Error al crear la cita. Por favor intente nuevamente.");
            return "redirect:/agendamientos/create";
        }
    }

    @PostMapping("/guardar")
    public String guardar(@RequestParam("cita") String tipoCita,
                         @RequestParam("fecha") String fechaStr,
                         @RequestParam("hora") String hora,
                         @RequestParam("idMedico") Long idMedico,
                         @RequestParam(value = "idPaciente", required = false) Long idPaciente,
                         HttpSession session,
                         RedirectAttributes redirectAttrs) {
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        // Verificar que sea un paciente o médico
        if (usuario == null || (!("paciente".equals(tipoUsuario) || "medico".equals(tipoUsuario)))) {
            return "redirect:/login";
        }
        
        Paciente paciente = null;
        
        // Si es paciente, usar el paciente de la sesión
        if ("paciente".equals(tipoUsuario)) {
            paciente = (Paciente) usuario;
        }
        // Si es médico, debe proporcionar el idPaciente
        else if ("medico".equals(tipoUsuario)) {
            if (idPaciente == null) {
                redirectAttrs.addFlashAttribute("error", "Debe seleccionar un paciente para agendar la cita.");
                return "redirect:/agendamientos/nuevo";
            }
            paciente = pacienteRepository.findById(idPaciente)
                .orElse(null);
            if (paciente == null) {
                redirectAttrs.addFlashAttribute("error", "Paciente no encontrado.");
                return "redirect:/agendamientos/nuevo";
            }
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
            agendamiento.setEstado("Programada"); // Estado inicial
            
            agendamientoRepository.save(agendamiento);
            
            redirectAttrs.addFlashAttribute("success", "Cita agendada exitosamente para " + paciente.getNombre() + " " + paciente.getApellido() + " con " + medico.getNombre() + " " + medico.getApellido());
            
            // Redirigir según el tipo de usuario
            if ("paciente".equals(tipoUsuario)) {
                return "redirect:/Layouts/paciente";
            } else {
                return "redirect:/medico/dashboard/citas";
            }
            
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
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        Agendamiento agendamiento = agendamientoRepository.findById(id)
            .orElse(null);
            
        if (agendamiento == null) {
            redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
            return "redirect:/agendamientos";
        }
        
        // Verificar permisos
        if ("paciente".equals(tipoUsuario)) {
            Paciente paciente = (Paciente) usuario;
            if (!agendamiento.getPaciente().getIdPaciente().equals(paciente.getIdPaciente())) {
                redirectAttrs.addFlashAttribute("error", "No tiene permiso para editar esta cita");
                return "redirect:/agendamientos";
            }
        }
        // Los administradores pueden editar cualquier cita
        
        try {
            // Obtener todas las citas con médico asignado
            List<Agendamiento> todasLasCitas = agendamientoRepository.findAllWithMedico();
            
            Map<Date, List<Agendamiento>> citasPorFecha = todasLasCitas.stream()
                .collect(Collectors.groupingBy(Agendamiento::getFecha));
            
            model.addAttribute("agendamiento", agendamiento);
            model.addAttribute("citasPorFecha", citasPorFecha);
            model.addAttribute("fechaMinima", LocalDate.now().plusDays(1).toString());
            
            // Si es administrador, agregar lista de pacientes y médicos
            if ("administrador".equals(tipoUsuario)) {
                model.addAttribute("pacientes", pacienteRepository.findAll());
                model.addAttribute("medicos", medicoRepository.findAll());
            }
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
                            @RequestParam(value = "idPaciente", required = false) Long idPaciente,
                            HttpSession session,
                            RedirectAttributes redirectAttrs) {
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        try {
            Agendamiento agendamiento = agendamientoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));
            
            // Verificar permisos
            if ("paciente".equals(tipoUsuario)) {
                Paciente paciente = (Paciente) usuario;
                if (!agendamiento.getPaciente().getIdPaciente().equals(paciente.getIdPaciente())) {
                    redirectAttrs.addFlashAttribute("error", "No tiene permiso para editar esta cita");
                    return "redirect:/agendamientos";
                }
            }
            // Los administradores pueden editar cualquier cita
            
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
            
            // Si es administrador y se proporciona idPaciente, actualizar el paciente también
            if ("administrador".equals(tipoUsuario) && idPaciente != null) {
                Paciente nuevoPaciente = pacienteRepository.findById(idPaciente)
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
                agendamiento.setPaciente(nuevoPaciente);
            }
            
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
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        if (usuario == null) {
            return "redirect:/login";
        }
        
        agendamientoRepository.findById(id).ifPresent(agendamiento -> {
            // Si es administrador, puede eliminar cualquier cita
            if ("administrador".equals(tipoUsuario)) {
                agendamientoRepository.deleteById(id);
                redirectAttrs.addFlashAttribute("success", "Cita eliminada exitosamente");
            }
            // Si es paciente, solo puede cancelar sus propias citas
            else if ("paciente".equals(tipoUsuario)) {
                Paciente paciente = (Paciente) usuario;
                if (agendamiento.getPaciente().getIdPaciente().equals(paciente.getIdPaciente())) {
                    agendamientoRepository.deleteById(id);
                    redirectAttrs.addFlashAttribute("success", "Cita cancelada exitosamente");
                } else {
                    redirectAttrs.addFlashAttribute("error", "No tiene permiso para cancelar esta cita");
                }
            }
            else {
                redirectAttrs.addFlashAttribute("error", "No tiene permisos para realizar esta acción");
            }
        });
        
        // Redirigir según el tipo de usuario
        if ("paciente".equals(tipoUsuario)) {
            return "redirect:/Layouts/paciente";
        } else if ("medico".equals(tipoUsuario)) {
            return "redirect:/medico/dashboard/citas";
        } else {
            return "redirect:/agendamientos";
        }
    }
    
    @GetMapping("/{id}")
    public String ver(@PathVariable("id") Long id, Model model, HttpSession session, RedirectAttributes redirectAttrs) {
        Object usuario = session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/login";
        }
        
        Agendamiento agendamiento = agendamientoRepository.findById(id).orElse(null);
        if (agendamiento == null) {
            redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
            return "redirect:/agendamientos";
        }
        
        model.addAttribute("agendamiento", agendamiento);
        return "agendamientos/show";
    }
    
    @GetMapping("/reporte")
    public String reporte(Model model) {
        model.addAttribute("agendamientos", agendamientoRepository.findAll());
        return "agendamientos/reporte";
    }
}