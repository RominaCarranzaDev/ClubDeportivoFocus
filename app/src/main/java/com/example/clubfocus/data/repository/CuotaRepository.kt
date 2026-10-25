package com.example.clubfocus.data.repository

import com.example.clubfocus.data.entity.Cuota
import com.example.clubfocus.data.entity.EstadoCuota
import com.example.clubfocus.data.entity.MedioPago
import com.example.clubfocus.data.entity.Promocion
import com.example.clubfocus.data.entity.TipoCuota
import java.time.LocalDate

object CuotaRepository {


    // Monto automático
    private fun obtenerMonto(tipo: TipoCuota): Double {
        // Monto de Cuotas según tipo de cliente
        return when (tipo) {
            TipoCuota.MENSUAL -> 45000.0
            TipoCuota.DIARIA -> 20000.0
        }
    }

    // Cálculo automático del vencimiento
    private fun calcularFechaVencimiento(
        fecha: LocalDate,
        tipo: TipoCuota
    ): LocalDate {
        // Vencimiento según el tipo de cliente
        return when (tipo) {
            TipoCuota.MENSUAL -> fecha.plusMonths(1)
            TipoCuota.DIARIA -> fecha
        }
    }

    // Cálculo automático del monto final si corresponde o no promociones
    fun calcularMontoFinal(
        monto: Double,
        promocion: Promocion
    ): Double {

        return when (promocion) {
            Promocion.SIN_PROMOCION -> monto
            Promocion.TRES_CUOTAS -> monto * 0.85
            Promocion.SEIS_CUOTAS -> monto * 0.90
        }
    }

    fun aplicarPromocion(
        cuota: Cuota,
        promocion: Promocion
    ) {
        cuota.promocion = promocion

        cuota.montoFinal = calcularMontoFinal(cuota.monto, promocion)
    }

    // Devuelve la cuota lista para pasarla al Helper, como un DTO
    fun prepararCuotaInicial(
        clienteId: Int,
        esSocio: Boolean
    ): Cuota {

        val tipo = if (esSocio) {
            TipoCuota.MENSUAL
        } else {
            TipoCuota.DIARIA
        }

        val monto = obtenerMonto(tipo)
        val fechaActual = LocalDate.now()
        val vencimiento = calcularFechaVencimiento(fechaActual, tipo)

        return Cuota(
            id = 0, // Todavía no fue insertada en SQLite
            clienteId = clienteId,
            tipo = tipo,
            monto = monto,
            montoFinal = monto,
            fecha = null, // Aún no fue pagada
            fechaVencimiento = vencimiento,
            estado = EstadoCuota.PENDIENTE,
            medioPago = null,
            promocion = null
        )
    }
}






