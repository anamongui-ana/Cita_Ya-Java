<link rel="stylesheet" href="{{ asset('Assets/CSS/edit.css') }}">

@extends('layouts.app')

@section('content')
<div class="container">
    <h1>Editar Cita</h1>

    <form action="{{ route('agendamientos.update', $agendamiento->id_agendamiento) }}" method="POST">
        @csrf
        @method('PUT')

        <!-- Tipo de Cita -->
        <div class="mb-3">
            <label for="cita" class="form-label">Tipo de Cita</label>
            <select name="cita" class="form-select" required>
                <option value="">- Selecciona -</option>
                <option value="Consulta general" {{ old('cita', $agendamiento->cita) == 'Consulta general' ? 'selected' : '' }}>Consulta General</option>
                <option value="Ortopedia" {{ old('cita', $agendamiento->cita) == 'Ortopedia' ? 'selected' : '' }}>Ortopedia</option>
                <option value="Psicologia" {{ old('cita', $agendamiento->cita) == 'Psicologia' ? 'selected' : '' }}>Psicología</option>
                <option value="Cardiologia" {{ old('cita', $agendamiento->cita) == 'Cardiologia' ? 'selected' : '' }}>Cardiología</option>
                <option value="Pediatria" {{ old('cita', $agendamiento->cita) == 'Pediatria' ? 'selected' : '' }}>Pediatría</option>
                <option value="Dermatología" {{ old('cita', $agendamiento->cita) == 'Dermatología' ? 'selected' : '' }}>Dermatología</option>
            </select>
        </div>

        <!-- Fecha -->
        <div class="mb-3">
            <label for="fecha" class="form-label">Fecha</label>
            <input type="date" name="fecha" class="form-control" value="{{ old('fecha', $agendamiento->fecha) }}" required min="{{ date('Y-m-d') }}">
        </div>

        <!-- Hora -->
        <div class="mb-3">
            <label for="hora" class="form-label">Hora</label>
            <select name="hora" class="form-select" required>
                <option value="">- Selecciona -</option>
                @foreach(['08:00','08:30','09:00','09:30','10:00','10:30','11:00','11:30','14:00','14:30','15:00','15:30','16:00','16:30','17:00','17:30'] as $hora)
                    <option value="{{ $hora }}" {{ old('hora', $agendamiento->hora) == $hora ? 'selected' : '' }}>
                        {{ \Carbon\Carbon::createFromFormat('H:i',$hora)->format('h:i A') }}
                    </option>
                @endforeach
            </select>
        </div>

        <!-- Paciente -->
        <div class="mb-3">
            <label for="id_paciente" class="form-label">Paciente</label>
            <select name="id_paciente" class="form-select" required>
                <option value="">- Selecciona Paciente -</option>
                @foreach($pacientes as $p)
                    <option value="{{ $p->id_paciente }}" {{ old('id_paciente', $agendamiento->id_paciente) == $p->id_paciente ? 'selected' : '' }}>
                        {{ $p->nombre }} {{ $p->apellido }}
                    </option>
                @endforeach
            </select>
        </div>

        <!-- Médico -->
        <div class="mb-3">
            <label for="id_medico" class="form-label">Médico</label>
            <select name="id_medico" class="form-select" required>
                <option value="">- Selecciona Médico -</option>
                @foreach($medicos as $m)
                    <option value="{{ $m->id_medico }}" {{ old('id_medico', $agendamiento->id_medico) == $m->id_medico ? 'selected' : '' }}>
                        {{ $m->nombre }} {{ $m->apellido }} ({{ $m->especialidad }})
                    </option>
                @endforeach
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Actualizar</button>
        <a href="{{ route('agendamientos.index') }}" class="btn btn-secondary">Cancelar</a>
    </form>
</div>
@endsection