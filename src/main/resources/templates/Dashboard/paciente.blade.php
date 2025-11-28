<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="icon" type="image" href="{{ asset('Assets/IMG/logotipo.png') }}">
    <title>Dashboard Paciente - Cita Ya</title>
    <style>
        * {
            font-family: arial, sans-serif;
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            background-color: #f8f9fa;
            padding-top: 100px;
        }

        /* Header */
        header {
            position: fixed;
            top: 0;
            width: 100%;
            z-index: 1000;
            background-color: #fff;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .container_header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem 2rem;
        }

        .logo {
            width: 4rem;
            height: 4rem;
        }

        .profile_dropdown {
            position: relative;
            display: inline-block;
        }

        .profile_button {
            background-color: #059669;
            color: white;
            padding: 0.7rem 1.5rem;
            border: none;
            border-radius: 0.5rem;
            cursor: pointer;
            font-weight: bold;
            display: flex;
            align-items: center;
            gap: 0.5rem;
            transition: all 0.2s;
        }

        .profile_button:hover {
            background-color: #10b981;
        }

        .dropdown_content {
            display: none;
            position: absolute;
            right: 0;
            background-color: white;
            min-width: 200px;
            box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);
            z-index: 1001;
            border-radius: 0.5rem;
            overflow: hidden;
        }

        .dropdown_content.show {
            display: block;
        }

        .dropdown_item {
            color: #333;
            padding: 12px 16px;
            text-decoration: none;
            display: block;
            transition: background-color 0.2s;
            cursor: pointer;
        }

        .dropdown_item:hover {
            background-color: #f1f1f1;
        }

        /* Main Content */
        .container_main {
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
        }

        /* Welcome Section */
        .welcome_section {
            background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
            color: white;
            padding: 2rem;
            border-radius: 1rem;
            margin-bottom: 2rem;
            text-align: center;
        }

        .welcome_title {
            font-size: 2.5rem;
            margin-bottom: 0.5rem;
            text-shadow: 1px 1px 10px rgba(0, 0, 0, 0.3);
        }

        .welcome_subtitle {
            font-size: 1.1rem;
            opacity: 0.9;
        }

        /* Stats Cards */
        .stats_container {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat_card {
            background: white;
            padding: 1.5rem;
            border-radius: 1rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            text-align: center;
            transition: transform 0.2s;
        }

        .stat_card:hover {
            transform: translateY(-5px);
        }

        .stat_card.primary {
            background: linear-gradient(135deg, #3b82f6, #1e40af);
            color: white;
        }

        .stat_card.success {
            background: linear-gradient(135deg, #10b981, #059669);
            color: white;
        }

        .stat_number {
            font-size: 2.5rem;
            font-weight: bold;
            margin-bottom: 0.5rem;
        }

        .stat_label {
            font-size: 0.9rem;
            opacity: 0.8;
        }

        /* Content Grid */
        .content_grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
            margin-bottom: 2rem;
        }

        /* Cards */
        .card {
            background: white;
            border-radius: 1rem;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .card_header {
            padding: 1.5rem;
            background: linear-gradient(135deg, #f8fafc, #e2e8f0);
            border-bottom: 1px solid #e2e8f0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .card_title {
            font-size: 1.3rem;
            color: #1e3a8a;
            font-weight: bold;
        }

        .badge {
            background-color: #059669;
            color: white;
            padding: 0.3rem 0.8rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: bold;
        }

        .card_body {
            padding: 1.5rem;
            max-height: 500px;
            overflow-y: auto;
        }

        /* Nueva clase para el botón de agendar */
        .card_footer {
            padding: 1rem 1.5rem;
            border-top: 1px solid #e2e8f0;
            text-align: center;
            background: #f8fafc;
        }

        /* Table */
        .table {
            width: 100%;
            border-collapse: collapse;
        }

        .table th,
        .table td {
            padding: 0.8rem;
            text-align: left;
            border-bottom: 1px solid #e2e8f0;
        }

        .table th {
            background-color: #f8fafc;
            font-weight: bold;
            color: #1e3a8a;
        }

        .table tr:hover {
            background-color: #f8fafc;
        }

        /* Status badges */
        .status_badge {
            padding: 0.3rem 0.8rem;
            border-radius: 1rem;
            font-size: 0.8rem;
            font-weight: bold;
        }

        .status_badge.warning {
            background-color: #fbbf24;
            color: white;
        }

        .status_badge.info {
            background-color: #3b82f6;
            color: white;
        }

        .status_badge.secondary {
            background-color: #6b7280;
            color: white;
        }

        /* History Cards */
        .history_card {
            background: white;
            border: 1px solid #e2e8f0;
            border-left: 4px solid #3b82f6;
            border-radius: 0.5rem;
            padding: 1rem;
            margin-bottom: 1rem;
            transition: transform 0.2s;
        }

        .history_card:hover {
            transform: translateX(5px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }

        .history_date {
            font-weight: bold;
            color: #1e3a8a;
            margin-bottom: 0.5rem;
        }

        .history_doctor {
            font-size: 0.9rem;
            color: #6b7280;
            margin-bottom: 0.5rem;
        }

        .btn {
            padding: 0.5rem 1rem;
            border: none;
            border-radius: 0.5rem;
            cursor: pointer;
            font-size: 0.9rem;
            transition: all 0.2s;
            text-decoration: none;
            display: inline-block;
        }

        .btn_outline_primary {
            border: 2px solid #3b82f6;
            color: #3b82f6;
            background: transparent;
        }

        .btn_outline_primary:hover {
            background: #3b82f6;
            color: white;
        }

        .btn_primary {
            background: #059669;
            color: white;
        }

        .btn_primary:hover {
            background: #10b981;
        }

        .btn_large {
            padding: 0.75rem 2rem;
            font-size: 1rem;
            font-weight: bold;
        }

        /* Modal */
        .modal {
            display: none;
            position: fixed;
            z-index: 2000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
        }

        .modal.show {
            display: block;
        }

        .modal_content {
            background-color: white;
            margin: 5% auto;
            padding: 0;
            border-radius: 1rem;
            width: 80%;
            max-width: 600px;
            max-height: 80vh;
            overflow-y: auto;
        }

        .modal_header {
            padding: 1.5rem;
            background: linear-gradient(135deg, #1e3a8a, #3b82f6);
            color: white;
            border-radius: 1rem 1rem 0 0;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .modal_body {
            padding: 1.5rem;
        }

        .modal_footer {
            padding: 1rem 1.5rem;
            border-top: 1px solid #e2e8f0;
            text-align: right;
        }

        .close {
            color: white;
            font-size: 2rem;
            font-weight: bold;
            cursor: pointer;
            background: none;
            border: none;
        }

        .close:hover {
            opacity: 0.7;
        }

        /* Empty states */
        .empty_state {
            text-align: center;
            padding: 3rem;
            color: #6b7280;
        }

        .empty_icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }

        /* Personal Info */
        .info_grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 1rem;
        }

        .info_item {
            padding: 0.5rem 0;
        }

        .info_label {
            font-weight: bold;
            color: #1e3a8a;
        }

        /* Form styles */
        .form_group {
            margin-bottom: 1rem;
        }

        .form_label {
            display: block;
            font-weight: bold;
            color: #1e3a8a;
            margin-bottom: 0.5rem;
        }

        .form_input,
        .form_select {
            width: 100%;
            padding: 0.75rem;
            border: 1px solid #d1d5db;
            border-radius: 0.375rem;
            font-size: 1rem;
            transition: border-color 0.2s;
        }

        .form_input:focus,
        .form_select:focus {
            outline: none;
            border-color: #3b82f6;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
        }

        /* Responsive */
        @media (max-width: 768px) {
            .content_grid {
                grid-template-columns: 1fr;
            }

            .container_main {
                padding: 1rem;
            }

            .welcome_title {
                font-size: 2rem;
            }

            .stats_container {
                grid-template-columns: 1fr;
            }

            .container_header {
                padding: 1rem;
            }

            .modal_content {
                width: 95%;
                margin: 10% auto;
            }
        }
    </style>
</head>
<body>

<header>
    <div class="container_header">
        <img class="logo" src="{{ asset('Assets/IMG/logo.png') }}" alt="Cita Ya Logo">
        
        <div class="profile_dropdown">
            <button class="profile_button" onclick="toggleDropdown()">
                {{ $paciente->nombre }} {{ $paciente->apellido }} ▼
            </button>
            <div class="dropdown_content" id="profileDropdown">
                <a href="#" class="dropdown_item" onclick="openProfileModal()">Ver Mi Perfil</a>
                <a href="#" class="dropdown_item" onclick="openEditModal()">Editar Perfil</a>
                <hr style="margin: 0;">
                <form method="POST" action="{{ route('logout') }}" style="margin: 0;">
                    @csrf
                    <button type="submit" class="dropdown_item" style="width: 100%; background: none; border: none; text-align: left;">Cerrar Sesión</button>
                </form>
            </div>
        </div>
    </div>
</header>

<div class="container_main">
    <!-- Welcome Section -->
    <div class="welcome_section">
        <h1 class="welcome_title">¡Bienvenido, {{ $paciente->nombre }}!</h1>
        <p class="welcome_subtitle">Tu panel de salud personal - Documento: {{ $paciente->tipo_doc }} {{ $paciente->numero_doc }}</p>
    </div>

    <!-- Stats Cards -->
    <div class="stats_container">
        <div class="stat_card primary">
            <div class="stat_number">{{ $agendamiento->count() }}</div>
            <div class="stat_label">Mis Citas Médicas</div>
        </div>
        <div class="stat_card success">
            <div class="stat_number">{{ $historia->count() }}</div>
            <div class="stat_label">historia Clínicos</div>
        </div>
    </div>

    <!-- Content Grid -->
    <div class="content_grid">
        <!-- Mis agendamiento -->
        <div class="card">
            <div class="card_header">
                <h5 class="card_title">Mis agendamiento</h5>
                <div style="display: flex; align-items: center; gap: 0.5rem;">
                    <span class="badge">{{ $agendamiento->count() }} citas</span>
                    <button class="btn btn_primary" onclick="openAgendarModal()" style="padding: 0.4rem 0.8rem; font-size: 0.8rem;">
                        + Agendar Cita
                    </button>
                </div>
            </div>
            <div class="card_body">
                @if($agendamiento->count() > 0)
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Hora</th>
                                <th>Médico</th>
                                <th>Tipo</th>
                                <th>Estado</th>
                            </tr>
                        </thead>
<tbody>
    @foreach($agendamiento as $agendamiento)
    <tr>
        <td>{{ \Carbon\Carbon::parse($agendamiento->fecha)->format('d/m/Y') }}</td>
        <td>{{ \Carbon\Carbon::parse($agendamiento->hora)->format('H:i') }}</td>
        <td>
            <strong>Dr. {{ $agendamiento->medico->nombre }} {{ $agendamiento->medico->apellido }}</strong>
            <br><small style="color: #6b7280;">{{ $agendamiento->medico->especialidad }}</small>
        </td>
        <td>{{ $agendamiento->cita }}</td>
        <td>
            @php
                $fechaCita = \Carbon\Carbon::parse($agendamiento->fecha . ' ' . $agendamiento->hora);
                $ahora = \Carbon\Carbon::now();
            @endphp
            @if($fechaCita->isFuture())
                <span class="status_badge warning">Programada</span>
                <div style="margin-top: 0.5rem; display: flex; gap: 0.3rem;">
                    <button class="btn btn_outline_primary" onclick="reprogramarCita({{ $agendamiento->id_agendamiento }});" style="padding: 0.2rem 0.5rem; font-size: 0.7rem;">
                        Reprogramar
                    </button>
                    <button class="btn" onclick="cancelarCita({{ $agendamiento->id_agendamiento }})" style="padding: 0.2rem 0.5rem; font-size: 0.7rem; background: #dc2626; color: white;">
                        Cancelar
                    </button>
                </div>
            @elseif($fechaCita->isToday())
                <span class="status_badge info">Hoy</span>
            @else
                <span class="status_badge secondary">Completada</span>
            @endif
        </td>
    </tr>

    <!-- Modales para Reprogramar Citas -->
    @php
        $fechaCita = \Carbon\Carbon::parse($agendamiento->fecha . ' ' . $agendamiento->hora);
    @endphp
    @if($fechaCita->isFuture())
    <div class="modal" id="reprogramarModal{{ $agendamiento->id_agendamiento }}">
    <div class="modal_content">
        <div class="modal_header">
            <h5>Reprogramar Cita</h5>
            <button class="close" onclick="closeModal('reprogramarModal{{ $agendamiento->id_agendamiento }}')">&times;</button>
        </div>
        
        <form method="POST" action="{{ route('agendamieto.update', $agendamiento->id_agendamiento) }}">
            @csrf
            @method('PUT')
            <input type="hidden" name="id_paciente" value="{{ $agendamiento->id_paciente }}">
            <input type="hidden" name="id_medico" value="{{ $agendamiento->id_medico }}">
            
            <div class="modal_body">
                <div class="info_item" style="margin-bottom: 1rem; background: #f8fafc; padding: 1rem; border-radius: 0.5rem;">
                    <strong>Cita actual:</strong>
                    <p>{{ $agendamiento->cita }} - Dr. {{ $agendamiento->medico->nombre }} {{ $agendamiento->medico->apellido }}</p>
                    <p>{{ \Carbon\Carbon::parse($agendamiento->fecha)->format('d/m/Y') }} a las {{ \Carbon\Carbon::parse($agendamiento->hora)->format('H:i') }}</p>
                </div>
                
                <div class="form_group">
                    <label class="form_label" for="cita{{ $agendamiento->id_agendamiento }}">Tipo de Cita:</label>
                    <select id="cita{{ $agendamiento->id_agendamiento }}" name="cita" class="form_select" required>
                        <option value="">- Selecciona -</option>
                        <option value="Consulta general" {{ $agendamiento->cita == 'Consulta general' ? 'selected' : '' }}>Consulta General</option>
                        <option value="Ortopedia" {{ $agendamiento->cita == 'Ortopedia' ? 'selected' : '' }}>Ortopedia</option>
                        <option value="Psicologia" {{ $agendamiento->cita == 'Psicologia' ? 'selected' : '' }}>Psicologia</option>
                        <option value="Cardiologia" {{ $agendamiento->cita == 'Cardiologia' ? 'selected' : '' }}>Cardiología</option>
                        <option value="Pediatria" {{ $agendamiento->cita == 'Pediatria' ? 'selected' : '' }}>Pediatria</option>
                        <option value="Dermatología" {{ $agendamiento->cita == 'Dermatología' ? 'selected' : '' }}>Dermatología</option>
                    </select>
                </div>
                
                <div class="form_group">
                    <label class="form_label" for="fecha{{ $agendamiento->id_agendamiento }}">Nueva Fecha:</label>
                    <input type="date" id="fecha{{ $agendamiento->id_agendamiento }}" name="fecha" class="form_input" value="{{ $agendamiento->fecha }}" required min="{{ date('Y-m-d') }}">
                </div>
                
                <div class="form_group">
                    <label class="form_label" for="hora{{ $agendamiento->id_agendamiento }}">Nueva Hora:</label>
                    <select id="hora{{ $agendamiento->id_agendamiento }}" name="hora" class="form_select" required>
                        <option value="08:00" {{ $agendamiento->hora == '08:00:00' ? 'selected' : '' }}>08:00 AM</option>
                        <option value="08:30" {{ $agendamiento->hora == '08:30:00' ? 'selected' : '' }}>08:30 AM</option>
                        <option value="09:00" {{ $agendamiento->hora == '09:00:00' ? 'selected' : '' }}>09:00 AM</option>
                        <option value="09:30" {{ $agendamiento->hora == '09:30:00' ? 'selected' : '' }}>09:30 AM</option>
                        <option value="10:00" {{ $agendamiento->hora == '10:00:00' ? 'selected' : '' }}>10:00 AM</option>
                        <option value="10:30" {{ $agendamiento->hora == '10:30:00' ? 'selected' : '' }}>10:30 AM</option>
                        <option value="11:00" {{ $agendamiento->hora == '11:00:00' ? 'selected' : '' }}>11:00 AM</option>
                        <option value="11:30" {{ $agendamiento->hora == '11:30:00' ? 'selected' : '' }}>11:30 AM</option>
                        <option value="14:00" {{ $agendamiento->hora == '14:00:00' ? 'selected' : '' }}>02:00 PM</option>
                        <option value="14:30" {{ $agendamiento->hora == '14:30:00' ? 'selected' : '' }}>02:30 PM</option>
                        <option value="15:00" {{ $agendamiento->hora == '15:00:00' ? 'selected' : '' }}>03:00 PM</option>
                        <option value="15:30" {{ $agendamiento->hora == '15:30:00' ? 'selected' : '' }}>03:30 PM</option>
                        <option value="16:00" {{ $agendamiento->hora == '16:00:00' ? 'selected' : '' }}>04:00 PM</option>
                        <option value="16:30" {{ $agendamiento->hora == '16:30:00' ? 'selected' : '' }}>04:30 PM</option>
                        <option value="17:00" {{ $agendamiento->hora == '17:00:00' ? 'selected' : '' }}>05:00 PM</option>
                        <option value="17:30" {{ $agendamiento->hora == '17:30:00' ? 'selected' : '' }}>05:30 PM</option>
                    </select>
                </div>
            </div>
            
            <div class="modal_footer">
                <button type="submit" class="btn btn_primary">Reprogramar Cita</button>
                <button type="button" class="btn" onclick="closeModal('reprogramarModal{{ $agendamiento->id_agendamiento }}')" style="background:#6b7280;color:white;">Cancelar</button>
            </div>
        </form>
    </div>
</div>
    @endif
    @endforeach
</tbody>
                    </table>
                @else
                    <div class="empty_state">
                        <div class="empty_icon">📅</div>
                        <p>No tienes agendamieto registrados</p>
                    </div>
                @endif
            </div>

        </div>

        <!-- Mi historia Clínico -->
        <div class="card">
            <div class="card_header">
                <h5 class="card_title">Mi historia Clínico</h5>
                <span class="badge">{{ $historia->count() }} registros</span>
            </div>
            <div class="card_body">
                @if($historia->count() > 0)
                    <div class="history_card">
                        <div class="history_date">
                            {{ \Carbon\Carbon::parse($historia->fecha_creacion)->format('d/m/Y') }}
                        </div>
                        <div class="history_doctor">
                            Dr. {{ $historia->medico->nombre ?? 'N/A' }} {{ $historia->medico->apellido ?? '' }}
                        </div>
                        
                        <div style="margin-bottom: 1rem;">
                            <strong>Diagnóstico:</strong>
                            <p style="margin: 0.5rem 0; color: #374151;">{{ Str::limit($historia->diagnostico, 100) }}</p>
                        </div>
                        
                        @if($historia->antecedentes)
                        <div style="margin-bottom: 1rem;">
                            <strong>Antecedentes:</strong>
                            <p style="margin: 0.5rem 0; color: #6b7280;">{{ Str::limit($historia->antecedentes, 80) }}</p>
                        </div>
                        @endif

                        <button class="btn btn_outline_primary" onclick="openhistoriaModal({{ $historia->id_historia }})">
                            Ver detalles completos
                        </button>
                    </div>
                    @endforeach
                @else
                    <div class="empty_state">
                        <div class="empty_icon">📋</div>
                        <p>No tienes historia clínicos registrados</p>
                    </div>
                @endif
            </div>
        </div>
    </div>

    <!-- Información Personal -->
    <div class="card">
        <div class="card_header">
            <h5 class="card_title">Mi Información Personal</h5>
        </div>
        <div class="card_body">
            <div class="info_grid">
                <div class="info_item">
                    <span class="info_label">Nombre Completo:</span>
                    <p>{{ $paciente->nombre }} {{ $paciente->apellido }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Documento:</span>
                    <p>{{ $paciente->tipo_doc }} {{ $paciente->numero_doc }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Género:</span>
                    <p>{{ $paciente->genero }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Fecha de Nacimiento:</span>
                    <p>{{ \Carbon\Carbon::parse($paciente->fecha_nacimiento)->format('d/m/Y') }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Tipo de Sangre:</span>
                    <p>{{ $paciente->tipo_sangre ?? 'No especificado' }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Teléfono:</span>
                    <p>{{ $paciente->telefono ?? 'No especificado' }}</p>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Modales -->
<!-- Modal Ver Perfil -->
<div class="modal" id="profileModal">
    <div class="modal_content">
        <div class="modal_header">
            <h5>Mi Perfil Completo</h5>
            <button class="close" onclick="closeModal('profileModal')">&times;</button>
        </div>
        <div class="modal_body">
            <div class="info_grid">
                <div class="info_item">
                    <span class="info_label">Nombre:</span>
                    <p>{{ $paciente->nombre }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Apellido:</span>
                    <p>{{ $paciente->apellido }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Tipo de Documento:</span>
                    <p>{{ $paciente->tipo_doc }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Número de Documento:</span>
                    <p>{{ $paciente->numero_doc }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Género:</span>
                    <p>{{ $paciente->genero }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Fecha de Nacimiento:</span>
                    <p>{{ \Carbon\Carbon::parse($paciente->fecha_nacimiento)->format('d/m/Y') }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Tipo de Sangre:</span>
                    <p>{{ $paciente->tipo_sangre ?? 'No especificado' }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Dirección:</span>
                    <p>{{ $paciente->direccion ?? 'No especificada' }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Teléfono:</span>
                    <p>{{ $paciente->telefono ?? 'No especificado' }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Correo:</span>
                    <p>{{ $paciente->correo }}</p>
                </div>
            </div>
        </div>
        <div class="modal_footer">
            <button class="btn btn_primary" onclick="openEditModal(); closeModal('profileModal');">Editar Información</button>
            <button class="btn" onclick="closeModal('profileModal')" style="background: #6b7280; color: white;">Cerrar</button>
        </div>
    </div>
</div>

<!-- Modal Editar Perfil -->
<div class="modal" id="editModal">
    <div class="modal_content">
        <div class="modal_header">
            <h5>Editar Mi Información</h5>
            <button class="close" onclick="closeModal('editModal')">&times;</button>
        </div>

        <form method="POST" action="{{ route('paciente.perfil.update') }}">
    @csrf
    @method('PUT')
            <div class="modal_body">
                <div class="info_grid">
                    <!-- Nombre -->
                    <div class="info_item">
                        <label class="info_label" for="nombre">Nombre:</label>
                        <input type="text" id="nombre" name="nombre" value="{{ $paciente->nombre }}" required style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                    </div>

                    <!-- Apellido -->
                    <div class="info_item">
                        <label class="info_label" for="apellido">Apellido:</label>
                        <input type="text" id="apellido" name="apellido" value="{{ $paciente->apellido }}" required style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                    </div>

                    <!-- Tipo de documento -->
                    <div class="info_item">
                        <label class="info_label" for="tipo_doc">Tipo de Documento:</label>
                        <select id="tipo_doc" name="tipo_doc" required style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                            <option value="CC" {{ $paciente->tipo_doc == 'CC' ? 'selected' : '' }}>Cédula de Ciudadanía</option>
                            <option value="TI" {{ $paciente->tipo_doc == 'TI' ? 'selected' : '' }}>Tarjeta de Identidad</option>
                            <option value="CE" {{ $paciente->tipo_doc == 'CE' ? 'selected' : '' }}>Cédula de Extranjería</option>
                            <option value="RC" {{ $paciente->tipo_doc == 'RC' ? 'selected' : '' }}>Registro Civil</option>
                        </select>
                    </div>

                    <!-- Número documento -->
                    <div class="info_item">
                        <label class="info_label" for="numero_doc">Número de Documento:</label>
                        <input type="text" id="numero_doc" name="numero_doc" value="{{ $paciente->numero_doc }}" required style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                    </div>

                    <!-- GÉNERO: OJO, aquí usamos los valores cortos que la BD espera -->
                    <div class="info_item">
                        <label class="info_label" for="genero">Género:</label>
                        <select id="genero" name="genero" required style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                            <option value="M" {{ $paciente->genero == 'M' ? 'selected' : '' }}>Masculino</option>
                            <option value="F" {{ $paciente->genero == 'F' ? 'selected' : '' }}>Femenino</option>
                            <option value="Otro" {{ $paciente->genero == 'Otro' ? 'selected' : '' }}>Otro</option>
                        </select>
                    </div>

                    <!-- Resto de campos (fecha, sangre, direccion, telefono, correo) -->
                    <div class="info_item">
                        <label class="info_label" for="fecha_nacimiento">Fecha de Nacimiento:</label>
                        <input type="date" id="fecha_nacimiento" name="fecha_nacimiento" value="{{ $paciente->fecha_nacimiento }}" required style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                    </div>

                    <div class="info_item">
                        <label class="info_label" for="tipo_sangre">Tipo de Sangre:</label>
                        <select id="tipo_sangre" name="tipo_sangre" style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                            <option value="">Seleccionar...</option>
                            <option value="A+" {{ $paciente->tipo_sangre == 'A+' ? 'selected' : '' }}>A+</option>
                            <option value="A-" {{ $paciente->tipo_sangre == 'A-' ? 'selected' : '' }}>A-</option>
                            <option value="B+" {{ $paciente->tipo_sangre == 'B+' ? 'selected' : '' }}>B+</option>
                            <option value="B-" {{ $paciente->tipo_sangre == 'B-' ? 'selected' : '' }}>B-</option>
                            <option value="AB+" {{ $paciente->tipo_sangre == 'AB+' ? 'selected' : '' }}>AB+</option>
                            <option value="AB-" {{ $paciente->tipo_sangre == 'AB-' ? 'selected' : '' }}>AB-</option>
                            <option value="O+" {{ $paciente->tipo_sangre == 'O+' ? 'selected' : '' }}>O+</option>
                            <option value="O-" {{ $paciente->tipo_sangre == 'O-' ? 'selected' : '' }}>O-</option>
                        </select>
                    </div>

                    <div class="info_item">
                        <label class="info_label" for="direccion">Dirección:</label>
                        <input type="text" id="direccion" name="direccion" value="{{ $paciente->direccion }}" style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                    </div>

                    <div class="info_item">
                        <label class="info_label" for="telefono">Teléfono:</label>
                        <input type="text" id="telefono" name="telefono" value="{{ $paciente->telefono }}" style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                    </div>

                    <div class="info_item">
                        <label class="info_label" for="correo">Correo:</label>
                        <input type="email" id="correo" name="correo" value="{{ $paciente->correo }}" required style="width:100%;padding:.5rem;border:1px solid #d1d5db;border-radius:.375rem;">
                    </div>
                </div>
            </div>

            <div class="modal_footer">
                <button type="submit" class="btn btn_primary">Guardar Cambios</button>
                <button type="button" class="btn" onclick="closeModal('editModal')" style="background:#6b7280;color:white;">Cancelar</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal Agendar Cita - Estilo CRUD Simple -->
<div class="modal" id="agendarModal">
    <div class="modal_content">
        <div class="modal_header">
            <h5>Agendar Nueva Cita</h5>
            <button class="close" onclick="closeModal('agendarModal')">&times;</button>
        </div>

        <form method="POST" action="{{ route('agendamieto.store') }}">
            @csrf
            <!-- Campo oculto para el ID del paciente -->
            <input type="hidden" name="id_paciente" value="{{ $paciente->id_paciente }}">
            
            <div class="modal_body">
                <!-- Tipo de Cita -->
                <div class="form_group">
                    <label class="form_label" for="cita">Tipo de Cita:</label>
                    <select id="cita" name="cita" class="form_select" required>
                        <option value="">- Selecciona -</option>
                        <option value="Consulta general">Consulta General</option>
                        <option value="Ortopedia">Ortopedia</option>
                        <option value="Psicologia">Psicologia</option>
                        <option value="Cardiologia">Cardiología</option>
                        <option value="Pediatria">Pediatria</option>
                        <option value="Dermatología">Dermatología</option>
                    </select>
                </div>

                <!-- Fecha -->
                <div class="form_group">
                    <label class="form_label" for="fecha">Fecha:</label>
                    <input type="date" id="fecha" name="fecha" class="form_input" required min="{{ date('Y-m-d') }}">
                </div>

                <!-- Hora -->
                <div class="form_group">
                    <label class="form_label" for="hora">Hora:</label>
                    <select id="hora" name="hora" class="form_select" required>
                        <option value="">- Selecciona -</option>
                        <option value="08:00">08:00 AM</option>
                        <option value="08:30">08:30 AM</option>
                        <option value="09:00">09:00 AM</option>
                        <option value="09:30">09:30 AM</option>
                        <option value="10:00">10:00 AM</option>
                        <option value="10:30">10:30 AM</option>
                        <option value="11:00">11:00 AM</option>
                        <option value="11:30">11:30 AM</option>
                        <option value="14:00">02:00 PM</option>
                        <option value="14:30">02:30 PM</option>
                        <option value="15:00">03:00 PM</option>
                        <option value="15:30">03:30 PM</option>
                        <option value="16:00">04:00 PM</option>
                        <option value="16:30">04:30 PM</option>
                        <option value="17:00">05:00 PM</option>
                        <option value="17:30">05:30 PM</option>
                    </select>
                </div>

                <!-- Médico -->
                <div class="form_group">
                    <label class="form_label" for="id_medico">Médico:</label>
                    <select id="id_medico" name="id_medico" class="form_select" required>
                        <option value="">- Selecciona -</option>
                        @foreach($medicos as $medico)
                            <option value="{{ $medico->id_medico }}">Dr. {{ $medico->nombre }} {{ $medico->apellido }} - {{ $medico->especialidad }}</option>
                        @endforeach
                    </select>
                </div>
            </div>

            <div class="modal_footer">
                <button type="submit" class="btn btn_primary">Agendar Cita</button>
                <button type="button" class="btn" onclick="closeModal('agendarModal')" style="background:#6b7280;color:white;">Cancelar</button>
            </div>
        </form>
    </div>
</div>

<!-- Modales para historia Clínico -->
<div class="modal" id="historiaModal{{ $historia->id_historia }}">
    <div class="modal_content">
        <div class="modal_header">
            <h5>historia Clínico - {{ \Carbon\Carbon::parse($historia->fecha_creacion)->format('d/m/Y') }}</h5>
            <button class="close" onclick="closeModal('historiaModal{{ $historia->id_historia }}')">&times;</button>
        </div>
        <div class="modal_body">
            <div class="info_grid">
                <div class="info_item">
                    <span class="info_label">Fecha:</span>
                    <p>{{ \Carbon\Carbon::parse($historia->fecha_creacion)->format('d/m/Y H:i') }}</p>
                </div>
                <div class="info_item">
                    <span class="info_label">Médico:</span>
                    <p>Dr. {{ $historia->medico->nombre ?? 'N/A' }} {{ $historia->medico->apellido ?? '' }}</p>
                </div>
            </div>
            <div class="info_item" style="margin-top: 1rem;">
                <span class="info_label">Diagnóstico:</span>
                <p style="background: #f8fafc; padding: 1rem; border-radius: 0.5rem; border-left: 4px solid #3b82f6;">{{ $historia->diagnostico }}</p>
            </div>
            @if($historia->antecedentes)
            <div class="info_item" style="margin-top: 1rem;">
                <span class="info_label">Antecedentes:</span>
                <p style="background: #f8fafc; padding: 1rem; border-radius: 0.5rem; border-left: 4px solid #059669;">{{ $historia->antecedentes }}</p>
            </div>
            @endif
        </div>
        <div class="modal_footer">
            <button class="btn" onclick="closeModal('historiaModal{{ $historia->id_historia }}')" style="background: #6b7280; color: white;">Cerrar</button>
        </div>
    </div>
</div>
@endforeach

<script>
    // Toggle dropdown
    function toggleDropdown() {
        document.getElementById("profileDropdown").classList.toggle("show");
    }

    // Close dropdown when clicking outside
    window.onclick = function(event) {
        if (!event.target.matches('.profile_button')) {
            var dropdowns = document.getElementsByClassName("dropdown_content");
            for (var i = 0; i < dropdowns.length; i++) {
                var openDropdown = dropdowns[i];
                if (openDropdown.classList.contains('show')) {
                    openDropdown.classList.remove('show');
                }
            }
        }

        // Close modal when clicking outside
        var modals = document.getElementsByClassName('modal');
        for (var i = 0; i < modals.length; i++) {
            if (event.target == modals[i]) {
                modals[i].classList.remove('show');
            }
        }
    }

    // Modal functions
    function openProfileModal() {
        document.getElementById('profileModal').classList.add('show');
        closeDropdown();
    }

    function openEditModal() {
        document.getElementById('editModal').classList.add('show');
        closeDropdown();
    }

    function openAgendarModal() {
        document.getElementById('agendarModal').classList.add('show');
    }

    function reprogramarCita(id) {
        document.getElementById('reprogramarModal' + id).classList.add('show');
    }

    function cancelarCita(id) {
        if (confirm('¿Estás seguro de que deseas cancelar esta cita? Esta acción no se puede deshacer.')) {
            // Crear formulario dinámicamente para eliminar la cita
            const form = document.createElement('form');
            form.method = 'POST';
            form.action = `/agendamieto/${id}`;
            form.style.display = 'none';
            
            // Token CSRF
            const csrfToken = document.createElement('input');
            csrfToken.type = 'hidden';
            csrfToken.name = '_token';
            csrfToken.value = '{{ csrf_token() }}';
            form.appendChild(csrfToken);
            
            // Method DELETE
            const methodField = document.createElement('input');
            methodField.type = 'hidden';
            methodField.name = '_method';
            methodField.value = 'DELETE';
            form.appendChild(methodField);
            
            document.body.appendChild(form);
            form.submit();
        }
    }

    function mostrarOpcionesCitas() {
        alert('Usa los botones "Reprogramar" y "Cancelar" en cada cita programada para gestionarlas.');
    }

    function openhistoriaModal(id) {
        document.getElementById('historiaModal' + id).classList.add('show');
    }

    function closeModal(modalId) {
        document.getElementById(modalId).classList.remove('show');
    }

    function closeDropdown() {
        document.getElementById("profileDropdown").classList.remove("show");
    }

    // Función para cargar médicos según especialidad
    function cargarMedicos() {
        const especialidad = document.getElementById('especialidad').value;
        const medicoSelect = document.getElementById('id_medico');
        
        if (!especialidad) {
            medicoSelect.disabled = true;
            medicoSelect.innerHTML = '<option value="">Primero selecciona una especialidad</option>';
            return;
        }

        // Mostrar loading
        medicoSelect.disabled = true;
        medicoSelect.innerHTML = '<option value="">Cargando médicos...</option>';

        // Hacer petición AJAX para obtener médicos de la especialidad
        fetch(`/api/medicos-por-especialidad/${encodeURIComponent(especialidad)}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error en la respuesta del servidor');
                }
                return response.json();
            })
            .then(data => {
                medicoSelect.disabled = false;
                medicoSelect.innerHTML = '<option value="">Seleccionar médico...</option>';
                
                if (data.length === 0) {
                    medicoSelect.innerHTML = '<option value="">No hay médicos disponibles para esta especialidad</option>';
                    return;
                }
                
                data.forEach(medico => {
                    const option = document.createElement('option');
                    option.value = medico.id_medico;
                    option.textContent = `Dr. ${medico.nombre} ${medico.apellido}`;
                    medicoSelect.appendChild(option);
                });
            })
            .catch(error => {
                console.error('Error:', error);
                medicoSelect.disabled = false;
                medicoSelect.innerHTML = '<option value="">Error al cargar médicos. Intenta de nuevo.</option>';
            });
    }

    // Validar fecha mínima (hoy)
    document.addEventListener('DOMContentLoaded', function() {
        const fechaInput = document.getElementById('fecha');
        if (fechaInput) {
            const hoy = new Date().toISOString().split('T')[0];
            fechaInput.min = hoy;
        }
    });
</script>

</body>
</html>