<!DOCTYPE html>
<html lang="es" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" th:href="@{/Assets/IMG/logotipo.png}">
    <link rel="stylesheet" th:href="@{/Assets/CSS/dashboard.css}">
    <title>Dashboard Médico - Cita Ya</title>
</head>
<body>

<header>
    <div class="container_header">
        <img class="logo" th:src="@{/Assets/IMG/logo.png}" alt="Cita Ya Logo">
        
        <div class="profile_dropdown">
            <button class="profile_button" onclick="toggleDropdown()">
                Dr. <span th:text="${medico.nombre} + ' ' + ${medico.apellido}"></span> ▼
            </button>
            <div class="dropdown_content" id="profileDropdown">
                <a href="#" class="dropdown_item" onclick="openProfileModal()">Ver Mi Perfil</a>
                <a href="#" class="dropdown_item" onclick="openEditModal()">Editar Perfil</a>
                <hr style="margin:0;">
                <form th:action="@{/logout}" method="post" style="margin:0;">
                    <button type="submit" class="dropdown_item" style="width:100%; background:none; border:none; text-align:left;">Cerrar Sesión</button>
                </form>
            </div>
        </div>
    </div>
</header>

<div class="container_main">
    <div class="welcome_section">
        <h1 class="welcome_title" th:text="${medico.nombre} + ' ' + ${medico.apellido}"></h1>
        <p class="welcome_subtitle" th:text="${medico.especialidad} + ' - Dashboard Médico'"></p>
    </div>

    <div class="date_filter">
        <label for="fecha_consulta">Ver citas del día:</label>
        <input type="date" id="fecha_consulta" th:value="${fechaSeleccionada}" onchange="cambiarFecha()">
        <button onclick="hoy()">Hoy</button>
        <button onclick="actualizarVista()">Actualizar</button>
    </div>

    <div class="stats_container">
        <div class="stat_card success">
            <div class="stat_number" th:text="${agendamientos.size()}"></div>
            <div class="stat_label">Citas del Día</div>
        </div>
    </div>

    <div class="content_grid">
        <div class="card">
            <div class="card_header">
                <h5 class="card_title" th:text="'Mis Pacientes del ' + ${#dates.format(fechaSeleccionada, 'dd/MM/yyyy')}"></h5>
                <span class="badge" th:text="${agendamientos.size()} + ' citas'"></span>
            </div>
            <div class="card_body">
                <div th:if="${agendamientos.size() > 0}">
                    <div th:each="agendamiento : ${agendamientos}" class="patient_card">
                        <div class="patient_header">
                            <div class="patient_info">
                                <h4 th:text="${agendamiento.paciente.nombre} + ' ' + ${agendamiento.paciente.apellido}"></h4>
                                <div class="patient_meta" th:text="${agendamiento.paciente.tipoDoc} + ' ' + ${agendamiento.paciente.numeroDoc} + ' • ' + ${agendamiento.cita}"></div>
                            </div>
                            <div class="appointment_time" th:text="${#dates.format(agendamiento.hora, 'HH:mm')}"></div>
                        </div>

                        <div class="patient_details">
                            <div class="detail_item">
                                <span class="detail_label">Género:</span>
                                <span th:text="${agendamiento.paciente.genero}"></span>
                            </div>
                            <div class="detail_item">
                                <span class="detail_label">Edad:</span>
                                <span th:text="${#dates.period(agendamiento.paciente.fechaNacimiento, #dates.createNow()).years} + ' años'"></span>
                            </div>
                            <div class="detail_item">
                                <span class="detail_label">Tipo Sangre:</span>
                                <span th:text="${agendamiento.paciente.tipoSangre != null ? agendamiento.paciente.tipoSangre : 'No especificado'}"></span>
                            </div>
                            <div class="detail_item">
                                <span class="detail_label">Teléfono:</span>
                                <span th:text="${agendamiento.paciente.telefono != null ? agendamiento.paciente.telefono : 'No especificado'}"></span>
                            </div>
                        </div>

                        <div class="patient_actions">
                            <button class="btn btn_primary btn_small" th:onclick="'openHistorialModal(' + ${agendamiento.paciente.idPaciente} + ', \'' + ${agendamiento.paciente.nombre} + ' ' + ${agendamiento.paciente.apellido} + '\')'">Ver - Editar Historial</button>
                            <button class="btn btn_success btn_small" th:onclick="'crearHistorial(' + ${agendamiento.paciente.idPaciente} + ', \'' + ${agendamiento.paciente.nombre} + ' ' + ${agendamiento.paciente.apellido} + '\')'">Nuevo Historial</button>
                            <button class="btn btn_info btn_small" th:onclick="'verInfoCompleta(' + ${agendamiento.paciente.idPaciente} + ', \'' + ${agendamiento.paciente.nombre} + '\', \'' + ${agendamiento.paciente.apellido} + '\', \'' + ${agendamiento.paciente.tipoDoc} + '\', \'' + ${agendamiento.paciente.numeroDoc} + '\', \'' + ${agendamiento.paciente.genero} + '\', \'' + ${#dates.format(agendamiento.paciente.fechaNacimiento,'yyyy-MM-dd')} + '\', \'' + (${agendamiento.paciente.tipoSangre} != null ? ${agendamiento.paciente.tipoSangre} : '') + '\', \'' + (${agendamiento.paciente.direccion} != null ? ${agendamiento.paciente.direccion} : '') + '\', \'' + (${agendamiento.paciente.telefono} != null ? ${agendamiento.paciente.telefono} : '') + '\', \'' + (${agendamiento.paciente.correo} != null ? ${agendamiento.paciente.correo} : '') + '\')'">Info Paciente</button>
                        </div>
                    </div>
                </div>
                <div th:if="${agendamientos.size() == 0}" class="empty_state">
                    <div class="empty_icon">📅</div>
                    <p>No tienes citas programadas para esta fecha</p>
                    <button class="btn btn_primary" onclick="cambiarFecha()" style="margin-top:1rem;">Seleccionar otra fecha</button>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modals -->
<div th:replace="fragments/modals :: allModals"></div>

<script th:src="@{/Assets/JS/dashboard.js}"></script>

</body>
</html>
