package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clubfocus.data.entity.Cuota
import com.example.clubfocus.data.entity.EstadoCuota
import com.example.clubfocus.data.entity.MedioPago
import com.example.clubfocus.data.entity.Promocion
import com.example.clubfocus.data.entity.TipoCuota
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class CuotasDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        crearTablaCuotas(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
        // Esto garantiza que la tabla se cree aunque ClientesDBHelper haya creado el archivo antes
        crearTablaCuotas(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS cuotas")
        if (db != null) {
            onCreate(db)
        }
    }

    private fun crearTablaCuotas(db: SQLiteDatabase) {
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
    }

    fun generarCuotaInicial(
        clienteId: Int,
        tipo: TipoCuota,
        monto: Double,
        montoFinal: Double,
        fechaVencimiento: LocalDate
    ): Long {
        val db = writableDatabase

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val valores = ContentValues().apply {
            put("cliente_id", clienteId)
            put("tipo", tipo.name)
            put("monto", monto)
            put("monto_final", montoFinal)
            putNull("fecha_pago")
            put("fecha_vencimiento", fechaVencimiento.format(formatter))
            putNull("medio_pago")
            putNull("promocion")
            put("estado", EstadoCuota.PENDIENTE.name)
        }

        return db.insert("cuotas", null, valores)
    }
    fun registrarPago(
        cuotaId: Int,
        fechaPago: String,
        montoFinal: Double,
        medioPago: MedioPago,
        promocion: Promocion?
    ): Int {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put("fecha_pago", fechaPago)
            put("monto_final", montoFinal)
            put("medio_pago", medioPago.name)
            put("promocion", promocion?.name)
            put("estado", EstadoCuota.PAGADA.name)
        }

        return db.update("cuotas", valores, "id = ?", arrayOf(cuotaId.toString()))}

    fun tieneCuotaVigente(dniCliente: String): Boolean {
        val db = readableDatabase

        val cursor = db.rawQuery(
            """
                SELECT
                    c.es_socio,
                    q.fecha_pago
                FROM cuotas q
                INNER JOIN clientes c
                    ON c.id = q.cliente_id
                WHERE c.dni = ?
                  AND UPPER(q.estado) = 'PAGADA'
                ORDER BY q.id DESC
                LIMIT 1
            """.trimIndent(),
            arrayOf(dniCliente)
        )
            .use { cursor ->

                // Si nunca pagó una cuota, no tiene vigencia.
                if (!cursor.moveToFirst()) {
                    return false
                }

                val esSocio = cursor.getInt(cursor.getColumnIndexOrThrow("es_socio")) == 1

                val fechaPagoTexto = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago"))

                // Sin fecha de pago no podemos validar la vigencia.
                if (fechaPagoTexto.isNullOrBlank()) {
                    return false
                }

                val fechaPago = try {
                    LocalDate.parse(
                        fechaPagoTexto,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
                } catch (e: DateTimeParseException) {
                    try {
                        // También admite fechas en formato yyyy-MM-dd.
                        LocalDate.parse(fechaPagoTexto)
                    } catch (e: DateTimeParseException) {
                        return false
                    }
                }

                val hoy = LocalDate.now()

                val fechaFinVigencia = if (esSocio) {
                    fechaPago.plusMonths(1)
                } else {
                    fechaPago
                }

                // Si hoy llegó a la fecha de vencimiento, ya puede generar una nueva cuota.
                return hoy.isBefore(fechaFinVigencia)
            }
    }

    // Objeto simple para transportar los datos del pago al comprobante
    data class UltimoPago(
        val montoFinal: Double,
        val medioPago: String
    )

    fun obtenerUltimoPago(dniCliente: String): UltimoPago? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            """
                SELECT cuotas.monto_final, cuotas.medio_pago
                FROM cuotas
                INNER JOIN clientes
                    ON cuotas.cliente_id = clientes.id
                WHERE clientes.dni = ?
                ORDER BY cuotas.id DESC
                LIMIT 1
            """.trimIndent(),
            arrayOf(dniCliente)
        )
        var pago: UltimoPago? = null

        if (cursor.moveToFirst()) {
            val monto = cursor.getDouble(cursor.getColumnIndexOrThrow("monto_final"))
            val medio = cursor.getString(cursor.getColumnIndexOrThrow("medio_pago"))
            pago = UltimoPago(monto, medio)
        }

        cursor.close()
        return pago
    }

    fun obtenerCuotasPorCliente(clienteId: Int): List<Cuota> {
        val db = readableDatabase
        val listaCuotas = mutableListOf<Cuota>()

        val cursor = db.rawQuery(
            "SELECT * FROM cuotas WHERE cliente_id = ? ORDER BY fecha_vencimiento ASC",
            arrayOf(clienteId.toString())
        )

        cursor.use {
            while (it.moveToNext()) {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

                val fechaPagoTexto = it.getString(it.getColumnIndexOrThrow("fecha_pago"))

                val medioPagoTexto = it.getString(it.getColumnIndexOrThrow("medio_pago"))

                val promocionTexto = it.getString(it.getColumnIndexOrThrow("promocion"))

                val cuota = Cuota(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    clienteId = it.getInt(it.getColumnIndexOrThrow("cliente_id")),
                    tipo = TipoCuota.valueOf(
                        it.getString(it.getColumnIndexOrThrow("tipo"))
                    ),
                    monto = it.getDouble(it.getColumnIndexOrThrow("monto")),
                    montoFinal = it.getDouble(it.getColumnIndexOrThrow("monto_final")),
                    fecha = fechaPagoTexto?.let { LocalDate.parse(it, formatter) },
                    fechaVencimiento = LocalDate.parse(it.getString(it.getColumnIndexOrThrow("fecha_vencimiento")), formatter),
                    estado = EstadoCuota.valueOf(it.getString(it.getColumnIndexOrThrow("estado"))),
                    medioPago = medioPagoTexto?.let { MedioPago.valueOf(it) },
                    promocion = promocionTexto?.let { Promocion.valueOf(it) }
                )

                listaCuotas.add(cuota)
            }
        }

        return listaCuotas
    }

    fun obtenerCuotaImpagaPorCliente(clienteId: Int): Cuota? {
        val db = readableDatabase

        val cursor = db.rawQuery(
            """
        SELECT * FROM cuotas
        WHERE cliente_id = ?
          AND estado IN (?, ?)
        ORDER BY id ASC
        LIMIT 1
        """.trimIndent(),
            arrayOf(
                clienteId.toString(),
                EstadoCuota.PENDIENTE.name,
                EstadoCuota.VENCIDA.name
            )
        )

        cursor.use {
            if (it.moveToFirst()) {
                val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val fechaPagoTexto = it.getString(it.getColumnIndexOrThrow("fecha_pago"))
                val medioPagoTexto = it.getString(it.getColumnIndexOrThrow("medio_pago"))
                val promocionTexto = it.getString(it.getColumnIndexOrThrow("promocion"))

                return Cuota(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    clienteId = it.getInt(it.getColumnIndexOrThrow("cliente_id")),
                    tipo = TipoCuota.valueOf(it.getString(it.getColumnIndexOrThrow("tipo"))),
                    monto = it.getDouble(it.getColumnIndexOrThrow("monto")),
                    montoFinal = it.getDouble(it.getColumnIndexOrThrow("monto_final")),
                    fecha = fechaPagoTexto?.let { java.time.LocalDate.parse(it, formatter) },
                    fechaVencimiento = java.time.LocalDate.parse(it.getString(it.getColumnIndexOrThrow("fecha_vencimiento")), formatter),
                    estado = EstadoCuota.valueOf(it.getString(it.getColumnIndexOrThrow("estado"))),
                    medioPago = medioPagoTexto?.let { MedioPago.valueOf(it) },
                    promocion = promocionTexto?.let { Promocion.valueOf(it) }
                )
            }
        }

        return null
    }

    fun obtenerCuotasVencidasOPorVencer(): List<Cuota> {
        val db = readableDatabase
        val cuotas = mutableListOf<Cuota>()
        val hoy = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val cursor = db.rawQuery(
            """SELECT * FROM cuotas
                WHERE estado != 'PAGADA'
                ORDER BY id ASC
                """.trimIndent(),
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                val fechaVencimiento = LocalDate.parse(it.getString(it.getColumnIndexOrThrow("fecha_vencimiento")), formatter)

                if (!fechaVencimiento.isAfter(hoy)) {
                    val fechaPagoTexto = it.getString(
                        it.getColumnIndexOrThrow("fecha_pago")
                    )

                    val medioPagoTexto = it.getString(it.getColumnIndexOrThrow("medio_pago"))

                    val promocionTexto = it.getString(it.getColumnIndexOrThrow("promocion"))

                    cuotas.add(
                        Cuota(
                            id = it.getInt(it.getColumnIndexOrThrow("id")),
                            clienteId = it.getInt(
                                it.getColumnIndexOrThrow("cliente_id")
                            ),
                            tipo = TipoCuota.valueOf(
                                it.getString(it.getColumnIndexOrThrow("tipo"))
                            ),
                            monto = it.getDouble(
                                it.getColumnIndexOrThrow("monto")
                            ),
                            montoFinal = it.getDouble(
                                it.getColumnIndexOrThrow("monto_final")
                            ),
                            fecha = fechaPagoTexto?.let { fecha ->
                                LocalDate.parse(fecha, formatter)
                            },
                            fechaVencimiento = fechaVencimiento,
                            estado = EstadoCuota.valueOf(
                                it.getString(it.getColumnIndexOrThrow("estado"))
                            ),
                            medioPago = medioPagoTexto?.let {
                                MedioPago.valueOf(it)
                            },
                            promocion = promocionTexto?.let {
                                Promocion.valueOf(it)
                            }
                        )
                    )
                }
            }
        }

        return cuotas
    }
}