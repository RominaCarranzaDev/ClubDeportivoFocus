package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Encabezado con datos desde SQLite / SharedPreferences
        val tvUsuario = findViewById<TextView>(R.id.tvUsuario)
        val sharedPref = getSharedPreferences("ClubFocusPrefs", MODE_PRIVATE)

        var nombreUsuario = intent.getStringExtra("usuarioLogueado") ?: ""
        var rolUsuario = intent.getStringExtra("rolUsuario") ?: ""

        if (nombreUsuario.isEmpty()) {
            nombreUsuario = sharedPref.getString("usuarioLogueado", "") ?: ""
        }
        if (rolUsuario.isEmpty()) {
            rolUsuario = sharedPref.getString("rolUsuario", "") ?: ""
        }

        if (nombreUsuario.isNotEmpty() && rolUsuario.isNotEmpty()) {
            val usuarioFormateado = nombreUsuario.replaceFirstChar { it.uppercase() }
            val rolFormateado = rolUsuario.replaceFirstChar { it.uppercase() }
            tvUsuario.text = "$usuarioFormateado • $rolFormateado"
        } else if (nombreUsuario.isNotEmpty()) {
            tvUsuario.text = nombreUsuario.replaceFirstChar { it.uppercase() }
        } else {
            tvUsuario.text = getString(R.string.home_user_role)
        }

        // Botones y cierre de sesión
        val tvCerrarSesion = findViewById<TextView>(R.id.tvCerrarSesion)
        val btnRegistrarSocio = findViewById<Button>(R.id.btnRegistrarSocio)
        val btnCobrarCuota = findViewById<Button>(R.id.btnCobroCuotas)
        val btnCarnets = findViewById<Button>(R.id.btnCarnets)
        val btnVencimientos = findViewById<Button>(R.id.btnVencimientos)
        val btnHistorialPago = findViewById<Button>(R.id.btnHistorialPagos)

        tvCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro que desea cerrar la sesión?")
                .setPositiveButton("Aceptar") { _, _ ->
                    sharedPref.edit().clear().apply()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnRegistrarSocio.setOnClickListener {
            val intent = Intent(this, RegistrarSocioActivity::class.java)
            startActivity(intent)
        }

        btnCobrarCuota.setOnClickListener {
            val intentCobro = Intent(this, CobrarCuotaActivity::class.java)
            startActivity(intentCobro)
        }

        btnCarnets.setOnClickListener {
            val intentListadoCarnet = Intent(this, ListadoCarnetActivity::class.java)
            startActivity(intentListadoCarnet)
        }

        btnVencimientos.setOnClickListener {
            val intentVencimientos = Intent(this, VencimientosActivity::class.java)
            startActivity(intentVencimientos)
        }

        btnHistorialPago.setOnClickListener {
            val intentHistorial = Intent(this, ListadoPagoActivity::class.java)
            startActivity(intentHistorial)
        }
    }
}