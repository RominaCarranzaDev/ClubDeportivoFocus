package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

object DBTablas {

    fun crearTodasLasTablas(db: SQLiteDatabase) {
        // 1. Tabla Usuarios
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                rol TEXT NOT NULL
            )
        """.trimIndent())

        val cursorAdmin = db.rawQuery("SELECT id FROM usuarios WHERE usuario = 'admin'", null)
        if (!cursorAdmin.moveToFirst()) {
            val valoresAdmin = ContentValues().apply {
                put("usuario", "admin")
                put("password", "123456")
                put("rol", "Administrador")
            }
            db.insert("usuarios", null, valoresAdmin)
        }
        cursorAdmin.close()

        // 2. Tabla Clientes
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS clientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                apellido TEXT NOT NULL,
                dni TEXT UNIQUE NOT NULL,
                fecha_nacimiento TEXT NOT NULL,
                email TEXT NOT NULL,
                apto_fisico INTEGER NOT NULL,
                fecha_inscripcion TEXT NOT NULL,
                activo INTEGER NOT NULL DEFAULT 0,
                es_socio INTEGER NOT NULL,
                nro_socio TEXT
            )
        """.trimIndent())

        // 3. Tabla Cuotas
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS cuotas (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_id INTEGER NOT NULL,
                tipo TEXT NOT NULL,
                monto REAL NOT NULL,
                monto_final REAL NOT NULL,
                fecha_pago TEXT,
                fecha_vencimiento TEXT NOT NULL,
                medio_pago TEXT,
                promocion TEXT,
                estado TEXT NOT NULL,
                FOREIGN KEY (cliente_id) REFERENCES clientes(id)
            )
        """.trimIndent())

        // 4. Tabla Carnets
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS carnets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nro_socio TEXT NOT NULL,
                fecha_emision TEXT NOT NULL,
                fecha_vencimiento TEXT NOT NULL,
                FOREIGN KEY (nro_socio) REFERENCES clientes(nro_socio)
            )
        """.trimIndent())

        // 5. Carga de Juan Pérez con verificación por tabla

        // A. Verificar e insertar Cliente
        val cursorCliente = db.rawQuery("SELECT id FROM clientes WHERE dni = '11223344'", null)
        val clienteId: Long
        if (cursorCliente.moveToFirst()) {
            clienteId = cursorCliente.getLong(0)
        } else {
            val valoresCliente = ContentValues().apply {
                put("nombre", "Juan")
                put("apellido", "Pérez")
                put("dni", "22222222")
                put("fecha_nacimiento", "15/05/1990")
                put("email", "carlos.vencido@focus.com")
                put("apto_fisico", 1)
                put("fecha_inscripcion", "01/01/2026")
                put("activo", 0)
                put("es_socio", 1)
                put("nro_socio", "CAR9999")
            }
            clienteId = db.insert("clientes", null, valoresCliente)
        }
        cursorCliente.close()

        // B. Verificar e insertar Cuota Vencida
        if (clienteId > 0) {
            val cursorCuota = db.rawQuery("SELECT id FROM cuotas WHERE cliente_id = ?", arrayOf(clienteId.toString()))
            if (!cursorCuota.moveToFirst()) {
                val valoresCuota = ContentValues().apply {
                    put("cliente_id", clienteId)
                    put("tipo", "MENSUAL")
                    put("monto", 45000.0)
                    put("monto_final", 45000.0)
                    putNull("fecha_pago")
                    put("fecha_vencimiento", "10/08/2026")
                    putNull("medio_pago")
                    putNull("promocion")
                    put("estado", "VENCIDA")
                }
                db.insert("cuotas", null, valoresCuota)
            }
            cursorCuota.close()
        }

        // C. Verificar e insertar Carnet (solo 1)
        val cursorCarnet = db.rawQuery("SELECT id FROM carnets WHERE nro_socio = 'CAR9999'", null)
        if (!cursorCarnet.moveToFirst()) {
            val valoresCarnet = ContentValues().apply {
                put("nro_socio", "CAR9999")
                put("fecha_emision", "10/07/2026")
                put("fecha_vencimiento", "10/08/2026")
            }
            db.insert("carnets", null, valoresCarnet)
        }
        cursorCarnet.close()
    }
}