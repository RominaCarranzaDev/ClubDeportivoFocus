package com.example.clubfocus.helperDB

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UsuariosDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        DBTablas.crearTodasLasTablas(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        DBTablas.crearTodasLasTablas(db)
        try {
            db.execSQL("ALTER TABLE usuarios ADD COLUMN rol TEXT NOT NULL DEFAULT 'Administrador'")
        } catch (_: Exception) {
            // Ignorar si la columna ya existe
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.let {
            it.execSQL("DROP TABLE IF EXISTS usuarios")
            it.execSQL("DROP TABLE IF EXISTS carnets")
            it.execSQL("DROP TABLE IF EXISTS cuotas")
            it.execSQL("DROP TABLE IF EXISTS clientes")
            onCreate(it)
        }
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
}