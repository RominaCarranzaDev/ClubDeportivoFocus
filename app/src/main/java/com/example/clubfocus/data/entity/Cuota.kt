package com.example.clubfocus.data.entity

import java.time.LocalDate

data class Cuota(
    val id: Int,
    val tipo: TipoCuota,
    val monto: Double,
    val fecha: LocalDate,
    val fechaVencimiento: LocalDate,
    val estado: EstadoCuota
)

enum class TipoCuota {
    MENSUAL,
    DIARIA
}

enum class EstadoCuota {
    PAGADA,
    PENDIENTE,
    VENCIDA
}