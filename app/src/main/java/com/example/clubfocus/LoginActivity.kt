package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)

        btnIngresar.setOnClickListener {
            val usuarioEscrito = etUsuario.text.toString()
            val claveEscrita = etContrasena.text.toString()

            if (usuarioEscrito.isEmpty() || claveEscrita.isEmpty()) {
                Toast.makeText(this, "Por favor, completa usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {

                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)

                // (Opcional) Destruimos la pantalla de Login para que si toca "Atrás" en el cel no vuelva al formulario
                finish()
            }
        }
    }
}