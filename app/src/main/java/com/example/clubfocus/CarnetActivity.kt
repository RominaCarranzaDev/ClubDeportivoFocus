package com.example.clubfocus

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubfocus.data.entity.Socio
import com.example.clubfocus.data.repository.CarnetRepository
import com.example.clubfocus.data.repository.ClienteRepository
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CarnetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_carnet)

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

        val socioDni =
            intent.getStringExtra("clienteDNI") ?: ""

        if (socioDni.isNotEmpty()) {

            val cliente = ClienteRepository.buscarPorDni(socioDni)

            if (cliente is Socio) {

                txtNombreSocio.text = "${cliente.nombre} ${cliente.apellido}"

                txtNroSocio.text = "${cliente.nroSocio}"

                val carnet = CarnetRepository.buscarPorNroSocio(cliente.nroSocio)

                if (carnet != null) {
                    txtFechaEmision.text = "${carnet.fechaEmision}"
                    txtFechaVencimiento.text = "${carnet.fechaVencimiento}"
                }
            }

        } else {
            // Recibir el socio seleccionado
            val nombreSocio =
                intent.getStringExtra("nombreSocio") ?: "Juan Perez"

            txtNombreSocio.text = nombreSocio

            // Fecha de emisión: hoy
            val formatoFecha =
                SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                )

            val fechaEmision = Date()

            txtFechaEmision.text =
                formatoFecha.format(fechaEmision)

            // Fecha de vencimiento: 30 días después
            val calendario = Calendar.getInstance()

            calendario.time = fechaEmision

            calendario.add(
                Calendar.DAY_OF_YEAR,
                30
            )

            txtFechaVencimiento.text =
                formatoFecha.format(calendario.time)
        }
        // Volver
        btnVolver.setOnClickListener {
            finish()
        }

        // Imprimir
        btnImprimir.setOnClickListener {
            Toast.makeText(
                this,
                "Carnet enviado a la impresora",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}