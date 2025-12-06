<link rel="stylesheet" href="{{ asset('Assets/CSS/table.css') }}">

@extends('layouts.app')

@section('content')
<div class="container">
    <h1>Historiales Clínicos</h1>
    
    <div class="mb-3 d-flex gap-2">
        <a href="{{ route('historiales.create') }}" class="btn-new">Nuevo Historial</a>
        <a href="{{ route('historiales.reporte.pdf', request()->all()) }}" target="_blank" class="btn-report">
            Generar Reporte PDF{{ request()->anyFilled(['buscar', 'fecha_desde', 'fecha_hasta', 'id_paciente', 'id_medico']) ? ' (Filtrado)' : '' }}
        </a>
    </div>

    <!-- FORMULARIO DE FILTROS -->
    <div class="card mb-3">
        <div class="card-header">
            <strong>Filtros de Búsqueda</strong>
        </div>
        <div class="card-body">
            <form action="{{ route('historiales.index') }}" method="GET">
                <div class="row g-3">
                    <!-- Búsqueda General -->
                    <div class="col-md-4">
                        <label class="form-label">Búsqueda General</label>
                        <input type="text" name="buscar" class="form-control" 
                               placeholder="Paciente, médico, diagnóstico..." 
                               value="{{ request('buscar') }}">
                    </div>

                    <!-- Paciente -->
                    <div class="col-md-4">
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
                    <div class="col-md-4">
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
                        <a href="{{ route('historiales.index') }}" class="btn btn-secondary">
                            Limpiar Filtros
                        </a>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Información de resultados -->
    @if(request()->anyFilled(['buscar', 'fecha_desde', 'fecha_hasta', 'id_paciente', 'id_medico']))
    <div class="alert alert-info">
        <strong>Filtros activos:</strong> 
        Mostrando {{ $historiales->total() }} resultados
    </div>
    @endif

    <!-- TABLA DE HISTORIALES -->
    <table class="table mt-3">
        <thead>
            <tr>
                <th>ID</th>
                <th>Fecha</th>
                <th>Paciente</th>
                <th>Médico</th>
                <th>Diagnóstico</th>
                <th>Acciones</th>
            </tr>
        </thead>
        <tbody>
            @forelse ($historiales as $h)
            <tr>
                <td>{{ $h->id_historial }}</td>
                <td>{{ \Carbon\Carbon::parse($h->fecha_creacion)->format('d/m/Y') }}</td>
                <td>{{ $h->paciente->nombre }} {{ $h->paciente->apellido }}</td>
                <td>{{ $h->medico->nombre }} {{ $h->medico->apellido }}</td>
                <td>{{ Str::limit($h->diagnostico, 50) }}</td>
                <td>
                    <a href="{{ route('historiales.show', $h->id_historial) }}" class="btn btn-info btn-sm">Ver</a>
                    <a href="{{ route('historiales.edit', $h->id_historial) }}" class="btn btn-warning btn-sm">Editar</a>
                    <form action="{{ route('historiales.destroy', $h->id_historial) }}" method="POST" style="display:inline;">
                        @csrf @method('DELETE')
                        <button type="submit" class="btn btn-danger btn-sm" onclick="return confirm('¿Eliminar este historial?')">Eliminar</button>
                    </form>
                </td>
            </tr>
            @empty
            <tr>
                <td colspan="6" class="text-center">
                    <em>No se encontraron historiales con los filtros aplicados</em>
                </td>
            </tr>
            @endforelse
        </tbody>
    </table>

    <!-- PAGINACIÓN -->
    <div class="d-flex justify-content-center">
        @if(method_exists($historiales, 'links'))
            {{ $historiales->links() }}
        @endif
    </div>
</div>
@endsection