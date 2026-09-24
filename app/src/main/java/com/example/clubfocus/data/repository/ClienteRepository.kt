package com.example.clubfocus.data.repository

import com.example.clubfocus.data.entity.Cliente
import java.time.LocalDate

class ClienteRepository {

    private var siguienteId = 1

    private val clientes = mutableListOf<Cliente>()

    init {
        agregarCliente(
            nombre = "Socio",
            apellido = "Prueba",
            dni = "12345678",
            fechaNacimiento = LocalDate.of(1990, 5, 10),
            telefono = "1123456789",
            email = "socio@gmail.com",
            aptoFisico = true,
            activo = true,
            esSocio = true,
            fechaInscripcion = LocalDate.of(2026, 9, 1)
        )
        agregarCliente(
            nombre = "No Socio",
            apellido = "Prueba",
            dni = "11223344",
            fechaNacimiento = LocalDate.of(1992, 3, 15),
            telefono = "1145678901",
            email = "nosocio@gmail.com",
            aptoFisico = true,
            activo = true,
            esSocio = false,
            fechaInscripcion = LocalDate.of(2026, 9, 1)
        )
    }

    fun agregarCliente(
        nombre: String,
        apellido: String,
        dni: String,
        fechaNacimiento: LocalDate,
        telefono: String,
        email: String,
        aptoFisico: Boolean,
        activo: Boolean,
        esSocio: Boolean,
        fechaInscripcion: LocalDate
    ) {
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
            esSocio = esSocio,
            fechaInscripcion = fechaInscripcion,
            cuotas = mutableListOf()
        )

        clientes.add(cliente)
        siguienteId++
    }

    fun buscarPorId(id: Int): Cliente? {
        return clientes.find { it.id == id }
    }

    fun buscarPorDni(dni: String): Cliente? {
        return clientes.find { it.dni == dni }
    }

    fun buscarPorApellido(apellido: String): List<Cliente> {
        return clientes.filter {
            it.apellido.equals(apellido, ignoreCase = true)
        }
    }

    fun obtenerTodos(): List<Cliente> {
        return clientes
    }
}