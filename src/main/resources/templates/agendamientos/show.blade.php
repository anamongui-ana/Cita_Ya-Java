    <link rel="stylesheet" href="{{ asset('Assets/CSS/show.css') }}">

    @extends('layouts.app')

    @section('content')
    <div class="container">
        <div class="card">
            <h1>Detalle de la Cita</h1>
            <div class="card-body">
                <p><strong>ID:</strong> {{ $agendamiento->id_agendamiento }}</p>
                <p><strong>Cita:</strong> {{ $agendamiento->cita }}</p>
                <p><strong>Fecha:</strong> {{ $agendamiento->fecha }}</p>
                <p><strong>Hora:</strong> {{ $agendamiento->hora }}</p>
                <p><strong>Paciente:</strong> {{ $agendamiento->paciente->nombre }} {{ $agendamiento->paciente->apellido }}</p>
                <p><strong>Médico:</strong> {{ $agendamiento->medico->nombre }} {{ $agendamiento->medico->apellido }} ({{ $agendamiento->medico->especialidad }})</p>
            </div>
            <div class="actions">
                <a href="{{ route('agendamientos.index') }}" class="btn btn-secondary mt-3">Volver</a>
                <a href="{{ route('agendamientos.edit', $agendamiento->id_agendamiento) }}" class="btn btn-warning mt-3">Editar</a>
            </div>
        </div>
    </div>
    @endsection