package com.example.clubfocus.data.repository

import com.example.clubfocus.data.entity.Cuota
import com.example.clubfocus.data.entity.EstadoCuota
import com.example.clubfocus.data.entity.MedioPago
import com.example.clubfocus.data.entity.Promocion
import com.example.clubfocus.data.entity.TipoCuota
import java.time.LocalDate

object CuotaRepository {

    private var siguienteId = 1

    private val cuotas = mutableListOf<Cuota>()

    fun agregarCuota(
        clienteId: Int,
        tipo: TipoCuota,
        estado: EstadoCuota,
        medioPago: MedioPago,
        promocion: Promocion
    ) {

        val monto = obtenerMonto(tipo)

        val montoFinal = calcularMontoFinal(
            monto,
            promocion
        )
        val fecha = LocalDate.now()

        val fechaVencimiento = calcularFechaVencimiento(
            fecha,
            tipo
        )


        val cuota = Cuota(
            id = siguienteId,
            clienteId = clienteId,
            tipo = tipo,
            monto = monto,
            montoFinal = montoFinal,
            fecha = fecha,
            fechaVencimiento = fechaVencimiento,
            estado = estado,
            medioPago = medioPago,
            promocion = promocion
        )

        cuotas.add(cuota)
        siguienteId++
    }

    fun crearCuotaInicial(
        clienteId: Int,
        tipo: TipoCuota
    ): Cuota {

        val monto = obtenerMonto(tipo)

        val fecha = LocalDate.now()

        val fechaVencimiento = calcularFechaVencimiento(
            fecha,
            tipo
        )

        val cuota = Cuota(
            id = siguienteId,
            clienteId = clienteId,
            tipo = tipo,
            monto = monto,
            montoFinal = monto,
            fecha = fecha,
            fechaVencimiento = fechaVencimiento,
            estado = EstadoCuota.PENDIENTE,
            medioPago = null,
            promocion = null
        )

        siguienteId++

        return cuota
    }



    private fun obtenerMonto(tipo: TipoCuota): Double {

        return when (tipo) {
            TipoCuota.MENSUAL -> 45000.0
            TipoCuota.DIARIA -> 20000.0
        }
    }

    private fun calcularFechaVencimiento(
        fecha: LocalDate,
        tipo: TipoCuota
    ): LocalDate {

        return when (tipo) {
            TipoCuota.MENSUAL -> fecha.plusMonths(1)
            TipoCuota.DIARIA -> fecha.plusDays(1)
        }
    }

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
        promocion: Promocion?
    ) {
        cuota.promocion = promocion

        cuota.montoFinal = when (promocion) {
            null -> cuota.monto
            Promocion.SIN_PROMOCION -> cuota.monto
            Promocion.TRES_CUOTAS -> cuota.monto * 0.85
            Promocion.SEIS_CUOTAS -> cuota.monto * 0.90
        }
    }

    fun buscarPorId(id: Int): Cuota? {
        return cuotas.find { it.id == id }
    }

    fun buscarPorCliente(clienteId: Int): List<Cuota> {
        return cuotas.filter {
            it.clienteId == clienteId
        }
    }

    fun obtenerTodas(): List<Cuota> {
        return cuotas
    }

}






