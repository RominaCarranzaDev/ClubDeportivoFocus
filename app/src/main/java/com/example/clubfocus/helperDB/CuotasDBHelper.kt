package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clubfocus.data.entity.Cuota
import com.example.clubfocus.data.entity.EstadoCuota
import com.example.clubfocus.data.entity.MedioPago
import com.example.clubfocus.data.entity.PagoHistorialItem
import com.example.clubfocus.data.entity.Promocion
import com.example.clubfocus.data.entity.TipoCuota
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Suppress("unused")
class CuotasDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

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
        clienteId: Int,
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

        db.beginTransaction()
        try {
            val filasActualizadas = db.update(
                "cuotas",
                valores,
                "id = ?",
                arrayOf(cuotaId.toString())
            )

            if (filasActualizadas > 0) {
                val valoresCliente = ContentValues().apply {
                    put("activo", 1)
                }

                db.update(
                    "clientes",
                    valoresCliente,
                    "id = ?",
                    arrayOf(clienteId.toString())
                )
            }

            db.setTransactionSuccessful()
            return filasActualizadas

        } finally {
            db.endTransaction()
        }
    }

    fun tieneCuotaVigente(dniCliente: String): Boolean {
        val db = readableDatabase

        return db.rawQuery(
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
        ).use { cursor ->
            if (!cursor.moveToFirst()) {
                return@use false
            }

            val esSocio = cursor.getInt(cursor.getColumnIndexOrThrow("es_socio")) == 1
            val fechaPagoTexto = cursor.getString(cursor.getColumnIndexOrThrow("fecha_pago"))

            if (fechaPagoTexto.isNullOrBlank()) {
                return@use false
            }

            val fechaPago = try {
                LocalDate.parse(
                    fechaPagoTexto,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            } catch (_: DateTimeParseException) {
                try {
                    LocalDate.parse(fechaPagoTexto)
                } catch (_: DateTimeParseException) {
                    return@use false
                }
            }

            val hoy = LocalDate.now()
            val fechaFinVigencia = if (esSocio) {
                fechaPago.plusMonths(1)
            } else {
                fechaPago
            }

            hoy.isBefore(fechaFinVigencia)
        }
    }

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

        cursor.use { c ->
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            while (c.moveToNext()) {
                val fechaPagoTexto = c.getString(c.getColumnIndexOrThrow("fecha_pago"))
                val medioPagoTexto = c.getString(c.getColumnIndexOrThrow("medio_pago"))
                val promocionTexto = c.getString(c.getColumnIndexOrThrow("promocion"))

                val cuota = Cuota(
                    id = c.getInt(c.getColumnIndexOrThrow("id")),
                    clienteId = c.getInt(c.getColumnIndexOrThrow("cliente_id")),
                    tipo = TipoCuota.valueOf(c.getString(c.getColumnIndexOrThrow("tipo"))),
                    monto = c.getDouble(c.getColumnIndexOrThrow("monto")),
                    montoFinal = c.getDouble(c.getColumnIndexOrThrow("monto_final")),
                    fecha = fechaPagoTexto?.let { fecha -> LocalDate.parse(fecha, formatter) },
                    fechaVencimiento = LocalDate.parse(c.getString(c.getColumnIndexOrThrow("fecha_vencimiento")), formatter),
                    estado = EstadoCuota.valueOf(c.getString(c.getColumnIndexOrThrow("estado"))),
                    medioPago = if (!medioPagoTexto.isNullOrBlank()) MedioPago.valueOf(medioPagoTexto) else null,
                    promocion = if (!promocionTexto.isNullOrBlank()) Promocion.valueOf(promocionTexto) else null
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

        cursor.use { c ->
            if (c.moveToFirst()) {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val fechaPagoTexto = c.getString(c.getColumnIndexOrThrow("fecha_pago"))
                val medioPagoTexto = c.getString(c.getColumnIndexOrThrow("medio_pago"))
                val promocionTexto = c.getString(c.getColumnIndexOrThrow("promocion"))

                return Cuota(
                    id = c.getInt(c.getColumnIndexOrThrow("id")),
                    clienteId = c.getInt(c.getColumnIndexOrThrow("cliente_id")),
                    tipo = TipoCuota.valueOf(c.getString(c.getColumnIndexOrThrow("tipo"))),
                    monto = c.getDouble(c.getColumnIndexOrThrow("monto")),
                    montoFinal = c.getDouble(c.getColumnIndexOrThrow("monto_final")),
                    fecha = fechaPagoTexto?.let { fecha -> LocalDate.parse(fecha, formatter) },
                    fechaVencimiento = LocalDate.parse(c.getString(c.getColumnIndexOrThrow("fecha_vencimiento")), formatter),
                    estado = EstadoCuota.valueOf(c.getString(c.getColumnIndexOrThrow("estado"))),
                    medioPago = if (!medioPagoTexto.isNullOrBlank()) MedioPago.valueOf(medioPagoTexto) else null,
                    promocion = if (!promocionTexto.isNullOrBlank()) Promocion.valueOf(promocionTexto) else null
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

        cursor.use { c ->
            while (c.moveToNext()) {
                val fechaVencimiento = LocalDate.parse(c.getString(c.getColumnIndexOrThrow("fecha_vencimiento")), formatter)

                if (!fechaVencimiento.isAfter(hoy)) {
                    val fechaPagoTexto = c.getString(c.getColumnIndexOrThrow("fecha_pago"))
                    val medioPagoTexto = c.getString(c.getColumnIndexOrThrow("medio_pago"))
                    val promocionTexto = c.getString(c.getColumnIndexOrThrow("promocion"))

                    cuotas.add(
                        Cuota(
                            id = c.getInt(c.getColumnIndexOrThrow("id")),
                            clienteId = c.getInt(c.getColumnIndexOrThrow("cliente_id")),
                            tipo = TipoCuota.valueOf(c.getString(c.getColumnIndexOrThrow("tipo"))),
                            monto = c.getDouble(c.getColumnIndexOrThrow("monto")),
                            montoFinal = c.getDouble(c.getColumnIndexOrThrow("monto_final")),
                            fecha = fechaPagoTexto?.let { fecha -> LocalDate.parse(fecha, formatter) },
                            fechaVencimiento = fechaVencimiento,
                            estado = EstadoCuota.valueOf(c.getString(c.getColumnIndexOrThrow("estado"))),
                            medioPago = if (!medioPagoTexto.isNullOrBlank()) MedioPago.valueOf(medioPagoTexto) else null,
                            promocion = if (!promocionTexto.isNullOrBlank()) Promocion.valueOf(promocionTexto) else null
                        )
                    )
                }
            }
        }

        return cuotas
    }

    fun obtenerHistorialPagos(): List<PagoHistorialItem> {
        val db = readableDatabase
        val lista = mutableListOf<PagoHistorialItem>()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val consulta = """
            SELECT 
                q.id AS cuota_id,
                q.cliente_id,
                c.nombre,
                c.apellido,
                c.dni,
                q.tipo,
                q.monto_final,
                q.fecha_pago,
                q.fecha_vencimiento,
                q.medio_pago,
                q.estado
            FROM cuotas q
            INNER JOIN clientes c ON q.cliente_id = c.id
            ORDER BY q.id DESC
        """.trimIndent()

        val cursor = db.rawQuery(consulta, null)

        cursor.use { c ->
            while (c.moveToNext()) {
                val nombre = c.getString(c.getColumnIndexOrThrow("nombre")) ?: ""
                val apellido = c.getString(c.getColumnIndexOrThrow("apellido")) ?: ""
                val fechaPagoTexto = c.getString(c.getColumnIndexOrThrow("fecha_pago"))
                val medioPagoTexto = c.getString(c.getColumnIndexOrThrow("medio_pago"))

                lista.add(
                    PagoHistorialItem(
                        cuotaId = c.getInt(c.getColumnIndexOrThrow("cuota_id")),
                        clienteId = c.getInt(c.getColumnIndexOrThrow("cliente_id")),
                        nombreCompleto = "$nombre $apellido".trim(),
                        dni = c.getString(c.getColumnIndexOrThrow("dni")) ?: "",
                        tipo = TipoCuota.valueOf(c.getString(c.getColumnIndexOrThrow("tipo"))),
                        montoFinal = c.getDouble(c.getColumnIndexOrThrow("monto_final")),
                        fechaVencimiento = LocalDate.parse(
                            c.getString(c.getColumnIndexOrThrow("fecha_vencimiento")),
                            formatter
                        ),
                        fechaPago = fechaPagoTexto?.let { fecha -> LocalDate.parse(fecha, formatter) },
                        estado = EstadoCuota.valueOf(c.getString(c.getColumnIndexOrThrow("estado"))),
                        medioPago = if (!medioPagoTexto.isNullOrBlank()) MedioPago.valueOf(medioPagoTexto) else null
                    )
                )
            }
        }

        return lista
    }
}