<link rel="stylesheet" href="{{ asset('Assets/CSS/show.css') }}">

@extends('layouts.app')

@section('content')
<div class="container">
    <div class="card">
        <h1>Detalle del Historial Clínico</h1>
        <div class="card-body">
            <p><strong>ID:</strong> {{ $historial->id_historial }}</p>
            <p><strong>Fecha de Creación:</strong> {{ $historial->fecha_creacion }}</p>
            <p><strong>Antecedentes:</strong> {{ $historial->antecedentes }}</p>
            <p><strong>Diagnóstico:</strong> {{ $historial->diagnostico }}</p>
            <p><strong>Paciente:</strong> {{ $historial->paciente->nombre }} {{ $historial->paciente->apellido }}</p>
            <p><strong>Médico:</strong> {{ $historial->medico->nombre }} {{ $historial->medico->apellido }} ({{ $historial->medico->especialidad }})</p>
        </div>
        <div class="actions">
            <a href="{{ route('historiales.index') }}" class="btn btn-secondary mt-3">Volver</a>
            <a href="{{ route('historiales.edit', $historial->id_historial) }}" class="btn btn-warning mt-3">Editar</a>
        </div>
    </div>
</div>
@endsection