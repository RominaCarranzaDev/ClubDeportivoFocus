package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class CarnetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carnet)
        val btnVolver = findViewById<TextView>(R.id.txtVolver)
        val btnImprimir = findViewById<ImageView>(R.id.ImgImprimir)


        btnVolver.setOnClickListener {
            finish()
        }

        btnImprimir.setOnClickListener {
            Toast.makeText(
                this,
                "ToDo Impresión de Carnet...",
                Toast.LENGTH_LONG
            ).show()
        }

    }
}