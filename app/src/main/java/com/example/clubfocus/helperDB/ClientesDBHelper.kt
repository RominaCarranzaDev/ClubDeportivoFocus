package com.example.clubfocus.helperDB

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clubfocus.data.entity.Cliente
import com.example.clubfocus.data.entity.SocioCuotaItem
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class ClientesDBHelper(context: Context) : SQLiteOpenHelper(context, "ClubFocus.db", null, 1) {

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

        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaInscripcion = formatter.format(Date())

        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("apellido", apellido)
            put("dni", dni)
            put("fecha_nacimiento", fechaNacimiento)
            put("email", email)
            put("apto_fisico", if (aptoFisico) 1 else 0)
            put("fecha_inscripcion", fechaInscripcion)
            put("es_socio", if (esSocio) 1 else 0)
            put("nro_socio", nroSocioGenerado)
        }

        return db.insert("clientes", null, valores)
    }

    fun existeCliente(dni: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT dni FROM clientes WHERE dni = ?", arrayOf(dni))
        val existe = cursor.count > 0
        cursor.close()
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
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val fechaNacStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_nacimiento"))
            val fechaNacLocalDate = try {
                LocalDate.parse(fechaNacStr, formatter)
            } catch (_: Exception) {
                LocalDate.now()
            }
            val fechaInscripcionStr = cursor.getString(cursor.getColumnIndexOrThrow("fecha_inscripcion"))
            val fechaInscripcionLocalDate = LocalDate.parse(fechaInscripcionStr, formatter)

            clienteEncontrado = Cliente(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellido")),
                dni = cursor.getString(cursor.getColumnIndexOrThrow("dni")),
                fechaNacimiento = fechaNacLocalDate,
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                aptoFisico = cursor.getInt(cursor.getColumnIndexOrThrow("apto_fisico")) == 1,
                activo = cursor.getInt(cursor.getColumnIndexOrThrow("activo")) == 1,
                esSocio = cursor.getInt(cursor.getColumnIndexOrThrow("es_socio")) == 1,
                fechaInscripcion = fechaInscripcionLocalDate,
                nroSocio = cursor.getString(cursor.getColumnIndexOrThrow("nro_socio")) ?: ""
            )
        }

        cursor.close()
        return clienteEncontrado
    }

    fun buscarPorId(id: Int): Cliente? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM clientes WHERE id = ?", arrayOf(id.toString()))

        cursor.use {
            if (it.moveToFirst()) {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val fechaNacStr = it.getString(it.getColumnIndexOrThrow("fecha_nacimiento"))
                val fechaInscripcionStr = it.getString(it.getColumnIndexOrThrow("fecha_inscripcion"))
                val fechaNacLocalDate = LocalDate.parse(fechaNacStr, formatter)
                val fechaInscripcionLocalDate = LocalDate.parse(fechaInscripcionStr, formatter)

                return Cliente(
                    id = it.getInt(it.getColumnIndexOrThrow("id")),
                    nombre = it.getString(it.getColumnIndexOrThrow("nombre")),
                    apellido = it.getString(it.getColumnIndexOrThrow("apellido")),
                    dni = it.getString(it.getColumnIndexOrThrow("dni")),
                    fechaNacimiento = fechaNacLocalDate,
                    email = it.getString(it.getColumnIndexOrThrow("email")),
                    aptoFisico = it.getInt(it.getColumnIndexOrThrow("apto_fisico")) == 1,
                    activo = it.getInt(it.getColumnIndexOrThrow("activo")) == 1,
                    esSocio = it.getInt(it.getColumnIndexOrThrow("es_socio")) == 1,
                    fechaInscripcion = fechaInscripcionLocalDate,
                    nroSocio = it.getString(it.getColumnIndexOrThrow("nro_socio")) ?: ""
                )
            }
        }

        return null
    }

    fun obtenerVencimientosSocios(): List<SocioCuotaItem> {
        val db = readableDatabase
        val lista = mutableListOf<SocioCuotaItem>()

        val cursor = db.rawQuery(
            """
                SELECT
                    q.id AS cuota_id,
                    c.id AS cliente_id,
                    c.dni,
                    c.nombre,
                    c.apellido,
                    c.nro_socio,
                    c.activo,
                    q.fecha_vencimiento,
                    q.estado
                FROM clientes c
                INNER JOIN cuotas q
                    ON q.cliente_id = c.id
                WHERE c.es_socio = 1
                  AND UPPER(q.estado) IN ('PENDIENTE', 'VENCIDA')
                ORDER BY
                    CASE
                        WHEN q.fecha_vencimiento IS NULL
                             OR q.fecha_vencimiento = '' THEN 1
                        ELSE 0
                    END,
                    SUBSTR(q.fecha_vencimiento, 7, 4) DESC,
                    SUBSTR(q.fecha_vencimiento, 4, 2) DESC,
                    SUBSTR(q.fecha_vencimiento, 1, 2) DESC
            """.trimIndent(), null
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val nombre = c.getString(c.getColumnIndexOrThrow("nombre")) ?: ""
                val apellido = c.getString(c.getColumnIndexOrThrow("apellido")) ?: ""
                val nroSocio = c.getString(c.getColumnIndexOrThrow("nro_socio")) ?: ""
                val fechaVencimiento = c.getString(c.getColumnIndexOrThrow("fecha_vencimiento"))
                val estadoCuota = c.getString(c.getColumnIndexOrThrow("estado"))

                val socio = SocioCuotaItem(
                    cuotaId = c.getInt(c.getColumnIndexOrThrow("cuota_id")),
                    clienteId = c.getInt(c.getColumnIndexOrThrow("cliente_id")),
                    dni = c.getString(c.getColumnIndexOrThrow("dni")) ?: "",
                    nombreCompleto = "$nombre $apellido".trim(),
                    nroSocio = nroSocio,
                    fechaVencimiento = fechaVencimiento,
                    activo = c.getInt(c.getColumnIndexOrThrow("activo")) == 1,
                    estadoCuota = estadoCuota
                )

                lista.add(socio)
            }
        }
        return lista
    }
}