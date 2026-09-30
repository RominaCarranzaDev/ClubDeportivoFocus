package com.example.clubfocus

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComprobantePagoExitoso : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_comprobante_pago_exitoso)

        val txtVolver =
            findViewById<TextView>(R.id.txtVolver)

        val btnImprimir =
            findViewById<ImageView>(R.id.btnImprimirComprobante)

        val txtFecha =
            findViewById<TextView>(R.id.txtFecha)

        val txtTotal =
            findViewById<TextView>(R.id.txtTotal)

        // FECHA ACTUAL

        val fechaActual = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())

        txtFecha.text = "Fecha: $fechaActual"

        // TOTAL DEL PAGO

        val total =
            intent.getStringExtra("total") ?: ""

        txtTotal.text = total

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