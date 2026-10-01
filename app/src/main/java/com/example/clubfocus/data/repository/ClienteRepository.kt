package com.example.clubfocus.data.repository

import com.example.clubfocus.data.entity.Cliente
import com.example.clubfocus.data.entity.Socio
import com.example.clubfocus.data.repository.CarnetRepository
import java.time.LocalDate


object ClienteRepository {

    private var siguienteId = 1
    private var siguienteNroSocio = 1

    private val clientes = mutableListOf<Cliente>()

    // Socio y No socio iniciales de prueba
    init {
        agregarCliente(
            nombre = "Socio",
            apellido = "Prueba",
            dni = "12345678",
            fechaNacimiento = LocalDate.of(1990, 5, 10),
            telefono = "1123456789",
            email = "socio@gmail.com",
            aptoFisico = true,
            esSocio = true,
            activo = true
        )
        agregarCliente(
            nombre = "No Socio",
            apellido = "Prueba",
            dni = "11223344",
            fechaNacimiento = LocalDate.of(1992, 3, 15),
            telefono = "1145678901",
            email = "nosocio@gmail.com",
            aptoFisico = true,
            esSocio = false,
            activo = true
        )
    }
    // Create Cliente
    fun agregarCliente(
        nombre: String,
        apellido: String,
        dni: String,
        fechaNacimiento: LocalDate,
        telefono: String,
        email: String,
        aptoFisico: Boolean,
        esSocio: Boolean,
        activo: Boolean
    ) {
        //Si es socio le agrega las propiedades propias del socio
        if (esSocio) {
            val nroSocio = "CAR$siguienteNroSocio"

            val socio = Socio(
                id = siguienteId,
                nombre = nombre,
                apellido = apellido,
                dni = dni,
                fechaNacimiento = fechaNacimiento,
                telefono = telefono,
                email = email,
                aptoFisico = aptoFisico,
                activo = activo,
                fechaInscripcion = LocalDate.now(),
                cuotas = mutableListOf(),
                nroSocio = nroSocio
            )


            clientes.add(socio)

            CarnetRepository.agregarCarnet(nroSocio)

            siguienteNroSocio++

        } else {

            val cliente = Cliente(
                id = siguienteId,
                nombre = nombre,
                apellido = apellido,
                dni = dni,
                fechaNacimiento = fechaNacimiento,
                telefono = telefono,
                email = email,
                aptoFisico = aptoFisico,
                activo = activo,
                esSocio = false,
                fechaInscripcion = LocalDate.now(),
                cuotas = mutableListOf()
            )

            clientes.add(cliente)
        }

        siguienteId++
    }


    fun buscarPorId(id: Int): Cliente? {
        return clientes.find { it.id == id }
    }

    fun buscarPorDni(dni: String?): Cliente? {
        return clientes.find { it.dni == dni }
    }

    fun obtenerTodos(): List<Cliente> {
        return clientes
    }
}