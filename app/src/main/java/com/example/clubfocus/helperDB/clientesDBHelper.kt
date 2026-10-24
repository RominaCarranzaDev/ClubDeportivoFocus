package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clubfocus.data.entity.Cliente

class ClientesDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE clientes(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT UNIQUE NOT NULL,
                fecha_nacimiento TEXT NOT NULL,
                email TEXT NOT NULL,
                apto_fisico INTEGER NOT NULL,
                es_socio INTEGER NOT NULL,
                nro_socio TEXT
            )""".trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS clientes")
        if (db != null) {
            onCreate(db)
        }
    }

    fun agregarCliente(
        nombre: String,
        apellido: String,
        dni: String,
        fechaNacimiento: String,
        email: String,
        aptoFisico: Boolean,
        esSocio: Boolean
    ): Long {
        val db = writableDatabase

        val nroSocioGenerado = if (esSocio) "CAR100${obtenerCantidadSocios() + 1}" else null

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("fecha_nacimiento", fechaNacimiento)
            put("email", email)
            put("apto_fisico", if (aptoFisico) 1 else 0)
            put("es_socio", if (esSocio) 1 else 0)
            put("nro_socio", nroSocioGenerado)
        }

        // No cerramos db acá para no cortar la conexión de SQLite
        return db.insert("clientes", null, valores)
    }

    fun existeCliente(dni: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT dni FROM clientes WHERE dni = ?", arrayOf(dni))
        val existe = cursor.count > 0
        cursor.close() // Solo cerramos el cursor
        return existe
    }

    private fun obtenerCantidadSocios(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM clientes WHERE es_socio = 1", null)
        var total = 0
        cursor.use {
            if (it.moveToFirst()) {
                total = it.getInt(0)
            }
        }
        return total
    }

    fun buscarPorDni(dni: String): Cliente? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM clientes WHERE dni = ?", arrayOf(dni))
        var clienteEncontrado: Cliente? = null

        if (cursor.moveToFirst()) {
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fechaNacStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento"))
            val fechaNacLocalDate = try {
                java.time.LocalDate.parse(fechaNacStr, formatter)
            } catch (e: Exception) {
                java.time.LocalDate.now()
            }

            clienteEncontrado = Cliente(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                fechaNacimiento = fechaNacLocalDate,
                telefono = "",
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("apto_fisico")) == 1,
                activo = true,
                esSocio = cursor.getInt(cursor.getColumnIndexOrThrow("es_socio")) == 1,
                fechaInscripcion = java.time.LocalDate.now()
            )
        }

        cursor.close()
        return clienteEncontrado

    }
}