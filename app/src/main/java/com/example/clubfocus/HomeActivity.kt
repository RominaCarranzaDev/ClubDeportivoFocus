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

        // 1. Vinculamos el texto de Cerrar Sesion y los botones principales
        val tvCerrarSesion = findViewById<TextView>(R.id.tvCerrarSesion)
        val btnRegistrarSocio = findViewById<Button>(R.id.btnRegistrarSocio)
        val btnCobrarCuota = findViewById<Button>(R.id.btnCobroCuotas)
        val btnCarnets = findViewById<Button>(R.id.btnCarnets)

        // 2. Ventana emergente
        tvCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Está seguro que desea cerrar la sesión?")
                .setPositiveButton("Aceptar") { _, _ ->
                    // Redirigimos al Login al confirmar
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        // 3. Navegacion a Registrar Socio
        btnRegistrarSocio.setOnClickListener {
            val intent = Intent(this, RegistrarSocioActivity::class.java)
            startActivity(intent)
        }

        // 4. Navegacion a Cobro de Cuotas
        btnCobrarCuota.setOnClickListener {
            val intentCobro = Intent(this, CobrarCuotaActivity::class.java)
            startActivity(intentCobro)
        }

        // 5. Navegacion a Carnets
        btnCarnets.setOnClickListener {
            val intentListadoCarnet = Intent(this, ListadoCarnetActivity::class.java)
            startActivity(intentListadoCarnet)
        }
    }
}