package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ConfirmarPagoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_confirmar_pago)

        val btnComprobante =
            findViewById<TextView>(R.id.btnImprimirComprobante)

        val btnCarnet =
            findViewById<TextView>(R.id.btnImprimirCarnet)

        val btnMenu =
            findViewById<TextView>(R.id.btnIrMenu)

        val esSocio =
            intent.getBooleanExtra("esSocio", false)

        val total =
            intent.getStringExtra("total") ?: ""

        btnCarnet.alpha = 1f

        // IR AL COMPROBANTE

        btnComprobante.setOnClickListener {

            val intent = Intent(
                this,
                ComprobantePagoExitoso::class.java
            )

            intent.putExtra(
                "total",
                total
            )

            startActivity(intent)
        }

        // IR AL CARNET

        btnCarnet.setOnClickListener {

            if (esSocio) {

                val intent = Intent(
                    this,
                    ListadoCarnetActivity::class.java
                )

                startActivity(intent)
            }
        }

        // VOLVER AL MENÚ

        btnMenu.setOnClickListener {

            val intent = Intent(
                this,
                HomeActivity::class.java
            )

            startActivity(intent)

            finish()
        }
    }
}