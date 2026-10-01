package com.example.clubfocus.data.entity

import java.time.LocalDate

data class Cuota(
    val id: Int,
    val clienteId: Int,
    val tipo: TipoCuota,
    val monto: Double,
    var montoFinal: Double,
    val fecha: LocalDate,
    val fechaVencimiento: LocalDate,
    var estado: EstadoCuota,
    var medioPago: MedioPago?,
    var promocion: Promocion?
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

enum class MedioPago {
    EFECTIVO,
    TARJETA_CREDITO,
    TARJETA_DEBITO
}

enum class Promocion {
    SIN_PROMOCION,
    TRES_CUOTAS,
    SEIS_CUOTAS
}