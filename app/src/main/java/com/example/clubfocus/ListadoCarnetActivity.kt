package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ListadoCarnetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listado_carnet)
        val txtVolver = findViewById<TextView>(R.id.txtVolver)

        txtVolver.setOnClickListener {
            finish()
        }
        val btnImprimir = findViewById<Button>(R.id.btnImprimirCarnet)

        btnImprimir.setOnClickListener {
            val intent = Intent(this, CarnetActivity::class.java)

            startActivity(intent)
        }

    }
}