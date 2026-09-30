package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ListadoCarnetActivity : AppCompatActivity() {

    private var carnetSeleccionado: LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_listado_carnet)

        val txtVolver =
            findViewById<TextView>(R.id.txtVolver)

        val etBuscador =
            findViewById<EditText>(R.id.etBuscador)

        val btnBuscar =
            findViewById<ImageView>(R.id.btnBuscar)

        val btnImprimir =
            findViewById<Button>(R.id.btnImprimirCarnet)

        val carnetJuan =
            findViewById<LinearLayout>(R.id.carnetJuan)

        val carnetMartin =
            findViewById<LinearLayout>(R.id.carnetMartin)

        val carnetMicaela =
            findViewById<LinearLayout>(R.id.carnetMicaela)

        val carnetCamila =
            findViewById<LinearLayout>(R.id.carnetCamila)


        // VOLVER
        txtVolver.setOnClickListener {
            finish()
        }


        // SELECCIONAR CARNETS TOCÁNDOLOS
        seleccionarCarnet(carnetJuan)
        seleccionarCarnet(carnetMartin)
        seleccionarCarnet(carnetMicaela)
        seleccionarCarnet(carnetCamila)


        // BUSCAR TOCANDO LA LUPA
        btnBuscar.setOnClickListener {

            val dni = etBuscador.text.toString().trim()

            if (dni.isEmpty()) {

                Toast.makeText(
                    this,
                    "Ingresá un DNI",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                buscarCarnetPorDni(dni)
            }
        }


        // BUSCAR TAMBIÉN CON ENTER DEL TECLADO
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

                    buscarCarnetPorDni(dni)
                }

                true

            } else {
                false
            }
        }


        // BOTÓN IMPRIMIR
        btnImprimir.setOnClickListener {

            if (carnetSeleccionado == null) {

                Toast.makeText(
                    this,
                    "Seleccioná un carnet primero",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val intent = Intent(
                    this,
                    CarnetActivity::class.java
                )

                startActivity(intent)
            }
        }
    }


    // SELECCIONAR UN CARNET AL TOCARLO
    private fun seleccionarCarnet(carnet: LinearLayout) {

        carnet.setOnClickListener {

            // Quitar selección anterior
            carnetSeleccionado?.setBackgroundResource(
                R.drawable.bg_card_carnet
            )

            // Guardar nuevo carnet
            carnetSeleccionado = carnet

            // Marcar carnet seleccionado
            carnet.setBackgroundResource(
                R.drawable.bg_card_carnet_seleccionado
            )
        }
    }


    // BÚSQUEDA POR DNI
    // Son DNI de prueba porque los datos están hardcodeados
    private fun buscarCarnetPorDni(dni: String) {

        when (dni) {

            "12345678" -> {

                seleccionarCarnetAutomaticamente(
                    findViewById(R.id.carnetJuan)
                )
            }

            "23456789" -> {

                seleccionarCarnetAutomaticamente(
                    findViewById(R.id.carnetMartin)
                )
            }

            "34567890" -> {

                seleccionarCarnetAutomaticamente(
                    findViewById(R.id.carnetMicaela)
                )
            }

            "45678901" -> {

                seleccionarCarnetAutomaticamente(
                    findViewById(R.id.carnetCamila)
                )
            }

            else -> {

                Toast.makeText(
                    this,
                    "No se encontró ningún carnet con ese DNI",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    // SELECCIONAR AUTOMÁTICAMENTE EL RESULTADO
    private fun seleccionarCarnetAutomaticamente(
        carnet: LinearLayout
    ) {

        // Quitar selección anterior
        carnetSeleccionado?.setBackgroundResource(
            R.drawable.bg_card_carnet
        )

        // Guardar nuevo seleccionado
        carnetSeleccionado = carnet

        // Marcarlo visualmente
        carnet.setBackgroundResource(
            R.drawable.bg_card_carnet_seleccionado
        )

        Toast.makeText(
            this,
            "Carnet encontrado",
            Toast.LENGTH_SHORT
        ).show()
    }
}