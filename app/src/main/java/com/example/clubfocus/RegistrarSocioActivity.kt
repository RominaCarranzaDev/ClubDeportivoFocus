package com.example.clubfocus

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegistrarSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_socio)

        // 1. Boton Volver
        val tvVolver = findViewById<TextView>(R.id.tvVolver)
        tvVolver.setOnClickListener {
            finish()
        }

        // 2. Vincular componentes del formulario:
        val rbSocio = findViewById<RadioButton>(R.id.rbSocio)
        val cbAptoFisico = findViewById<CheckBox>(R.id.cbAptoFisico)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etDni = findViewById<EditText>(R.id.etDni)
        val btnGuardarRegistro = findViewById<Button>(R.id.btnGuardarRegistro)

        // 3. Accion Boton Guardar
        btnGuardarRegistro.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()

            val presentoApto = cbAptoFisico.isChecked
            val esSocio = rbSocio.isChecked

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {
                Toast.makeText(this, "Por favor, completa nombre, apellido y DNI", Toast.LENGTH_SHORT).show()
            } else {
                val tipoUsuario = if (esSocio) "Socio" else "No Socio"
                val mensajeConfirmacion = "$tipoUsuario $nombre $apellido registrado correctamente"

                Toast.makeText(this, mensajeConfirmacion, Toast.LENGTH_SHORT).show()
            }
        }
    }

}