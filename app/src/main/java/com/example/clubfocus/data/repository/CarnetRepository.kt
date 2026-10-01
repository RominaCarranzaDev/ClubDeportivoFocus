package com.example.clubfocus.data.repository

import com.example.clubfocus.data.entity.Carnet
import java.time.LocalDate

object CarnetRepository {

    private var siguienteId = 1

    private val carnets = mutableListOf<Carnet>()

    fun agregarCarnet(nroSocio: String) {

        val fechaEmision = LocalDate.now()
        val fechaVencimiento = fechaEmision.plusMonths(1)

        val carnet = Carnet(
            id = siguienteId,
            nroSocio = nroSocio,
            fechaEmision = fechaEmision,
            fechaVencimiento = fechaVencimiento
        )

        carnets.add(carnet)
        siguienteId++
    }

    fun buscarPorNroSocio(nroSocio: String): Carnet? {

        val hoy = LocalDate.now()

        return carnets.find {
            it.nroSocio == nroSocio &&
                    hoy <= it.fechaVencimiento
        }
    }
}