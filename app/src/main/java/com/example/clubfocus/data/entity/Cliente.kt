package com.example.clubfocus.data.entity

import java.time.LocalDate

data class Cliente(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val fechaNacimiento: LocalDate,
    val telefono: String,
    val email: String,
    val aptoFisico: Boolean,
    val activo: Boolean,
    val esSocio: Boolean,
    val fechaInscripcion: LocalDate,
    val cuotas: MutableList<Cuota>
)