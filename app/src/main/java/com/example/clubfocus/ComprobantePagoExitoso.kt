package com.example.clubfocus

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubfocus.data.repository.ClienteRepository
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

        val txtNombreCompleto =
            findViewById<TextView>(R.id.txtNombre)

        val txtDNI =
            findViewById<TextView>(R.id.txtDni)

        val txtConcepto =
            findViewById<TextView>(R.id.txtConcepto)

        val txtFormaPago =
            findViewById<TextView>(R.id.txtFormaPago)

        val txtDetalle =
            findViewById<TextView>(R.id.txtDetalle)

        // Se cargan los datos para visualizarlos

        // FECHA ACTUAL
        val fechaActual = SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())

        txtFecha.text = "Fecha: $fechaActual"

        // Buscamos al cliente por su DNI
        val dni =
            intent.getStringExtra("clienteDNI") ?: ""

        if (dni.isNotEmpty()) {

            val cliente = ClienteRepository.buscarPorDni(dni)

            if (cliente != null) {

                txtNombreCompleto.text =
                    "A nombre de: ${cliente.nombre} ${cliente.apellido}"

                txtDNI.text =
                    "DNI: $dni"

                if (cliente.cuotas.isNotEmpty()) {

                    val cuota = cliente.cuotas[0]

                    txtTotal.text =
                        "$ ${cuota.montoFinal}"

                    txtFormaPago.text =
                        "Medio de pago: ${cuota.medioPago}"

                    txtConcepto.text =
                        if (cliente.esSocio) {
                            "En concepto de: Cuota mensual."
                        } else {
                            "En concepto de: Cuota diaria."
                        }
                }
            } else {

                Toast.makeText(
                    this,
                    "No se encontró el cliente",
                    Toast.LENGTH_SHORT
                ).show()
            }


        // detalle de cuota
        val detalles =
            intent.getStringExtra("detallePago") ?: ""

        txtDetalle.text = "Detalle: ${detalles}"

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
}