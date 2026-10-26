package com.example.clubfocus

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubfocus.adapter.HistorialPagoAdapter
import com.example.clubfocus.data.entity.PagoHistorialItem
import com.example.clubfocus.helperDB.CuotasDBHelper

class ListadoPagoActivity : AppCompatActivity() {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var adapter: HistorialPagoAdapter
    private lateinit var cuotasHelper: CuotasDBHelper
    private var todosLosPagos: List<PagoHistorialItem> = emptyList()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_pago)

        cuotasHelper = CuotasDBHelper(this)

        val btnVolver = findViewById<TextView>(R.id.txtVolver)
        val etBuscador = findViewById<EditText>(R.id.etBuscador)
        rvHistorial = findViewById(R.id.rvHistorialPagos)

        rvHistorial.layoutManager = LinearLayoutManager(this)
        adapter = HistorialPagoAdapter(emptyList())
        rvHistorial.adapter = adapter

        // 1. CARGA DIRECTA: Lista todos los pagos apenas ingresas a la pantalla
        todosLosPagos = cuotasHelper.obtenerHistorialPagos()
        adapter.actualizarLista(todosLosPagos)

        btnVolver.setOnClickListener {
            finish()
        }

        // 2. FILTRAR POR DNI
        fun filtrarPorDni() {
            val dni = etBuscador.text.toString().trim()

            // Si está vacío, restaura la lista con todos los socios
            if (dni.isEmpty()) {
                adapter.actualizarLista(todosLosPagos)
                return
            }

            // Si hay texto, filtra entre los miles de registros por coincidencia
            val resultado = todosLosPagos.filter {
                it.dni.contains(dni, ignoreCase = true)
            }

            adapter.actualizarLista(resultado)

            if (resultado.isEmpty()) {
                Toast.makeText(this, "No se encontraron pagos para ese DNI", Toast.LENGTH_SHORT).show()
            }
        }

        // Clic en la lupa
        etBuscador.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = etBuscador.compoundDrawables[2]

                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()

                    if (event.x >= etBuscador.width - etBuscador.paddingEnd - drawableWidth - 40) {
                        v.performClick()
                        filtrarPorDni()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        // Presionar la tecla de búsqueda en el teclado
        etBuscador.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filtrarPorDni()
                true
            } else {
                false
            }
        }
    }
}