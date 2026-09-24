package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvCerrarSesion = findViewById<TextView>(R.id.tvCerrarSesion)
        val btnRegistrarSocio = findViewById<Button>(R.id.btnRegistrarSocio)
        val btnCobrarCuota = findViewById<Button>(R.id.btnCobroCuotas)


        tvCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            // 2. Iniciamos la pantalla de Login
            startActivity(intent)

            // 3. Cerramos la pantalla del Menú para que no quede abierta de fondo
            finish()
        }

        btnRegistrarSocio.setOnClickListener {
            val intent = Intent(this, RegistrarSocioActivity::class.java)

            startActivity(intent)
        }

        btnCobrarCuota.setOnClickListener {
            val intentCobro = Intent(this, CobrarCuotaActivity::class.java)

            startActivity(intentCobro)
        }
    }
}