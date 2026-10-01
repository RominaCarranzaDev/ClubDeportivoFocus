package com.example.clubfocus.data.entity

import java.time.LocalDate

data class Carnet(
    val id: Int,
    val nroSocio: String,
    val fechaEmision: LocalDate,
    val fechaVencimiento: LocalDate
)