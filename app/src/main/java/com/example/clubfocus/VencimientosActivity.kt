package com.example.clubfocus

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class VencimientosActivity : AppCompatActivity() {

    private var socioSeleccionado: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_vencimientos)

        val btnVolver =
            findViewById<TextView>(R.id.txtVolver)

        val etBuscador =
            findViewById<EditText>(R.id.etBuscador)

        val btnBuscar =
            findViewById<ImageView>(R.id.btnBuscar)

        val socio1001 =
            findViewById<LinearLayout>(R.id.socio1001)

        val socio1002 =
            findViewById<LinearLayout>(R.id.socio1002)

        val socio1003 =
            findViewById<LinearLayout>(R.id.socio1003)

        val socio1004 =
            findViewById<LinearLayout>(R.id.socio1004)

        btnVolver.setOnClickListener {
            finish()
        }

        // También se puede seleccionar tocando el socio
        seleccionarSocio(socio1001)
        seleccionarSocio(socio1002)
        seleccionarSocio(socio1003)
        seleccionarSocio(socio1004)

        // Lupa del costado
        btnBuscar.setOnClickListener {

            val dni = etBuscador.text.toString().trim()

            if (dni.isEmpty()) {

                Toast.makeText(
                    this,
                    "Ingresá un DNI",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                buscarSocioPorDni(dni)
            }
        }

        // Lupa del teclado
        etBuscador.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val dni = etBuscador.text.toString().trim()

                if (dni.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Ingresá un DNI",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    buscarSocioPorDni(dni)
                }

                true

            } else {

                false
            }
        }
    }

    private fun seleccionarSocio(
        socio: LinearLayout
    ) {

        socio.setOnClickListener {

            socioSeleccionado?.setBackgroundResource(
                R.drawable.bg_borde_card
            )

            socioSeleccionado = socio

            socio.setBackgroundResource(
                R.drawable.bg_card_carnet_seleccionado
            )
        }
    }

    private fun buscarSocioPorDni(dni: String) {

        when (dni) {

            "12345678" -> {

                seleccionarSocioAutomaticamente(
                    findViewById(R.id.socio1001)
                )
            }

            "23456789" -> {

                seleccionarSocioAutomaticamente(
                    findViewById(R.id.socio1002)
                )
            }

            "34567890" -> {

                seleccionarSocioAutomaticamente(
                    findViewById(R.id.socio1003)
                )
            }

            "45678901" -> {

                seleccionarSocioAutomaticamente(
                    findViewById(R.id.socio1004)
                )
            }

            else -> {

                Toast.makeText(
                    this,
                    "No se encontró ningún socio con ese DNI",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun seleccionarSocioAutomaticamente(
        socio: LinearLayout
    ) {

        socioSeleccionado?.setBackgroundResource(
            R.drawable.bg_borde_card
        )

        socioSeleccionado = socio

        socio.setBackgroundResource(
            R.drawable.bg_card_carnet_seleccionado
        )

        Toast.makeText(
            this,
            "Socio encontrado",
            Toast.LENGTH_SHORT
        ).show()
    }
}