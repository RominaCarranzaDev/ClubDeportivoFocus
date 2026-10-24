package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UsuariosDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        crearTablaEInsertarAdmin(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        crearTablaEInsertarAdmin(db)
        // Asegura que la columna 'rol' exista si la tabla ya había sido creada antes
        try {
            db.execSQL("ALTER TABLE usuarios ADD COLUMN rol TEXT NOT NULL DEFAULT 'Administrador'")
        } catch (e: Exception) {
            // Si la columna ya existe, SQLite lanza excepción y continúa normalmente
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS usuarios")
        if (db != null) {
            onCreate(db)
        }
    }

    private fun crearTablaEInsertarAdmin(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                rol TEXT NOT NULL
            )
        """.trimIndent())

        val cursor = db.rawQuery("SELECT COUNT(*) FROM usuarios", null)
        var cantidad = 0
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0)
        }
        cursor.close()

        if (cantidad == 0) {
            val valoresAdmin = ContentValues().apply {
                put("usuario", "admin")
                put("password", "123456")
                put("rol", "Administrador")
            }
            db.insert("usuarios", null, valoresAdmin)
        }
    }

    // Devuelve el rol si el usuario y contraseña coinciden; de lo contrario devuelve null
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
}