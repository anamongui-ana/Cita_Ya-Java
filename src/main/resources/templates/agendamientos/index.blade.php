<link rel="stylesheet" href="{{ asset('Assets/CSS/table.css') }}">

@extends('layouts.app')

@section('content')
<div class="container">
    <h1>Agendamientos</h1>
    
    <div class="mb-3 d-flex gap-2">
        <a href="{{ route('agendamientos.create') }}" class="btn-new">Nueva Cita</a>
        <a href="{{ route('agendamientos.reporte.pdf', request()->all()) }}" target="_blank" class="btn-report">
            Generar Reporte PDF{{ request()->anyFilled(['buscar', 'cita', 'fecha_desde', 'fecha_hasta', 'id_paciente', 'id_medico']) ? ' (Filtrado)' : '' }}
        </a>
    </div>

    <!-- FORMULARIO DE FILTROS -->
    <div class="card mb-3">
        <div class="card-header">
            <strong>Filtros de Búsqueda</strong>
        </div>
        <div class="card-body">
            <form action="{{ route('agendamientos.index') }}" method="GET">
                <div class="row g-3">
                    <!-- Búsqueda General -->
                    <div class="col-md-3">
                        <label class="form-label">Búsqueda General</label>
                        <input type="text" name="buscar" class="form-control" 
                               placeholder="Paciente o médico..." 
                               value="{{ request('buscar') }}">
                    </div>

                    <!-- Tipo de Cita -->
                    <div class="col-md-3">
                        <label class="form-label">Tipo de Cita</label>
                        <select name="cita" class="form-select">
                            <option value="">Todas</option>
                            <option value="Consulta general" {{ request('cita') == 'Consulta general' ? 'selected' : '' }}>Consulta General</option>
                            <option value="Ortopedia" {{ request('cita') == 'Ortopedia' ? 'selected' : '' }}>Ortopedia</option>
                            <option value="Psicologia" {{ request('cita') == 'Psicologia' ? 'selected' : '' }}>Psicología</option>
                            <option value="Cardiologia" {{ request('cita') == 'Cardiologia' ? 'selected' : '' }}>Cardiología</option>
                            <option value="Pediatria" {{ request('cita') == 'Pediatria' ? 'selected' : '' }}>Pediatría</option>
                            <option value="Dermatología" {{ request('cita') == 'Dermatología' ? 'selected' : '' }}>Dermatología</option>
                        </select>
                    </div>

                    <!-- Paciente -->
                    <div class="col-md-3">
                        <label class="form-label">Paciente</label>
                        <select name="id_paciente" class="form-select">
                            <option value="">Todos</option>
                            @foreach($pacientes as $p)
                                <option value="{{ $p->id_paciente }}" {{ request('id_paciente') == $p->id_paciente ? 'selected' : '' }}>
                                    {{ $p->nombre }} {{ $p->apellido }}
                                </option>
                            @endforeach
                        </select>
                    </div>

                    <!-- Médico -->
                    <div class="col-md-3">
                        <label class="form-label">Médico</label>
                        <select name="id_medico" class="form-select">
                            <option value="">Todos</option>
                            @foreach($medicos as $m)
                                <option value="{{ $m->id_medico }}" {{ request('id_medico') == $m->id_medico ? 'selected' : '' }}>
                                    {{ $m->nombre }} {{ $m->apellido }}
                                </option>
                            @endforeach
                        </select>
                    </div>

                    <!-- Fecha Desde -->
                    <div class="col-md-3">
                        <label class="form-label">Fecha Desde</label>
                        <input type="date" name="fecha_desde" class="form-control" 
                               value="{{ request('fecha_desde') }}">
                    </div>

                    <!-- Fecha Hasta -->
                    <div class="col-md-3">
                        <label class="form-label">Fecha Hasta</label>
                        <input type="date" name="fecha_hasta" class="form-control" 
                               value="{{ request('fecha_hasta') }}">
                    </div>

                    <!-- Ordenamiento -->
                    <div class="col-md-2">
                        <label class="form-label">Orden</label>
                        <select name="orden" class="form-select">
                            <option value="desc" {{ request('orden') == 'desc' ? 'selected' : '' }}>Más recientes</option>
                            <option value="asc" {{ request('orden') == 'asc' ? 'selected' : '' }}>Más antiguos</option>
                        </select>
                    </div>

                    <!-- Botones -->
                    <div class="col-md-4 d-flex align-items-end gap-2">
                        <button type="submit" class="btn btn-primary">
                            Filtrar
                        </button>
                        <a href="{{ route('agendamientos.index') }}" class="btn btn-secondary">
                            Limpiar Filtros
                        </a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Información de resultados -->
    @if(request()->anyFilled(['buscar', 'cita', 'fecha_desde', 'fecha_hasta', 'id_paciente', 'id_medico']))
    <div class="alert alert-info">
        <strong>Filtros activos:</strong> 
        Mostrando {{ $agendamientos->total() }} resultados
    </div>
    @endif

    <!-- TABLA DE AGENDAMIENTOS -->
    <table class="table mt-3">
        <thead>
            <tr>
                <th>ID</th>
                <th>Cita</th>
                <th>Fecha</th>
                <th>Hora</th>
                <th>Paciente</th>
                <th>Médico</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            @forelse ($agendamientos as $a)
            <tr>
                <td>{{ $a->id_agendamiento }}</td>
                <td>{{ $a->cita }}</td>
                <td>{{ \Carbon\Carbon::parse($a->fecha)->format('d/m/Y') }}</td>
                <td>{{ \Carbon\Carbon::parse($a->hora)->format('h:i A') }}</td>
                <td>{{ $a->paciente->nombre }} {{ $a->paciente->apellido }}</td>
                <td>{{ $a->medico->nombre }} {{ $a->medico->apellido }}</td>
                <td>
                    <a href="{{ route('agendamientos.show', $a->id_agendamiento) }}" class="btn btn-info btn-sm">Ver</a>
                    <a href="{{ route('agendamientos.edit', $a->id_agendamiento) }}" class="btn btn-warning btn-sm">Editar</a>
                    <form action="{{ route('agendamientos.destroy', $a->id_agendamiento) }}" method="POST" style="display:inline;">
                        @csrf @method('DELETE')
                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Eliminar esta cita?')">Eliminar</button>
                    </form>
                </td>
            </tr>
            @empty
            <tr>
                <td colspan="7" class="text-center">
                    <em>No se encontraron citas con los filtros aplicados</em>
                </td>
            </tr>
            @endforelse
        </tbody>
    </table>

    <!-- PAGINACIÓN -->
    <div class="d-flex justify-content-center">
        @if(method_exists($agendamientos, 'links'))
            {{ $agendamientos->links() }}
        @endif
    </div>
</div>
@endsection