package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.time.LocalDate

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
                dni_cliente TEXT NOT NULL,
                tipo TEXT NOT NULL,
                monto REAL NOT NULL,
                monto_final REAL NOT NULL,
                fecha_pago TEXT NOT NULL,
                fecha_vencimiento TEXT NOT NULL,
                medio_pago TEXT NOT NULL,
                promocion TEXT NOT NULL,
                estado TEXT NOT NULL
            )
        """.trimIndent())
    }

    fun registrarPago(
        dniCliente: String,
        tipo: String,
        monto: Double,
        montoFinal: Double,
        fechaPago: String,
        fechaVencimiento: String,
        medioPago: String,
        estado: String = "PAGADA",
        promocion: String
    ): Long {
        val db = writableDatabase

        val valores = ContentValues().apply {
            put("dni_cliente", dniCliente)
            put("tipo", tipo)
            put("monto", monto)
            put("monto_final", montoFinal)
            put("fecha_pago", fechaPago)
            put("fecha_vencimiento", fechaVencimiento)
            put("medio_pago", medioPago)
            put("estado", estado)
            put("promocion", promocion)
        }

        return db.insert("cuotas", null, valores)
    }

    fun tieneCuotaVigente(dniCliente: String): Boolean {
        val db = readableDatabase
        val hoy = LocalDate.now().toString()

        val cursor = db.rawQuery(
            "SELECT id FROM cuotas WHERE dni_cliente = ? AND estado = 'PAGADA' AND fecha_vencimiento >= ?",
            arrayOf(dniCliente, hoy)
        )
        val tieneVigente = cursor.count > 0
        cursor.close()
        return tieneVigente
    }

    // Objeto simple para transportar los datos del pago al comprobante
    data class UltimoPago(
        val montoFinal: Double,
        val medioPago: String
    )

    fun obtenerUltimoPago(dniCliente: String): UltimoPago? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT monto_final, medio_pago FROM cuotas WHERE dni_cliente = ? ORDER BY id DESC LIMIT 1",
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
}