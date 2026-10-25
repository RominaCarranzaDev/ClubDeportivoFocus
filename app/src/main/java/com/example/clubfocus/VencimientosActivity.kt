package com.example.clubfocus

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubfocus.data.entity.SocioCuotaItem
import com.example.clubfocus.helperDB.ClientesDBHelper

class VencimientosActivity : AppCompatActivity() {

    private lateinit var adapter: SociosCuotaAdapter
    private lateinit var todasLasCuotas: List<SocioCuotaItem>

    private var cuotaSeleccionada: SocioCuotaItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vencimientos)

        val btnVolver = findViewById<TextView>(R.id.txtVolver)
        val etBuscador = findViewById<EditText>(R.id.etBuscador)
        val btnBuscar = findViewById<ImageView>(R.id.btnBuscar)
        val rvSocios = findViewById<RecyclerView>(R.id.rvSocios)

        btnVolver.setOnClickListener {
            finish()
        }

        // Configurar RecyclerView
        rvSocios.layoutManager = LinearLayoutManager(this)

        // Obtener las cuotas impagas desde SQLite
        val dbHelper = ClientesDBHelper(this)
        todasLasCuotas = dbHelper.obtenerVencimientosSocios()

        adapter = SociosCuotaAdapter(todasLasCuotas) { cuota ->
            cuotaSeleccionada = cuota

            Toast.makeText(
                this,
                "Cuota seleccionada: ${cuota.nroSocio}",
                Toast.LENGTH_SHORT
            ).show()
        }

        rvSocios.adapter = adapter

        // Buscar desde la lupa
        btnBuscar.setOnClickListener {
            buscarCuota(etBuscador.text.toString())
        }

        // Buscar desde el teclado
        etBuscador.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarCuota(etBuscador.text.toString())
                true
            } else {
                false
            }
        }
    }

    private fun buscarCuota(texto: String) {
        val dni = texto.trim()

        if (dni.isEmpty()) {
            adapter.actualizarLista(todasLasCuotas)
            return
        }

        val resultados = todasLasCuotas.filter {
            it.dni.contains(dni)
        }

        adapter.actualizarLista(resultados)

        if (resultados.isEmpty()) {
            Toast.makeText(
                this,
                "No se encontraron cuotas para ese DNI",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}