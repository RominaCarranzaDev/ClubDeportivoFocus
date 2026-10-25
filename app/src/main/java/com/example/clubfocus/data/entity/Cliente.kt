package com.example.clubfocus.data.entity

import java.time.LocalDate

open class Cliente(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val dni: String,
    val fechaNacimiento: LocalDate,
    val email: String,
    val aptoFisico: Boolean,
    val activo: Boolean,
    val esSocio: Boolean,
    val fechaInscripcion: LocalDate,
    val nroSocio: String
)

//DTO

data class SocioCuotaItem(
    val cuotaId: Int,
    val clienteId: Int,
    val dni: String,
    val nombreCompleto: String,
    val nroSocio: String,
    val fechaVencimiento: String?,
    val activo: Boolean,
    val estadoCuota: String
)
