<link rel="stylesheet" href="{{ asset('Assets/CSS/edit.css') }}">

@extends('layouts.app')

@section('content')
<div class="container">
    <h1>Editar Historial Clínico</h1>

    <form action="{{ route('historiales.update', $historial->id_historial) }}" method="POST">
        @csrf
        @method('PUT')

        <div class="mb-3">
            <label for="fecha_creacion" class="form-label">Fecha de Creación</label>
            <input type="date" name="fecha_creacion" class="form-control" value="{{ old('fecha_creacion', $historial->fecha_creacion) }}" required>
        </div>

        <div class="mb-3">
            <label for="antecedentes" class="form-label">Antecedentes</label>
            <textarea name="antecedentes" class="form-control">{{ old('antecedentes', $historial->antecedentes) }}</textarea>
        </div>

        <div class="mb-3">
            <label for="diagnostico" class="form-label">Diagnóstico</label>
            <textarea name="diagnostico" class="form-control">{{ old('diagnostico', $historial->diagnostico) }}</textarea>
        </div>

        <div class="mb-3">
            <label for="id_paciente" class="form-label">Paciente</label>
            <select name="id_paciente" class="form-select" required>
                <option value="">-- Selecciona Paciente --</option>
                @foreach($pacientes as $p)
                    <option value="{{ $p->id_paciente }}" {{ $historial->id_paciente == $p->id_paciente ? 'selected' : '' }}>
                        {{ $p->nombre }} {{ $p->apellido }}
                    </option>
                @endforeach
            </select>
        </div>

        <div class="mb-3">
            <label for="id_medico" class="form-label">Médico</label>
            <select name="id_medico" class="form-select" required>
                <option value="">-- Selecciona Médico --</option>
                @foreach($medicos as $m)
                    <option value="{{ $m->id_medico }}" {{ $historial->id_medico == $m->id_medico ? 'selected' : '' }}>
                        {{ $m->nombre }} {{ $m->apellido }} ({{ $m->especialidad }})
                    </option>
                @endforeach
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Actualizar</button>
        <a href="{{ route('historiales.index') }}" class="btn btn-secondary">Cancelar</a>
    </form>
</div>
@endsection