package com.example.clubfocus.helperDB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

data class AdministradorCuenta(val id: Int, val usuario: String)

class UsuariosDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        DBTablas.crearTodasLasTablas(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TO DO
    }

    fun obtenerRol(usuario: String, contrasena: String): String? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT rol FROM usuarios WHERE usuario = ? AND password = ?",
            arrayOf(usuario, contrasena)
        )
        var rolEncontrado: String? = null

        if (cursor.moveToFirst()) {
            rolEncontrado = cursor.getString(cursor.getColumnIndexOrThrow("rol"))
        }

        cursor.close()
        return rolEncontrado
    }

    fun obtenerRolPorUsuario(usuario: String): String? {
        return readableDatabase.rawQuery(
            "SELECT rol FROM usuarios WHERE usuario = ?",
            arrayOf(usuario)
        ).use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(cursor.getColumnIndexOrThrow("rol")) else null
        }
    }

    fun obtenerAdministradores(): List<AdministradorCuenta> {
        val administradores = mutableListOf<AdministradorCuenta>()
        readableDatabase.rawQuery(
            "SELECT id, usuario FROM usuarios WHERE LOWER(rol) = LOWER(?) ORDER BY usuario",
            arrayOf("Administrador")
        ).use { cursor ->
            while (cursor.moveToNext()) {
                administradores.add(
                    AdministradorCuenta(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        usuario = cursor.getString(cursor.getColumnIndexOrThrow("usuario"))
                    )
                )
            }
        }
        return administradores
    }

    fun crearAdministrador(usuario: String, contrasena: String): Long {
        val valores = ContentValues().apply {
            put("usuario", usuario)
            put("password", contrasena)
            put("rol", "Administrador")
        }
        return writableDatabase.insert("usuarios", null, valores)
    }

    fun eliminarAdministrador(id: Int): Int {
        return writableDatabase.delete(
            "usuarios",
            "id = ? AND LOWER(rol) = LOWER(?)",
            arrayOf(id.toString(), "Administrador")
        )
    }
}
