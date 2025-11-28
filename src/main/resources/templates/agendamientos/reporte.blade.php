<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Reporte de Agendamientos - Cita Ya</title>
    <link rel="stylesheet" href="{{ public_path('Assets/CSS/reporte.css') }}">
</head>
<body>
    <div class="content-wrap">
        <header>
            <img src="{{ public_path('Assets/IMG/logo.png') }}" alt="Logo Cita Ya">
            <div class="info">
                <br>
                <p>Sistema de Gestión de Citas Médicas</p>
            </div>
            <div class="fecha">
                <p><strong>Fecha:</strong> {{ now()->format('d/m/Y') }}</p>
            </div>
        </header>

        <h2>Reporte de Agendamientos</h2>

        <!-- FILTROS APLICADOS -->
        @if(isset($filtros) && (
            (isset($filtros['buscar']) && $filtros['buscar']) || 
            (isset($filtros['cita']) && $filtros['cita']) || 
            (isset($filtros['fecha_desde']) && $filtros['fecha_desde']) || 
            (isset($filtros['fecha_hasta']) && $filtros['fecha_hasta']) ||
            (isset($filtros['id_paciente']) && $filtros['id_paciente']) ||
            (isset($filtros['id_medico']) && $filtros['id_medico'])
        ))
        <div style="background-color: #f0f8ff; padding: 10px; margin-bottom: 15px; border-left: 4px solid #007bff; font-size: 10px;">
            <strong>Filtros Aplicados:</strong>
            @if(isset($filtros['buscar']) && $filtros['buscar'])
                <br>Búsqueda: {{ $filtros['buscar'] }}
            @endif
            @if(isset($filtros['cita']) && $filtros['cita'])
                <br>Tipo de Cita: {{ $filtros['cita'] }}
            @endif
            @if(isset($filtros['fecha_desde']) && $filtros['fecha_desde'])
                <br>Fecha Desde: {{ date('d/m/Y', strtotime($filtros['fecha_desde'])) }}
            @endif
            @if(isset($filtros['fecha_hasta']) && $filtros['fecha_hasta'])
                <br>Fecha Hasta: {{ date('d/m/Y', strtotime($filtros['fecha_hasta'])) }}
            @endif
            @if(isset($filtros['id_paciente']) && $filtros['id_paciente'])
                <br>Paciente Específico Filtrado
            @endif
            @if(isset($filtros['id_medico']) && $filtros['id_medico'])
                <br>Médico Específico Filtrado
            @endif
        </div>
        @endif

        <div class="tabla-container">
            @if($agendamientos->count() > 0)
            <table>
                <thead>
                    <tr>
                        <th style="width:5%;">ID</th>
                        <th style="width:10%;">Cita</th>
                        <th style="width:12%;">Fecha</th>
                        <th style="width:10%;">Hora</th>
                        <th style="width:20%;">Paciente</th>
                        <th style="width:25%;">Médico</th>
                        <th style="width:18%;">Especialidad</th>
                    </tr>
                </thead>
                <tbody>
                    @foreach ($agendamientos as $ag)
                        <tr>
                            <td>{{ $ag->id_agendamiento }}</td>
                            <td>{{ $ag->cita }}</td>
                            <td>{{ \Carbon\Carbon::parse($ag->fecha)->format('d/m/Y') }}</td>
                            <td>{{ \Carbon\Carbon::parse($ag->hora)->format('h:i A') }}</td>
                            <td>{{ $ag->paciente->nombre }} {{ $ag->paciente->apellido }}</td>
                            <td>{{ $ag->medico->nombre }} {{ $ag->medico->apellido }}</td>
                            <td>{{ $ag->medico->especialidad }}</td>
                        </tr>
                    @endforeach
                </tbody>
            </table>
            @else
            <p style="text-align: center; padding: 20px; color: #999;">No se encontraron agendamientos con los filtros aplicados</p>
            @endif
        </div>

        <div class="footer-stats">
            <div>Total registros: <strong>{{ $agendamientos->count() }}</strong></div>
            <div>Generado por: <strong>Cita Ya</strong></div>
        </div>
    </div>
</body>
</html>