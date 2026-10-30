package com.example.clubfocus

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubfocus.adapter.CarnetAdapter
import com.example.clubfocus.data.entity.CarnetListado
import com.example.clubfocus.helperDB.CarnetDBHelper

class ListadoCarnetActivity : AppCompatActivity() {

    private lateinit var rvCarnets: RecyclerView
    private lateinit var carnetAdapter: CarnetAdapter
    private lateinit var etBuscador: EditText
    private var todosLosCarnets: List<CarnetListado> = emptyList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listado_carnet)

        val carnetDBHelper = CarnetDBHelper(this)

        rvCarnets = findViewById(R.id.rvCarnets)
        etBuscador = findViewById(R.id.etBuscador)
        val txtVolver = findViewById<TextView>(R.id.txtVolver)

        txtVolver.setOnClickListener {
            finish()
        }

        // Configurar el RecyclerView
        carnetAdapter = CarnetAdapter(emptyList()) { carnet ->
            val intent = Intent(this, CarnetActivity::class.java)
            intent.putExtra("carnet_id", carnet.id)
            startActivity(intent)
        }

        rvCarnets.layoutManager = LinearLayoutManager(this)
        rvCarnets.adapter = carnetAdapter

        // Cargar todos los carnets
        todosLosCarnets = carnetDBHelper.obtenerCarnetsConDatosSocio()
        carnetAdapter.actualizarLista(todosLosCarnets)

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
                        buscarPorDni()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        // Búsqueda desde la acción de buscar del teclado
        etBuscador.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarPorDni()
                true
            } else {
                false
            }
        }
    }

    private fun buscarPorDni() {
        val dni = etBuscador.text.toString().trim()

        if (dni.isEmpty()) {
            carnetAdapter.actualizarLista(todosLosCarnets)
            return
        }

        val resultado = todosLosCarnets.filter {
            it.dni.contains(dni, ignoreCase = true)
        }

        carnetAdapter.actualizarLista(resultado)

        if (resultado.isEmpty()) {
            Toast.makeText(
                this,
                "No se encontraron carnets para ese DNI",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}