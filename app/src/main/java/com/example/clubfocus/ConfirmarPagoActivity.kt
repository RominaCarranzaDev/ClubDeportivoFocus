package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
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


        // Recibir si el cliente es socio
        val esSocio =
            intent.getBooleanExtra("esSocio", false)


        // El carnet solo se puede usar para socios
        if (esSocio) {

            btnCarnet.alpha = 1f

        } else {

            btnCarnet.alpha = 0.5f
        }


        // IMPRIMIR COMPROBANTE
        btnComprobante.setOnClickListener {

            Toast.makeText(
                this,
                "Preparando comprobante...",
                Toast.LENGTH_SHORT
            ).show()

            // Acá después podés abrir la pantalla
            // del comprobante.
        }


        // IMPRIMIR CARNET
        btnCarnet.setOnClickListener {

//            Cuando se complete el flujo va a poder saber si es socio o no
            val intent = Intent(this, CarnetActivity::class.java)

            startActivity(intent)
            if (esSocio) {

                Toast.makeText(
                    this,
                    "Preparando carnet...",
                    Toast.LENGTH_SHORT
                ).show()

                // Acá después podés abrir la pantalla
                // de impresión del carnet.

            }
        }


        // IR AL MENÚ
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