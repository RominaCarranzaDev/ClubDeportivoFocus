package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvCerrarSesion = findViewById<TextView>(R.id.tvCerrarSesion)

        tvCerrarSesion.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)

            // 2. Iniciamos la pantalla de Login
            startActivity(intent)

            // 3. Cerramos la pantalla del Menú para que no quede abierta de fondo
            finish()
        }
    }
}