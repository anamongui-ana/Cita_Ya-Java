package proyecto.conexiondb.JPA.Controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proyecto.conexiondb.JPA.Entity.Paciente;
import proyecto.conexiondb.JPA.Repository.PacienteRepository;

import java.util.Optional;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteRepository pacienteRepository;
    private final proyecto.conexiondb.JPA.Repository.AgendamientoRepository agendamientoRepository;
    private final proyecto.conexiondb.JPA.Repository.HistoriaClinicaRepository historiaClinicaRepository;

    public PacienteController(PacienteRepository pacienteRepository,
                             proyecto.conexiondb.JPA.Repository.AgendamientoRepository agendamientoRepository,
                             proyecto.conexiondb.JPA.Repository.HistoriaClinicaRepository historiaClinicaRepository) {
        this.pacienteRepository = pacienteRepository;
        this.agendamientoRepository = agendamientoRepository;
        this.historiaClinicaRepository = historiaClinicaRepository;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, jakarta.servlet.http.HttpSession session) {
        proyecto.conexiondb.JPA.Entity.Paciente paciente = 
            (proyecto.conexiondb.JPA.Entity.Paciente) session.getAttribute("usuario");
        
        if (paciente == null) {
            return "redirect:/login";
        }
        
        // Recargar desde la base de datos para asegurar datos actualizados
        paciente = pacienteRepository.findById(paciente.getIdPaciente()).orElse(paciente);
        
        // Obtener las citas del paciente
        java.util.List<proyecto.conexiondb.JPA.Entity.Agendamiento> citas = 
            agendamientoRepository.findByPaciente(paciente);
        
        // Obtener los historiales clínicos del paciente
        java.util.List<proyecto.conexiondb.JPA.Entity.Historia_Clinica> historiales = 
            historiaClinicaRepository.findByPacienteOrderByFechaAtencionDesc(paciente);
        
        model.addAttribute("citas", citas);
        model.addAttribute("totalCitas", citas.size());
        model.addAttribute("historiales", historiales);
        model.addAttribute("totalHistoriales", historiales.size());
        
        return "layouts/paciente";
    }

    @GetMapping
    public String listar(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String tipo_doc,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String tipo_sangre,
            @RequestParam(required = false) String fecha_desde,
            @RequestParam(required = false) String fecha_hasta,
            @RequestParam(required = false, defaultValue = "desc") String orden,
            Model model) {
        
        // Obtener pacientes filtrados
        java.util.List<Paciente> pacientes = obtenerPacientesFiltrados(buscar, tipo_doc, genero, tipo_sangre, fecha_desde, fecha_hasta, orden);
        
        // Verificar si hay filtros activos
        boolean hayFiltros = (buscar != null && !buscar.isEmpty()) ||
                            (tipo_doc != null && !tipo_doc.isEmpty()) ||
                            (genero != null && !genero.isEmpty()) ||
                            (tipo_sangre != null && !tipo_sangre.isEmpty()) ||
                            (fecha_desde != null && !fecha_desde.isEmpty()) ||
                            (fecha_hasta != null && !fecha_hasta.isEmpty());
        
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("filtered", hayFiltros);
        model.addAttribute("total", pacientes.size());
        
        return "pacientes/index";
    }

    @GetMapping("/create")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "pacientes/create";
    }

    @PostMapping("/create")
    public String crear(@ModelAttribute Paciente paciente, RedirectAttributes redirectAttrs) {
        pacienteRepository.save(paciente);
        redirectAttrs.addFlashAttribute("success", "Paciente creado exitosamente");
        return "redirect:/pacientes";
    }

    // Rutas para el paciente logueado (deben ir ANTES de /{id})
    @GetMapping("/perfil")
    public String verPerfil(Model model, jakarta.servlet.http.HttpSession session, RedirectAttributes redirectAttrs) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        // Recargar el paciente desde la base de datos para tener datos actualizados
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(paciente.getIdPaciente());
        if (pacienteOpt.isPresent()) {
            model.addAttribute("paciente", pacienteOpt.get());
            return "pacientes/verPerfil"; // Vista específica para pacientes
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes/dashboard";
        }
    }

    @GetMapping("/editar")
    public String editarPerfil(Model model, jakarta.servlet.http.HttpSession session, RedirectAttributes redirectAttrs) {
        Paciente paciente = (Paciente) session.getAttribute("usuario");
        if (paciente == null) {
            return "redirect:/login";
        }
        
        // Recargar el paciente desde la base de datos para tener datos actualizados
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(paciente.getIdPaciente());
        if (pacienteOpt.isPresent()) {
            model.addAttribute("paciente", pacienteOpt.get());
            return "pacientes/editarPerfil"; // Vista específica para pacientes
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes/dashboard";
        }
    }

    @PostMapping("/editar")
    public String actualizarPerfil(@ModelAttribute Paciente paciente, 
                                   jakarta.servlet.http.HttpSession session,
                                   RedirectAttributes redirectAttrs) {
        Paciente pacienteSession = (Paciente) session.getAttribute("usuario");
        if (pacienteSession == null) {
            return "redirect:/login";
        }
        
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(pacienteSession.getIdPaciente());
        if (pacienteOpt.isPresent()) {
            Paciente pacienteExistente = pacienteOpt.get();
            pacienteExistente.setNombre(paciente.getNombre());
            pacienteExistente.setApellido(paciente.getApellido());
            pacienteExistente.setTipoDoc(paciente.getTipoDoc());
            pacienteExistente.setNumeroDoc(paciente.getNumeroDoc());
            pacienteExistente.setGenero(paciente.getGenero());
            pacienteExistente.setTelefono(paciente.getTelefono());
            pacienteExistente.setCorreo(paciente.getCorreo());
            pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
            pacienteExistente.setTipoSangre(paciente.getTipoSangre());
            pacienteExistente.setDireccion(paciente.getDireccion());
            if (paciente.getContrasena() != null && !paciente.getContrasena().isEmpty()) {
                pacienteExistente.setContrasena(paciente.getContrasena());
            }
            pacienteRepository.save(pacienteExistente);
            
            // Actualizar la sesión con los datos nuevos
            session.setAttribute("usuario", pacienteExistente);
            session.setAttribute("nombreCompleto", pacienteExistente.getNombre() + " " + pacienteExistente.getApellido());
            
            redirectAttrs.addFlashAttribute("success", "Perfil actualizado exitosamente");
            return "redirect:/pacientes/dashboard";
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes/dashboard";
        }
    }

    @GetMapping("/{id}")
    public String ver(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent()) {
            model.addAttribute("paciente", pacienteOpt.get());
            return "pacientes/show";
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes";
        }
    }

    @GetMapping("/edit/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttrs) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent()) {
            model.addAttribute("paciente", pacienteOpt.get());
            model.addAttribute("esPerfilPropio", false); // Indicador para el formulario
            return "pacientes/edit";
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes";
        }
    }

    @PostMapping("/edit/{id}")
    public String editar(@PathVariable("id") Long id, @ModelAttribute Paciente paciente, RedirectAttributes redirectAttrs) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
        if (pacienteOpt.isPresent()) {
            Paciente pacienteExistente = pacienteOpt.get();
            pacienteExistente.setNombre(paciente.getNombre());
            pacienteExistente.setApellido(paciente.getApellido());
            pacienteExistente.setTipoDoc(paciente.getTipoDoc());
            pacienteExistente.setNumeroDoc(paciente.getNumeroDoc());
            pacienteExistente.setGenero(paciente.getGenero());
            pacienteExistente.setTelefono(paciente.getTelefono());
            pacienteExistente.setCorreo(paciente.getCorreo());
            pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
            pacienteExistente.setTipoSangre(paciente.getTipoSangre());
            pacienteExistente.setDireccion(paciente.getDireccion());
            if (paciente.getContrasena() != null && !paciente.getContrasena().isEmpty()) {
                pacienteExistente.setContrasena(paciente.getContrasena());
            }
            pacienteRepository.save(pacienteExistente);
            redirectAttrs.addFlashAttribute("success", "Paciente actualizado exitosamente");
            return "redirect:/pacientes";
        } else {
            redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/pacientes";
        }
    }

    @PostMapping("/delete/{id}")
    public String eliminar(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        System.out.println("=== INTENTANDO INACTIVAR PACIENTE ID: " + id + " ===");
        try {
            Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
            if (pacienteOpt.isPresent()) {
                Paciente paciente = pacienteOpt.get();
                System.out.println("Paciente encontrado: " + paciente.getNombre() + " " + paciente.getApellido());
                System.out.println("Estado actual: " + paciente.getEstado());
                
                // Borrado lógico: marcar como inactivo (1 = inactivo, 0 = activo)
                paciente.setEstado(1);
                System.out.println("Nuevo estado: " + paciente.getEstado());
                
                Paciente guardado = pacienteRepository.save(paciente);
                System.out.println("Paciente guardado con estado: " + guardado.getEstado());
                
                redirectAttrs.addFlashAttribute("success", "Paciente marcado como inactivo exitosamente");
            } else {
                System.out.println("ERROR: Paciente no encontrado");
                redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            }
        } catch (Exception e) {
            System.out.println("ERROR AL INACTIVAR: " + e.getMessage());
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        System.out.println("=== FIN INACTIVAR PACIENTE ===");
        return "redirect:/pacientes";
    }

    @PostMapping("/activate/{id}")
    public String activar(@PathVariable("id") Long id, RedirectAttributes redirectAttrs) {
        System.out.println("=== INTENTANDO ACTIVAR PACIENTE ID: " + id + " ===");
        try {
            Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);
            if (pacienteOpt.isPresent()) {
                Paciente paciente = pacienteOpt.get();
                System.out.println("Paciente encontrado: " + paciente.getNombre() + " " + paciente.getApellido());
                System.out.println("Estado actual: " + paciente.getEstado());
                
                // Activar: marcar como activo (0 = activo, 1 = inactivo)
                paciente.setEstado(0);
                System.out.println("Nuevo estado: " + paciente.getEstado());
                
                Paciente guardado = pacienteRepository.save(paciente);
                System.out.println("Paciente guardado con estado: " + guardado.getEstado());
                
                redirectAttrs.addFlashAttribute("success", "Paciente activado exitosamente");
            } else {
                System.out.println("ERROR: Paciente no encontrado");
                redirectAttrs.addFlashAttribute("error", "Paciente no encontrado");
            }
        } catch (Exception e) {
            System.out.println("ERROR AL ACTIVAR: " + e.getMessage());
            e.printStackTrace();
            redirectAttrs.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        System.out.println("=== FIN ACTIVAR PACIENTE ===");
        return "redirect:/pacientes";
    }

    @GetMapping("/reporte")
    public String generarReporte(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String tipo_doc,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String tipo_sangre,
            @RequestParam(required = false) String fecha_desde,
            @RequestParam(required = false) String fecha_hasta,
            @RequestParam(required = false, defaultValue = "desc") String orden,
            Model model) {
        
        // Obtener pacientes filtrados
        java.util.List<Paciente> pacientes = obtenerPacientesFiltrados(buscar, tipo_doc, genero, tipo_sangre, fecha_desde, fecha_hasta, orden);
        
        // Crear mapa de filtros para mostrar en la vista
        java.util.Map<String, String> filtros = new java.util.HashMap<>();
        if (buscar != null && !buscar.isEmpty()) filtros.put("buscar", buscar);
        if (tipo_doc != null && !tipo_doc.isEmpty()) filtros.put("tipo_doc", tipo_doc);
        if (genero != null && !genero.isEmpty()) filtros.put("genero", genero);
        if (tipo_sangre != null && !tipo_sangre.isEmpty()) filtros.put("tipo_sangre", tipo_sangre);
        if (fecha_desde != null && !fecha_desde.isEmpty()) filtros.put("fecha_desde", fecha_desde);
        if (fecha_hasta != null && !fecha_hasta.isEmpty()) filtros.put("fecha_hasta", fecha_hasta);
        
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("fechaActual", java.time.LocalDate.now().toString());
        model.addAttribute("filtros", filtros);
        return "pacientes/reporte";
    }

    @GetMapping("/reporte/pdf")
    public void generarReportePDF(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String tipo_doc,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String tipo_sangre,
            @RequestParam(required = false) String fecha_desde,
            @RequestParam(required = false) String fecha_hasta,
            @RequestParam(required = false, defaultValue = "desc") String orden,
            jakarta.servlet.http.HttpServletResponse response) throws Exception {
        
        // Obtener pacientes con filtros
        java.util.List<Paciente> pacientes = obtenerPacientesFiltrados(buscar, tipo_doc, genero, tipo_sangre, fecha_desde, fecha_hasta, orden);
        
        // Configurar respuesta HTTP
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=reporte_pacientes.pdf");
        
        // Generar PDF
        generarPDF(pacientes, response.getOutputStream(), buscar, tipo_doc, genero, tipo_sangre, fecha_desde, fecha_hasta);
    }

    private java.util.List<Paciente> obtenerPacientesFiltrados(String buscar, String tipo_doc, String genero, 
                                                                 String tipo_sangre, String fecha_desde, String fecha_hasta, String orden) {
        java.util.List<Paciente> pacientes = pacienteRepository.findAll(
            Sort.by("asc".equals(orden) ? Sort.Direction.ASC : Sort.Direction.DESC, "idPaciente")
        );
        
        return pacientes.stream()
            .filter(p -> buscar == null || buscar.isEmpty() || 
                        p.getNombre().toLowerCase().contains(buscar.toLowerCase()) ||
                        p.getApellido().toLowerCase().contains(buscar.toLowerCase()) ||
                        p.getNumeroDoc().contains(buscar) ||
                        (p.getCorreo() != null && p.getCorreo().toLowerCase().contains(buscar.toLowerCase())))
            .filter(p -> tipo_doc == null || tipo_doc.isEmpty() || p.getTipoDoc().equals(tipo_doc))
            .filter(p -> genero == null || genero.isEmpty() || p.getGenero().equals(genero))
            .filter(p -> tipo_sangre == null || tipo_sangre.isEmpty() || 
                        (p.getTipoSangre() != null && p.getTipoSangre().equals(tipo_sangre)))
            .filter(p -> {
                if (fecha_desde == null || fecha_desde.isEmpty()) return true;
                try {
                    java.util.Date fechaDesdeDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fecha_desde);
                    return p.getFechaNacimiento() != null && 
                           !p.getFechaNacimiento().before(fechaDesdeDate);
                } catch (Exception e) {
                    return true;
                }
            })
            .filter(p -> {
                if (fecha_hasta == null || fecha_hasta.isEmpty()) return true;
                try {
                    java.util.Date fechaHastaDate = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(fecha_hasta);
                    return p.getFechaNacimiento() != null && 
                           !p.getFechaNacimiento().after(fechaHastaDate);
                } catch (Exception e) {
                    return true;
                }
            })
            .collect(java.util.stream.Collectors.toList());
    }

    private void generarPDF(java.util.List<Paciente> pacientes, java.io.OutputStream outputStream,
                           String buscar, String tipo_doc, String genero, String tipo_sangre, 
                           String fecha_desde, String fecha_hasta) throws Exception {
        
        com.itextpdf.kernel.pdf.PdfWriter writer = new com.itextpdf.kernel.pdf.PdfWriter(outputStream);
        com.itextpdf.kernel.pdf.PdfDocument pdf = new com.itextpdf.kernel.pdf.PdfDocument(writer);
        com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf, com.itextpdf.kernel.geom.PageSize.A4.rotate());
        
        // Título
        com.itextpdf.layout.element.Paragraph titulo = new com.itextpdf.layout.element.Paragraph("Reporte de Pacientes - Cita Ya")
            .setFontSize(18)
            .setBold()
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER);
        document.add(titulo);
        
        // Fecha
        com.itextpdf.layout.element.Paragraph fecha = new com.itextpdf.layout.element.Paragraph("Fecha: " + java.time.LocalDate.now())
            .setFontSize(10)
            .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT);
        document.add(fecha);
        
        // Filtros aplicados
        if (buscar != null || tipo_doc != null || genero != null || tipo_sangre != null || fecha_desde != null || fecha_hasta != null) {
            StringBuilder filtrosTexto = new StringBuilder("Filtros aplicados: ");
            if (buscar != null && !buscar.isEmpty()) filtrosTexto.append("Búsqueda: ").append(buscar).append(" | ");
            if (tipo_doc != null && !tipo_doc.isEmpty()) filtrosTexto.append("Tipo Doc: ").append(tipo_doc).append(" | ");
            if (genero != null && !genero.isEmpty()) filtrosTexto.append("Género: ").append(genero).append(" | ");
            if (tipo_sangre != null && !tipo_sangre.isEmpty()) filtrosTexto.append("Tipo Sangre: ").append(tipo_sangre).append(" | ");
            if (fecha_desde != null && !fecha_desde.isEmpty()) filtrosTexto.append("Desde: ").append(fecha_desde).append(" | ");
            if (fecha_hasta != null && !fecha_hasta.isEmpty()) filtrosTexto.append("Hasta: ").append(fecha_hasta);
            
            com.itextpdf.layout.element.Paragraph filtros = new com.itextpdf.layout.element.Paragraph(filtrosTexto.toString())
                .setFontSize(9)
                .setItalic();
            document.add(filtros);
        }
        
        document.add(new com.itextpdf.layout.element.Paragraph("\n"));
        
        // Tabla
        float[] columnWidths = {1, 2, 2, 2, 1.5f, 2, 1.5f, 3, 2, 3};
        com.itextpdf.layout.element.Table table = new com.itextpdf.layout.element.Table(columnWidths);
        table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));
        
        // Encabezados
        String[] headers = {"ID", "Documento", "Nombre", "Apellido", "Género", "Fecha Nac.", "Tipo Sangre", "Dirección", "Teléfono", "Correo"};
        for (String header : headers) {
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                .add(new com.itextpdf.layout.element.Paragraph(header))
                .setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY)
                .setBold()
                .setFontSize(8));
        }
        
        // Datos
        for (Paciente p : pacientes) {
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(String.valueOf(p.getIdPaciente()))).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getTipoDoc() + " " + p.getNumeroDoc())).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getNombre())).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getApellido())).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getGenero() != null ? p.getGenero() : "")).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getFechaNacimiento() != null ? p.getFechaNacimiento().toString() : "")).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getTipoSangre() != null ? p.getTipoSangre() : "")).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getDireccion() != null ? p.getDireccion() : "")).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getTelefono() != null ? p.getTelefono() : "")).setFontSize(7));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(p.getCorreo() != null ? p.getCorreo() : "")).setFontSize(7));
        }
        
        document.add(table);
        
        // Total
        document.add(new com.itextpdf.layout.element.Paragraph("\nTotal de registros: " + pacientes.size())
            .setFontSize(10)
            .setBold());
        
        document.close();
    }
}
