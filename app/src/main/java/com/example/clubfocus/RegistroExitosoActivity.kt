package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RegistroExitosoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_registro_exitoso)

        val btnPagarCuota = findViewById<TextView>(R.id.btnPagarCuota)
        val btnIrInicio = findViewById<TextView>(R.id.btnIrInicio)

        btnPagarCuota.setOnClickListener {
            val intent = Intent(this, CobrarCuotaActivity::class.java)
            startActivity(intent)
        }

        btnIrInicio.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }
}