package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clubfocus.data.entity.CarnetListado
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CarnetDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        DBTablas.crearTodasLasTablas(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        DBTablas.crearTodasLasTablas(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.let {
            it.execSQL("DROP TABLE IF EXISTS carnets")
            it.execSQL("DROP TABLE IF EXISTS cuotas")
            it.execSQL("DROP TABLE IF EXISTS clientes")
            it.execSQL("DROP TABLE IF EXISTS usuarios")
            onCreate(it)
        }
    }

    fun crearCarnet(nroSocio: String): Long {
        val db = writableDatabase

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val fechaEmision = LocalDate.now()
        val fechaVencimiento = fechaEmision.plusMonths(1)

        val valores = ContentValues().apply {
            put("nro_socio", nroSocio)
            put("fecha_emision", fechaEmision.format(formatter))
            put("fecha_vencimiento", fechaVencimiento.format(formatter))
        }

        return db.insert("carnets", null, valores)
    }

    fun renovarCarnet(
        carnetId: Int,
        nuevaFechaEmision: LocalDate
    ): Int {
        val db = writableDatabase
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val nuevaFechaVencimiento = nuevaFechaEmision.plusMonths(1)

        val valores = ContentValues().apply {
            put("fecha_vencimiento", nuevaFechaVencimiento.format(formatter))
        }

        return db.update("carnets", valores, "id = ?", arrayOf(carnetId.toString()))
    }

    fun obtenerCarnetsConDatosSocio(): List<CarnetListado> {
        val lista = mutableListOf<CarnetListado>()
        val db = readableDatabase
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val consulta = """
            SELECT
                carnets.id,
                carnets.nro_socio,
                carnets.fecha_emision,
                carnets.fecha_vencimiento,
                clientes.nombre,
                clientes.apellido,
                clientes.dni
            FROM carnets
            INNER JOIN clientes
                ON carnets.nro_socio = clientes.nro_socio
            ORDER BY clientes.apellido ASC
        """.trimIndent()

        val cursor = db.rawQuery(consulta, null)

        cursor.use {
            while (it.moveToNext()) {
                lista.add(
                    CarnetListado(
                        id = it.getInt(it.getColumnIndexOrThrow("id")),
                        nroSocio = it.getString(
                            it.getColumnIndexOrThrow("nro_socio")
                        ),
                        fechaEmision = LocalDate.parse(
                            it.getString(
                                it.getColumnIndexOrThrow("fecha_emision")
                            ),
                            formatter
                        ),
                        fechaVencimiento = LocalDate.parse(
                            it.getString(
                                it.getColumnIndexOrThrow("fecha_vencimiento")
                            ),
                            formatter
                        ),
                        nombre = it.getString(
                            it.getColumnIndexOrThrow("nombre")
                        ),
                        apellido = it.getString(
                            it.getColumnIndexOrThrow("apellido")
                        ),
                        dni = it.getString(
                            it.getColumnIndexOrThrow("dni")
                        )
                    )
                )
            }
        }

        return lista
    }

    fun buscarCarnetConDatosPorId(carnetId: Int): CarnetListado? {
        val db = readableDatabase
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val consulta = """
            SELECT
                carnets.id,
                carnets.nro_socio,
                carnets.fecha_emision,
                carnets.fecha_vencimiento,
                clientes.nombre,
                clientes.apellido,
                clientes.dni
            FROM carnets
            INNER JOIN clientes
                ON carnets.nro_socio = clientes.nro_socio
            WHERE carnets.id = ?
            LIMIT 1
        """.trimIndent()

        val cursor = db.rawQuery(
            consulta,
            arrayOf(carnetId.toString())
        )

        cursor.use {
            if (it.moveToFirst()) {
                return CarnetListado(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    nroSocio = it.getString(
                        it.getColumnIndexOrThrow("nro_socio")
                    ),
                    fechaEmision = LocalDate.parse(
                        it.getString(
                            it.getColumnIndexOrThrow("fecha_emision")
                        ),
                        formatter
                    ),
                    fechaVencimiento = LocalDate.parse(
                        it.getString(
                            it.getColumnIndexOrThrow("fecha_vencimiento")
                        ),
                        formatter
                    ),
                    nombre = it.getString(
                        it.getColumnIndexOrThrow("nombre")
                    ),
                    apellido = it.getString(
                        it.getColumnIndexOrThrow("apellido")
                    ),
                    dni = it.getString(
                        it.getColumnIndexOrThrow("dni")
                    )
                )
            }
        }

        return null
    }

    fun buscarCarnetPorDni(dni: String): CarnetListado? {
        val db = readableDatabase
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val cursor = db.rawQuery(
            """
                SELECT carnets.id, carnets.nro_socio,
                       carnets.fecha_emision, carnets.fecha_vencimiento,
                       clientes.nombre, clientes.apellido, clientes.dni
                FROM carnets
                INNER JOIN clientes
                    ON carnets.nro_socio = clientes.nro_socio
                WHERE clientes.dni = ?
                ORDER BY carnets.id DESC
                LIMIT 1
            """.trimIndent(),
            arrayOf(dni)
        )

        return cursor.use {
            if (it.moveToFirst()) {
                CarnetListado(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    nroSocio = it.getString(it.getColumnIndexOrThrow("nro_socio")),
                    fechaEmision = LocalDate.parse(
                        it.getString(it.getColumnIndexOrThrow("fecha_emision")),
                        formatter
                    ),
                    fechaVencimiento = LocalDate.parse(
                        it.getString(it.getColumnIndexOrThrow("fecha_vencimiento")),
                        formatter
                    ),
                    nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                    apellido = it.getString(it.getColumnIndexOrThrow("apellido")),
                    dni = it.getString(it.getColumnIndexOrThrow("dni"))
                )
            } else {
                null
            }
        }
    }
}