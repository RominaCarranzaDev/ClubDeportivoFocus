package com.example.clubfocus.data.entity
import java.time.LocalDate

data class Usuario(
    val id: Int,
    val username: String,
    val password: String,
    val rol: Rol,
    val estaActivo: Boolean,
    val nombre: String,
    val apellido: String,
    val fechaNacimiento: LocalDate
)

enum class Rol {
    ADMIN,
    RECEPCIONISTA,
    PROFESOR
}