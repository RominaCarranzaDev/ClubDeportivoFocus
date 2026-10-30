package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase

object DBTablas {

    fun crearTodasLasTablas(db: SQLiteDatabase) {
        // 1. Tabla Usuarios
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                usuario TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                rol TEXT NOT NULL
            )
        """.trimIndent()
        )

        // 2. Tabla Clientes
        db.execSQL(
            """
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
                nro_socio TEXT UNIQUE
            )
        """.trimIndent()
        )

        // 3. Tabla Cuotas
        db.execSQL(
            """
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
        """.trimIndent()
        )

        // 4. Tabla Carnets
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS carnets (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nro_socio TEXT NOT NULL UNIQUE,
                fecha_emision TEXT NOT NULL,
                fecha_vencimiento TEXT NOT NULL,
                FOREIGN KEY (nro_socio) REFERENCES clientes(nro_socio)
            )
        """.trimIndent()
        )
    }

    fun cargarAdministradorInicial(db: SQLiteDatabase) {

        val cursor = db.rawQuery(
            "SELECT id FROM usuarios WHERE usuario = ?",
            arrayOf("admin")
        )

        val existeAdmin = cursor.use { it.moveToFirst() }

        if (!existeAdmin) {
            val valores = ContentValues().apply {
                put("usuario", "admin")
                put("password", "123456")
                put("rol", "Administrador")
            }

            db.insert("usuarios", null, valores)
        }
    }

    fun cargarDatosPrueba(db: SQLiteDatabase) {

        // SOCIO 1: Activo, dos cuotas pagas
        val juanId = obtenerOCrearCliente(
            db, "Juan", "Pérez", "11111111",
            "15/05/1990", "juan@focus.com",
            "29/08/2026", 1, 1, "CAR1001"
        )

        insertarCuota(
            db, juanId, "MENSUAL", 45000.0,
            "29/08/2026", "29/09/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION")

        insertarCuota(
            db, juanId, "MENSUAL", 45000.0,
            "30/09/2026", "30/10/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )

        insertarCarnet(db, "CAR1001", "30/09/2026", "30/10/2026")


        // SOCIO 2: Inactivo, primera cuota paga y segunda vencida
        val mariaId = obtenerOCrearCliente(
            db, "María", "González", "22222222",
            "20/04/1992", "maria@focus.com",
            "20/08/2026", 1, 0, "CAR1002"
        )

        insertarCuota(
            db, mariaId, "MENSUAL", 45000.0,
            "20/08/2026", "20/09/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )

        insertarCuota(
            db, mariaId, "MENSUAL", 45000.0,
            null, "21/09/2026", "VENCIDA", null, null
        )

        insertarCarnet(db, "CAR1002", "20/08/2026", "20/09/2026")


        // SOCIO 3: Activo, su cuota y carnet vencen hoy
        val lauraId = obtenerOCrearCliente(
            db, "Laura", "Fernández", "33333333",
            "12/06/1995", "laura@focus.com",
            "29/08/2026", 1, 1, "CAR1003"
        )

        insertarCuota(
            db, lauraId, "MENSUAL", 45000.0,
            "29/08/2026", "29/09/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )

        insertarCuota(
            db, lauraId, "MENSUAL", 45000.0,
            "30/09/2026", "30/10/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )

        insertarCarnet(db, "CAR1003", "30/09/2026", "30/10/2026")


        // SOCIO 4: Inscripción y primer pago de hoy
        val martinId = obtenerOCrearCliente(
            db, "Martín", "López", "44444444",
            "10/02/1998", "martin@focus.com",
            "30/10/2026", 1, 1, "CAR1004"
        )

        insertarCuota(
            db, martinId, "MENSUAL", 45000.0,
            "30/10/2026", "30/11/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )

        insertarCarnet(db, "CAR1004", "30/10/2026", "30/11/2026")


        // NO SOCIO 1: Dos cuotas diarias consecutivas
        val pedroId = obtenerOCrearCliente(
            db, "Pedro", "Martínez", "55555555",
            "05/03/1990", "pedro@focus.com",
            "29/10/2026", 0, 1, null
        )

        insertarCuota(
            db, pedroId, "DIARIA", 20000.0,
            "29/10/2026", "29/10/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )

        insertarCuota(
            db, pedroId, "DIARIA", 20000.0,
            "30/10/2026", "30/10/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )


        // NO SOCIO 2: Dos cuotas diarias separadas por una semana
        val anaId = obtenerOCrearCliente(
            db, "Analia", "Torres", "66666666",
            "18/11/1993", "ana@focus.com",
            "23/10/2026", 0, 1, null
        )

        insertarCuota(
            db, anaId, "DIARIA", 20000.0,
            "23/10/2026", "23/10/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )

        insertarCuota(
            db, anaId, "DIARIA", 20000.0,
            "30/10/2026", "30/10/2026", "PAGADA", medioPago = "EFECTIVO",
            promocion = "SIN_PROMOCION"
        )
    }

    // Busca el cliente por DNI. Si no existe, lo crea.
    private fun obtenerOCrearCliente(
        db: SQLiteDatabase,
        nombre: String,
        apellido: String,
        dni: String,
        fechaNacimiento: String,
        email: String,
        fechaInscripcion: String,
        esSocio: Int,
        activo: Int,
        nroSocio: String?
    ): Long {

        val cursor = db.rawQuery(
            "SELECT id FROM clientes WHERE dni = ?",
            arrayOf(dni)
        )

        val clienteExistente = cursor.use {
            if (it.moveToFirst()) it.getLong(0) else null
        }

        if (clienteExistente != null) return clienteExistente

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("fecha_nacimiento", fechaNacimiento)
            put("email", email)
            put("apto_fisico", 1)
            put("fecha_inscripcion", fechaInscripcion)
            put("activo", activo)
            put("es_socio", esSocio)

            if (nroSocio != null) {
                put("nro_socio", nroSocio)
            } else {
                putNull("nro_socio")
            }
        }

        return db.insert("clientes", null, valores)
    }

    // Inserta una cuota si todavía no existe.
    private fun insertarCuota(
        db: SQLiteDatabase,
        clienteId: Long,
        tipo: String,
        monto: Double,
        fechaPago: String?,
        fechaVencimiento: String,
        estado: String,
        medioPago: String?,
        promocion: String?
    ) {

        val cursor = db.rawQuery(
            """
            SELECT id FROM cuotas
            WHERE cliente_id = ?
            AND tipo = ?
            AND fecha_vencimiento = ?
            """.trimIndent(),
            arrayOf(clienteId.toString(), tipo, fechaVencimiento)
        )

        val existe = cursor.use { it.moveToFirst() }

        if (existe) return

        val valores = ContentValues().apply {
            put("cliente_id", clienteId)
            put("tipo", tipo)
            put("monto", monto)
            put("monto_final", monto)

            if (fechaPago != null) {
                put("fecha_pago", fechaPago)
            } else {
                putNull("fecha_pago")
            }

            put("fecha_vencimiento", fechaVencimiento)

            put("estado", estado)

            if (medioPago != null) {
                put("medio_pago", medioPago)
            } else {
                putNull("medio_pago")
            }

            if (promocion != null) {
                put("promocion", promocion)
            } else {
                putNull("promocion")
            }
        }

        db.insert("cuotas", null, valores)
    }

    // Inserta el carnet si todavía no existe.
    private fun insertarCarnet(
        db: SQLiteDatabase,
        nroSocio: String,
        fechaEmision: String,
        fechaVencimiento: String
    ) {

        val cursor = db.rawQuery(
            "SELECT id FROM carnets WHERE nro_socio = ?",
            arrayOf(nroSocio)
        )

        val existe = cursor.use { it.moveToFirst() }

        if (existe) return

        val valores = ContentValues().apply {
            put("nro_socio", nroSocio)
            put("fecha_emision", fechaEmision)
            put("fecha_vencimiento", fechaVencimiento)
        }

        db.insert("carnets", null, valores)
    }
}
