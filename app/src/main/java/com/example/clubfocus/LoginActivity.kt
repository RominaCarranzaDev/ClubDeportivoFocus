package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.clubfocus.helperDB.ClubFocusDBHelper
import com.example.clubfocus.helperDB.UsuariosDBHelper

class LoginActivity : AppCompatActivity() {
    // Iniciamos la persistencia en la base de datos
    private lateinit var dbHelper: ClubFocusDBHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Nos aseguramos de crear toda la base de datos
        dbHelper = ClubFocusDBHelper(this)
        dbHelper.inicializarBaseDatos()

        // Helper de la tabla Usuarios
        val usuariosHelper = UsuariosDBHelper(this)

        val tvOlvidoPass = findViewById<TextView>(R.id.tvOlvidasteContrasena)

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val etUsuario = findViewById<EditText>(R.id.etUsuario)
        val etContrasena = findViewById<EditText>(R.id.etContrasena)

        btnIngresar.setOnClickListener {
            val usuarioEscrito = etUsuario.text.toString().trim()
            val claveEscrita = etContrasena.text.toString().trim()

            if (usuarioEscrito.isEmpty() || claveEscrita.isEmpty()) {
                Toast.makeText(this, "Por favor, completa usuario y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Consultamos a la base de datos si existe y qué rol tiene
            val rol = usuariosHelper.obtenerRol(usuarioEscrito, claveEscrita)

            if (rol != null) {
                val sharedPref = getSharedPreferences("ClubFocusPrefs", MODE_PRIVATE)
                sharedPref.edit()
                    .putString("usuarioLogueado", usuarioEscrito)
                    .putString("rolUsuario", rol)
                    .apply()

                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("usuarioLogueado", usuarioEscrito)
                    putExtra("rolUsuario", rol)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Usuario y/o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            }
        }

        tvOlvidoPass.setOnClickListener {
            Toast.makeText(
                this,
                "Por favor, contacta con el área de sistemas del club",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}