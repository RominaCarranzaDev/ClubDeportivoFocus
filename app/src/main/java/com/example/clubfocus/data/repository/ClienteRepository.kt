package com.example.clubfocus.data.repository

import com.example.clubfocus.data.entity.Cliente
import java.time.LocalDate


object ClienteRepository {

    private val clientes = mutableListOf<Cliente>()


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