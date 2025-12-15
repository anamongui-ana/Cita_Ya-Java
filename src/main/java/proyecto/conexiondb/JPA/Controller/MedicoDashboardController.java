package proyecto.conexiondb.JPA.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import proyecto.conexiondb.JPA.Entity.Agendamiento;
import proyecto.conexiondb.JPA.Entity.Medico;
import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.AgendamientoRepository;
import proyecto.conexiondb.JPA.Repository.MedicoRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CONTROLADOR DEL DASHBOARD MÉDICO - Gestiona todas las funcionalidades del panel médico
 * 
 * Este controlador maneja:
 * - Dashboard principal con estadísticas
 * - Gestión de citas (ver, filtrar, cambiar estados)
 * - Lista de pacientes atendidos
 * - Búsqueda de pacientes
 * - Visualización de historias clínicas
 * - Actualización automática de estados de citas
 * 
 * RUTAS PRINCIPALES:
 * - /medico/dashboard - Dashboard principal
 * - /medico/dashboard/citas - Gestión de citas
 * - /medico/dashboard/pacientes - Lista de pacientes
 * - /medico/dashboard/buscar-paciente - Búsqueda de pacientes
 * 
 * SEGURIDAD: Todos los endpoints verifican que el usuario sea un médico logueado
 */
@Controller
@RequestMapping("/medico/dashboard")
public class MedicoDashboardController {

    // =============== INYECCIÓN DE DEPENDENCIAS ===============
    
    /**
     * Repositorio para operaciones con agendamientos/citas
     */
    private final AgendamientoRepository agendamientoRepository;
    
    /**
     * Repositorio para operaciones con médicos
     */
    private final MedicoRepository medicoRepository;
    
    /**
     * Repositorio para operaciones con historias clínicas
     */
    private final proyecto.conexiondb.JPA.Repository.HistoriaClinicaRepository historiaClinicaRepository;
    
    /**
     * Repositorio para operaciones con pacientes
     */
    private final proyecto.conexiondb.JPA.Repository.PacienteRepository pacienteRepository;

    /**
     * Constructor con inyección de dependencias
     */
    public MedicoDashboardController(AgendamientoRepository agendamientoRepository,
                                     MedicoRepository medicoRepository,
                                     proyecto.conexiondb.JPA.Repository.HistoriaClinicaRepository historiaClinicaRepository,
                                     proyecto.conexiondb.JPA.Repository.PacienteRepository pacienteRepository) {
        this.agendamientoRepository = agendamientoRepository;
        this.medicoRepository = medicoRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
        this.pacienteRepository = pacienteRepository;
    }

    // =============== DASHBOARD PRINCIPAL ===============
    
    /**
     * ENDPOINT: GET /medico/dashboard
     * 
     * Muestra el dashboard principal del médico con:
     * - Estadísticas de citas (del día, del mes, total pacientes)
     * - Lista de próximas citas
     * - Accesos rápidos a funcionalidades principales
     * 
     * FUNCIONALIDADES AUTOMÁTICAS:
     * - Actualiza estados de citas automáticamente
     * - Calcula estadísticas en tiempo real
     * - Muestra solo citas futuras en "próximas citas"
     * 
     * @param session Sesión HTTP para obtener el médico logueado
     * @param model Modelo para pasar datos a la vista
     * @return Vista del dashboard médico
     */
    @GetMapping
    public String dashboard(HttpSession session, Model model) {
        // VALIDACIÓN DE SEGURIDAD: Verificar que hay un médico logueado
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        // ACTUALIZACIÓN AUTOMÁTICA: Actualizar estados de citas antes de mostrar estadísticas
        actualizarEstadosCitas(medico);

        // =============== CÁLCULO DE ESTADÍSTICAS ===============
        
        // Estadística 1: Citas programadas para HOY
        Date hoy = Date.valueOf(LocalDate.now());
        long citasDelDia = agendamientoRepository.countByMedicoAndFechaAndEstado(medico, hoy, "Programada");
        
        // Estadística 2: Total de citas del MES ACTUAL
        LocalDate ahora = LocalDate.now();
        long citasDelMes = agendamientoRepository.countByMedicoAndMesAndAnio(
            medico, ahora.getMonthValue(), ahora.getYear()
        );
        
        // Estadística 3: Citas completadas este mes (para mostrar productividad)
        long citasCompletadasMes = agendamientoRepository.countByMedicoAndMesAndAnioAndEstado(
            medico, ahora.getMonthValue(), ahora.getYear(), "Completada"
        );
        
        long citasNoAsistioMes = agendamientoRepository.countByMedicoAndMesAndAnioAndEstado(
            medico, ahora.getMonthValue(), ahora.getYear(), "No Asistió"
        );
        
        List<Paciente> pacientesUnicos = agendamientoRepository.findDistinctPacientesByMedico(medico);
        long totalPacientes = pacientesUnicos.size();

        // Calcular porcentaje de asistencia
        long citasTotalesMes = citasCompletadasMes + citasNoAsistioMes;
        double porcentajeAsistencia = citasTotalesMes > 0 ? 
            (citasCompletadasMes * 100.0 / citasTotalesMes) : 0;

        // Obtener próximas citas (limitadas a 5)
        List<Agendamiento> todasLasCitas = agendamientoRepository.findByMedicoOrderByFechaAscHoraAsc(medico);
        List<Agendamiento> proximasCitas = todasLasCitas.stream()
            .filter(cita -> !cita.getFecha().before(hoy) && 
                   ("Programada".equals(cita.getEstado()) || "En Curso".equals(cita.getEstado())))
            .limit(5)
            .collect(Collectors.toList());

        model.addAttribute("citasDelDia", citasDelDia);
        model.addAttribute("citasDelMes", citasDelMes);
        model.addAttribute("citasCompletadasMes", citasCompletadasMes);
        model.addAttribute("citasNoAsistioMes", citasNoAsistioMes);
        model.addAttribute("porcentajeAsistencia", String.format("%.1f", porcentajeAsistencia));
        model.addAttribute("totalPacientes", totalPacientes);
        model.addAttribute("proximasCitas", proximasCitas);
        model.addAttribute("medico", medico);

        return "medico/dashboard";
    }

    @GetMapping("/citas")
    public String misCitas(HttpSession session, Model model,
                          @RequestParam(required = false) String fecha,
                          @RequestParam(required = false) String buscar,
                          @RequestParam(required = false) String estado) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        // Actualizar estados antes de mostrar
        actualizarEstadosCitas(medico);

        List<Agendamiento> citas = agendamientoRepository.findByMedicoOrderByFechaAscHoraAsc(medico);

        // Filtrar por fecha si se proporciona
        if (fecha != null && !fecha.isEmpty()) {
            try {
                Date fechaFiltro = Date.valueOf(fecha);
                citas = citas.stream()
                    .filter(c -> c.getFecha().equals(fechaFiltro))
                    .collect(Collectors.toList());
            } catch (Exception e) {
                // Ignorar error de fecha inválida
            }
        }

        // Filtrar por estado si se proporciona
        if (estado != null && !estado.isEmpty() && !"Todas".equals(estado)) {
            citas = citas.stream()
                .filter(c -> estado.equals(c.getEstado()))
                .collect(Collectors.toList());
        }

        // Filtrar por nombre de paciente si se proporciona
        if (buscar != null && !buscar.isEmpty()) {
            String buscarLower = buscar.toLowerCase();
            citas = citas.stream()
                .filter(c -> {
                    String nombreCompleto = (c.getPaciente().getNombre() + " " + 
                                           c.getPaciente().getApellido()).toLowerCase();
                    return nombreCompleto.contains(buscarLower);
                })
                .collect(Collectors.toList());
        }

        model.addAttribute("citas", citas);
        model.addAttribute("fecha", fecha);
        model.addAttribute("buscar", buscar);
        model.addAttribute("estado", estado);
        model.addAttribute("medico", medico);

        return "medico/citas";
    }

    @GetMapping("/citas/{id}")
    public String verCita(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        Agendamiento cita = agendamientoRepository.findById(id).orElse(null);
        
        if (cita == null) {
            redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
            return "redirect:/medico/dashboard/citas";
        }

        // Verificar que la cita pertenece al médico
        if (cita.getMedico() == null || !cita.getMedico().getIdMedico().equals(medico.getIdMedico())) {
            redirectAttrs.addFlashAttribute("error", "No tiene permiso para ver esta cita");
            return "redirect:/medico/dashboard/citas";
        }

        // Actualizar estado de esta cita específica
        actualizarEstadoCita(cita);
        agendamientoRepository.save(cita);

        model.addAttribute("cita", cita);
        model.addAttribute("medico", medico);

        return "medico/cita-detalle";
    }

    @PostMapping("/citas/{id}/cancelar")
    public String cancelarCita(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttrs) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        Agendamiento cita = agendamientoRepository.findById(id).orElse(null);
        
        if (cita == null) {
            redirectAttrs.addFlashAttribute("error", "Cita no encontrada");
            return "redirect:/medico/dashboard/citas";
        }

        // Verificar que la cita pertenece al médico
        if (cita.getMedico() == null || !cita.getMedico().getIdMedico().equals(medico.getIdMedico())) {
            redirectAttrs.addFlashAttribute("error", "No tiene permiso para cancelar esta cita");
            return "redirect:/medico/dashboard/citas";
        }

        // Solo se pueden cancelar citas programadas o en curso
        if ("Completada".equals(cita.getEstado()) || "Cancelada".equals(cita.getEstado()) || 
            "No Asistió".equals(cita.getEstado())) {
            redirectAttrs.addFlashAttribute("error", "No se puede cancelar una cita con estado: " + cita.getEstado());
            return "redirect:/medico/dashboard/citas/" + id;
        }

        cita.setEstado("Cancelada");
        agendamientoRepository.save(cita);

        redirectAttrs.addFlashAttribute("success", "Cita cancelada exitosamente");
        return "redirect:/medico/dashboard/citas";
    }

    @GetMapping("/pacientes")
    public String misPacientes(HttpSession session, Model model) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        List<Paciente> pacientes = agendamientoRepository.findDistinctPacientesByMedico(medico);

        model.addAttribute("pacientes", pacientes);
        model.addAttribute("medico", medico);

        return "medico/pacientes";
    }

    @GetMapping("/pacientes/{id}/citas")
    public String citasPaciente(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        // Verificar que el paciente tiene citas con este médico
        List<Paciente> pacientesDelMedico = agendamientoRepository.findDistinctPacientesByMedico(medico);
        Paciente paciente = pacientesDelMedico.stream()
            .filter(p -> p.getIdPaciente().equals(id))
            .findFirst()
            .orElse(null);

        if (paciente == null) {
            redirectAttrs.addFlashAttribute("error", "No tiene permiso para ver este paciente");
            return "redirect:/medico/dashboard/pacientes";
        }

        // Obtener todas las citas entre este médico y este paciente
        List<Agendamiento> citas = agendamientoRepository.findByMedicoOrderByFechaAscHoraAsc(medico)
            .stream()
            .filter(c -> c.getPaciente().getIdPaciente().equals(id))
            .collect(Collectors.toList());

        model.addAttribute("paciente", paciente);
        model.addAttribute("citas", citas);
        model.addAttribute("medico", medico);

        return "medico/paciente-citas";
    }

    @GetMapping("/perfil")
    public String verPerfil(HttpSession session, Model model) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        model.addAttribute("medico", medico);
        return "medico/perfil";
    }

    @GetMapping("/perfil/editar")
    public String editarPerfil(HttpSession session, Model model) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        model.addAttribute("medico", medico);
        return "medico/editar-perfil";
    }

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(@ModelAttribute Medico medicoForm,
                                   @RequestParam(required = false) String contrasenaActual,
                                   @RequestParam(required = false) String contrasenaNueva,
                                   HttpSession session,
                                   RedirectAttributes redirectAttrs) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        // Validar campos obligatorios
        if (medicoForm.getNombre() == null || medicoForm.getNombre().trim().isEmpty() ||
            medicoForm.getApellido() == null || medicoForm.getApellido().trim().isEmpty() ||
            medicoForm.getCorreo() == null || medicoForm.getCorreo().trim().isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "Los campos nombre, apellido y correo son obligatorios");
            return "redirect:/medico/dashboard";
        }

        // Actualizar datos básicos
        medico.setNombre(medicoForm.getNombre());
        medico.setApellido(medicoForm.getApellido());
        medico.setTelefono(medicoForm.getTelefono());
        medico.setCorreo(medicoForm.getCorreo());
        medico.setEspecialidad(medicoForm.getEspecialidad());

        // Cambiar contraseña si se proporciona
        if (contrasenaNueva != null && !contrasenaNueva.isEmpty()) {
            if (contrasenaActual == null || !contrasenaActual.equals(medico.getContraseña())) {
                redirectAttrs.addFlashAttribute("error", "La contraseña actual es incorrecta");
                return "redirect:/medico/dashboard";
            }
            medico.setContraseña(contrasenaNueva);
        }

        medicoRepository.save(medico);
        
        // Actualizar sesión
        session.setAttribute("usuario", medico);
        session.setAttribute("nombreCompleto", medico.getNombre() + " " + medico.getApellido());

        redirectAttrs.addFlashAttribute("success", "Perfil actualizado exitosamente");
        return "redirect:/medico/dashboard";
    }

    @GetMapping("/historias-clinicas")
    public String historiasClinicas(HttpSession session, Model model) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        List<proyecto.conexiondb.JPA.Entity.Historia_Clinica> historias = 
            historiaClinicaRepository.findByMedicoOrderByFechaAtencionDesc(medico);

        model.addAttribute("historias", historias);
        model.addAttribute("medico", medico);

        return "medico/historias-clinicas";
    }

    @GetMapping("/pacientes/{id}/historia")
    public String verHistoriaPaciente(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        // Verificar que el paciente tiene citas con este médico
        List<Paciente> pacientesDelMedico = agendamientoRepository.findDistinctPacientesByMedico(medico);
        Paciente paciente = pacientesDelMedico.stream()
            .filter(p -> p.getIdPaciente().equals(id))
            .findFirst()
            .orElse(null);

        if (paciente == null) {
            redirectAttrs.addFlashAttribute("error", "No tiene permiso para ver este paciente");
            return "redirect:/medico/dashboard/pacientes";
        }

        // Ver TODAS las historias clínicas del paciente (de todos los médicos)
        List<proyecto.conexiondb.JPA.Entity.Historia_Clinica> historias = 
            historiaClinicaRepository.findByPacienteOrderByFechaAtencionDesc(paciente);

        model.addAttribute("paciente", paciente);
        model.addAttribute("historias", historias);
        model.addAttribute("medico", medico);

        return "medico/paciente-historia";
    }

    @GetMapping("/pacientes/{id}/historia/nueva")
    public String nuevaHistoria(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttrs) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        // Verificar que el paciente tiene citas con este médico
        List<Paciente> pacientesDelMedico = agendamientoRepository.findDistinctPacientesByMedico(medico);
        Paciente paciente = pacientesDelMedico.stream()
            .filter(p -> p.getIdPaciente().equals(id))
            .findFirst()
            .orElse(null);

        if (paciente == null) {
            redirectAttrs.addFlashAttribute("error", "No tiene permiso para crear historia clínica de este paciente");
            return "redirect:/medico/dashboard/pacientes";
        }

        model.addAttribute("paciente", paciente);
        model.addAttribute("medico", medico);

        return "medico/nueva-historia";
    }

    @PostMapping("/pacientes/{id}/historia/guardar")
    public String guardarHistoria(@PathVariable Long id,
                                  @RequestParam String fechaAtencion,
                                  @RequestParam String antecedentes,
                                  @RequestParam String diagnostico,
                                  @RequestParam String tratamiento,
                                  @RequestParam(required = false) String observaciones,
                                  @RequestParam(required = false) Long citaId,
                                  HttpSession session,
                                  RedirectAttributes redirectAttrs) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        // Validar campos obligatorios
        if (diagnostico == null || diagnostico.trim().isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "El diagnóstico es obligatorio");
            return "redirect:/medico/dashboard/pacientes/" + id + "/historia/nueva";
        }

        if (tratamiento == null || tratamiento.trim().isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "El tratamiento es obligatorio");
            return "redirect:/medico/dashboard/pacientes/" + id + "/historia/nueva";
        }

        if (fechaAtencion == null || fechaAtencion.trim().isEmpty()) {
            redirectAttrs.addFlashAttribute("error", "La fecha de atención es obligatoria");
            return "redirect:/medico/dashboard/pacientes/" + id + "/historia/nueva";
        }

        // Verificar que el paciente tiene citas con este médico
        List<Paciente> pacientesDelMedico = agendamientoRepository.findDistinctPacientesByMedico(medico);
        Paciente paciente = pacientesDelMedico.stream()
            .filter(p -> p.getIdPaciente().equals(id))
            .findFirst()
            .orElse(null);

        if (paciente == null) {
            redirectAttrs.addFlashAttribute("error", "No tiene permiso para crear historia clínica de este paciente");
            return "redirect:/medico/dashboard/pacientes";
        }

        // Crear historia clínica
        proyecto.conexiondb.JPA.Entity.Historia_Clinica historia = new proyecto.conexiondb.JPA.Entity.Historia_Clinica();
        historia.setPaciente(paciente);
        historia.setMedico(medico);
        historia.setAntecedentes(antecedentes);
        historia.setDiagnostico(diagnostico);
        historia.setTratamiento(tratamiento);
        historia.setObservaciones(observaciones);
        historia.setFechaCreacion(Date.valueOf(LocalDate.now()));
        historia.setFechaAtencion(Date.valueOf(fechaAtencion));

        historiaClinicaRepository.save(historia);

        // Si viene de una cita, marcarla como completada
        if (citaId != null) {
            Agendamiento cita = agendamientoRepository.findById(citaId).orElse(null);
            if (cita != null && cita.getMedico().getIdMedico().equals(medico.getIdMedico())) {
                cita.setEstado("Completada");
                agendamientoRepository.save(cita);
            }
        }

        redirectAttrs.addFlashAttribute("success", "Historia clínica creada exitosamente");
        
        // Si viene de una cita, redirigir a las citas
        if (citaId != null) {
            return "redirect:/medico/dashboard/citas";
        }
        
        return "redirect:/medico/dashboard/pacientes/" + id + "/historia";
    }

    @GetMapping("/buscar-paciente")
    public String buscarPaciente(@RequestParam(required = false) String q, HttpSession session, Model model) {
        Medico medico = obtenerMedicoSesion(session);
        if (medico == null) {
            return "redirect:/login";
        }

        List<Paciente> pacientes = agendamientoRepository.findDistinctPacientesByMedico(medico);

        if (q != null && !q.isEmpty()) {
            String buscarLower = q.toLowerCase();
            pacientes = pacientes.stream()
                .filter(p -> {
                    String nombreCompleto = (p.getNombre() + " " + p.getApellido()).toLowerCase();
                    String documento = p.getNumeroDoc().toLowerCase();
                    return nombreCompleto.contains(buscarLower) || documento.contains(buscarLower);
                })
                .collect(Collectors.toList());
        }

        model.addAttribute("pacientes", pacientes);
        model.addAttribute("q", q);
        model.addAttribute("medico", medico);

        return "medico/buscar-paciente";
    }

    private Medico obtenerMedicoSesion(HttpSession session) {
        Object usuario = session.getAttribute("usuario");
        String tipoUsuario = (String) session.getAttribute("tipoUsuario");
        
        if (usuario instanceof Medico && "medico".equals(tipoUsuario)) {
            Medico medicoSesion = (Medico) usuario;
            // Recargar desde la base de datos para asegurar que tenemos los datos actualizados
            return medicoRepository.findById(medicoSesion.getIdMedico()).orElse(medicoSesion);
        }
        return null;
    }

    private void actualizarEstadosCitas(Medico medico) {
        List<Agendamiento> citas = agendamientoRepository.findByMedicoOrderByFechaAscHoraAsc(medico);
        
        for (Agendamiento cita : citas) {
            if (actualizarEstadoCita(cita)) {
                agendamientoRepository.save(cita);
            }
        }
    }

    private boolean actualizarEstadoCita(Agendamiento cita) {
        // No actualizar citas ya completadas, canceladas o marcadas como no asistió
        if ("Completada".equals(cita.getEstado()) || 
            "Cancelada".equals(cita.getEstado()) || 
            "No Asistió".equals(cita.getEstado())) {
            return false;
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDate fechaCita = cita.getFecha().toLocalDate();
        
        // Parsear la hora (formato HH:mm)
        LocalTime horaCita;
        try {
            horaCita = LocalTime.parse(cita.getHora());
        } catch (Exception e) {
            // Si no se puede parsear, mantener el estado actual
            return false;
        }
        
        LocalDateTime fechaHoraCita = LocalDateTime.of(fechaCita, horaCita);
        LocalDateTime fechaHoraLimite = fechaHoraCita.plusMinutes(30);
        
        // Si la cita es en el futuro, debe estar "Programada"
        if (fechaHoraCita.isAfter(ahora)) {
            if (!"Programada".equals(cita.getEstado())) {
                cita.setEstado("Programada");
                return true;
            }
            return false;
        }
        
        // Si estamos dentro de los 30 minutos de la cita, está "En Curso"
        if (ahora.isBefore(fechaHoraLimite)) {
            if (!"En Curso".equals(cita.getEstado())) {
                cita.setEstado("En Curso");
                return true;
            }
            return false;
        }
        
        // Si pasaron más de 30 minutos y no fue atendida, marcar como "No Asistió"
        if (!"No Asistió".equals(cita.getEstado())) {
            cita.setEstado("No Asistió");
            return true;
        }
        
        return false;
    }
}
