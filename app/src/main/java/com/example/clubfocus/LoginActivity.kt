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
                Toast.makeText(this, "Por favor, completa usuario y contraseña", Toast.LENGTH_SHORT)
                    .show()
            } else {

                if (usuarioEscrito == "admin" && claveEscrita == "123456") {

                    // ¡Los datos son correctos! Hacemos el viaje
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)

                    // Destruimos la pantalla de Login
                    finish()

                } else {
                    // Si los campos tienen texto, pero le pifió a la contraseña o al correo:
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}