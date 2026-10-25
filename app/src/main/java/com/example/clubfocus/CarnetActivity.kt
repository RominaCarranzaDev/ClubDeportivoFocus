package com.example.clubfocus

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubfocus.helperDB.CarnetDBHelper


class CarnetActivity : AppCompatActivity() {

    private lateinit var carnetDBHelper: CarnetDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carnet)

        carnetDBHelper = CarnetDBHelper(this)

        val btnVolver =
            findViewById<TextView>(R.id.txtVolver)

        val btnImprimir =
            findViewById<ImageView>(R.id.ImgImprimir)

        val txtNroSocio =
            findViewById<TextView>(R.id.txtNroSocio)

        val txtNombreSocio =
            findViewById<TextView>(R.id.txtNombreSocio)

        val txtFechaEmision =
            findViewById<TextView>(R.id.txtFechaEmision)

        val txtFechaVencimiento =
            findViewById<TextView>(R.id.txtFechaVencimiento)

        // Dejar los campos vacíos inicialmente.
        txtNroSocio.text = ""
        txtNombreSocio.text = ""
        txtFechaEmision.text = ""
        txtFechaVencimiento.text = ""

        // Deshabilitar impresión hasta encontrar un carnet.
        btnImprimir.isEnabled = false

        // Recibir el ID enviado desde las distintas activitys.
        val carnetId = intent.getIntExtra("carnet_id", -1)
        val dniCliente = intent.getStringExtra("clienteDNI")

        val carnet = when {
            carnetId != -1 -> {
                carnetDBHelper.buscarCarnetConDatosPorId(carnetId)
            }

            !dniCliente.isNullOrBlank() -> {
                carnetDBHelper.buscarCarnetPorDni(dniCliente)
            }

            else -> null
        }

        if (carnet != null) {

            txtNroSocio.text = carnet.nroSocio

            txtNombreSocio.text =
                "${carnet.nombre} ${carnet.apellido}"

            val formatter =
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")

            txtFechaEmision.text =
                carnet.fechaEmision.format(formatter)

            txtFechaVencimiento.text =
                carnet.fechaVencimiento.format(formatter)

            btnImprimir.isEnabled = true

        } else {
            btnImprimir.isEnabled = false

            Toast.makeText(
                this,
                "No se encontró el carnet del socio",
                Toast.LENGTH_SHORT
            ).show()
        }


        btnVolver.setOnClickListener {
            finish()
        }

        btnImprimir.setOnClickListener {
            if (btnImprimir.isEnabled) {
                Toast.makeText(
                    this,
                    "Carnet enviado a la impresora",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
