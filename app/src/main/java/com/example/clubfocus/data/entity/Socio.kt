package com.example.clubfocus.data.entity

import java.time.LocalDate

class Socio(
    id: Int,
    nombre: String,
    apellido: String,
    dni: String,
    fechaNacimiento: LocalDate,
    telefono: String,
    email: String,
    aptoFisico: Boolean,
    activo: Boolean,
    fechaInscripcion: LocalDate,
    cuotas: MutableList<Cuota> = mutableListOf(),
    val nroSocio: String
) : Cliente(
    id,
    nombre,
    apellido,
    dni,
    fechaNacimiento,
    telefono,
    email,
    aptoFisico,
    activo,
    true,
    fechaInscripcion,
    cuotas
)