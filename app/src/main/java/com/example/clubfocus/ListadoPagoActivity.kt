package com.example.clubfocus

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class ListadoPagoActivity : AppCompatActivity() {

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_pago)

        val btnVolver = findViewById<TextView>(R.id.txtVolver)
        val etBuscador = findViewById<EditText>(R.id.etBuscador)
        val listadoPago = findViewById<LinearLayout>(R.id.listadoPago)

        listadoPago.visibility = View.GONE

        btnVolver.setOnClickListener {
            finish()
        }

        // Carga los pagos
        fun buscarSocio(etBuscador: EditText) {
            val dni = etBuscador.text.toString().trim()

            if (dni.isEmpty()) {
                Toast.makeText(
                    this,
                    "Ingresá un DNI",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            // Avisar que está buscando
            Toast.makeText(
                this,
                "Buscando socio...",
                Toast.LENGTH_SHORT
            ).show()

            when (dni) {
                "12345678" -> {
                    Toast.makeText(
                        this,
                        "Mostrando pagos de Juan Perez",
                        Toast.LENGTH_SHORT
                    ).show()
                    listadoPago.visibility = View.VISIBLE
                }

                "11111111" -> {
                    Toast.makeText(
                        this,
                        "Mostrando pagos de Martin Rodriguez",
                        Toast.LENGTH_SHORT
                    ).show()
                    listadoPago.visibility = View.VISIBLE
                }

                "22222222" -> {
                    Toast.makeText(
                        this,
                        "Mostrando pagos de Micaela Lopez",
                        Toast.LENGTH_SHORT
                    ).show()
                    listadoPago.visibility = View.VISIBLE
                }

                "33333333" -> {
                    Toast.makeText(
                        this,
                        "Mostrando pagos de Camila Andrada",
                        Toast.LENGTH_SHORT
                    ).show()
                    listadoPago.visibility = View.VISIBLE
                }

                else -> {
                    Toast.makeText(
                        this,
                        "No se encontró ningún socio con ese DNI",
                        Toast.LENGTH_SHORT
                    ).show()
                    listadoPago.visibility = View.GONE
                }
            }
        }

        // Clic sobre el icono de la lupa integrado en el EditText
        etBuscador.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = etBuscador.compoundDrawables[2]

                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()

                    if (
                        event.x >= etBuscador.width -
                        etBuscador.paddingEnd -
                        drawableWidth - 40
                    ) {
                        v.performClick()
                        buscarSocio(etBuscador)
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        // Buscar desde el teclado
        etBuscador.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarSocio(etBuscador)
                true
            } else {
                false
            }
        }
    }
}