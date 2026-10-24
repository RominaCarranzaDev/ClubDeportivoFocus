package com.example.clubfocus

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubfocus.helperDB.ClientesDBHelper
import com.example.clubfocus.helperDB.CuotasDBHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComprobantePagoExitoso : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comprobante_pago_exitoso)

        val clientesHelper = ClientesDBHelper(this)
        val cuotasHelper = CuotasDBHelper(this)

        val txtVolver = findViewById<TextView>(R.id.txtVolver)
        val btnImprimir = findViewById<ImageView>(R.id.btnImprimirComprobante)
        val txtFecha = findViewById<TextView>(R.id.txtFecha)
        val txtTotal = findViewById<TextView>(R.id.txtTotal)
        val txtNombreCompleto = findViewById<TextView>(R.id.txtNombre)
        val txtDNI = findViewById<TextView>(R.id.txtDni)
        val txtConcepto = findViewById<TextView>(R.id.txtConcepto)
        val txtFormaPago = findViewById<TextView>(R.id.txtFormaPago)
        val txtDetalle = findViewById<TextView>(R.id.txtDetalle)

        // 1. FECHA ACTUAL
        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        txtFecha.text = "Fecha: $fechaActual"

        // 2. RECIBIR EXTRAS
        val dni = intent.getStringExtra("clienteDNI") ?: ""
        val detalles = intent.getStringExtra("detallePago") ?: ""
        txtDetalle.text = "Detalle: $detalles"

        // 3. CONSULTAR DATOS EN SQLITE
        if (dni.isNotEmpty()) {
            val cliente = clientesHelper.buscarPorDni(dni)
            val pago = cuotasHelper.obtenerUltimoPago(dni)

            if (cliente != null) {
                txtNombreCompleto.text = "A nombre de: ${cliente.nombre} ${cliente.apellido}"
                txtDNI.text = "DNI: $dni"

                txtConcepto.text = if (cliente.esSocio) {
                    "En concepto de: Cuota mensual."
                } else {
                    "En concepto de: Cuota diaria."
                }
            } else {
                Toast.makeText(this, "No se encontró el cliente", Toast.LENGTH_SHORT).show()
            }

            if (pago != null) {
                txtTotal.text = "$ ${pago.montoFinal}"
                txtFormaPago.text = "Medio de pago: ${pago.medioPago}"
            }
        }

        // VOLVER
        txtVolver.setOnClickListener {
            finish()
        }

        // IMPRIMIR
        btnImprimir.setOnClickListener {
            Toast.makeText(
                this,
                "Comprobante enviado a la impresora",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}