
package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.appcompat.app.AppCompatActivity

class RegistrarSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_socio)

        // 1. Botón Volver
        val tvVolver = findViewById<TextView>(R.id.tvVolver)

        tvVolver.setOnClickListener {
            finish()
        }

        // 2. Vincular componentes del formulario
        val rbSocio = findViewById<RadioButton>(R.id.rbSocio)
        val cbAptoFisico = findViewById<CheckBox>(R.id.cbAptoFisico)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etDni = findViewById<EditText>(R.id.etDni)
        val btnGuardarRegistro = findViewById<Button>(R.id.btnGuardarRegistro)
        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)

        // Agregar / al escribir la fecha de nacimiento

        etFechaNacimiento.doAfterTextChanged { texto ->
            val cadena = texto.toString()

            if ((cadena.length == 2 || cadena.length == 5) && !cadena.endsWith("/")) {
                etFechaNacimiento.setText("$cadena/")

                etFechaNacimiento.setSelection(etFechaNacimiento.text.length)
            }
        }


        // 3. Acción Botón Guardar
        btnGuardarRegistro.setOnClickListener {

            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()

            val presentoApto = cbAptoFisico.isChecked
            val esSocio = rbSocio.isChecked

            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty()) {

                Toast.makeText(
                    this,
                    "Por favor, completa nombre, apellido y DNI",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (!presentoApto) {

                Toast.makeText(
                    this,
                    "Debe presentar el Apto Físico para registrarse",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                val tipoUsuario = if (esSocio) "Socio" else "No Socio"

                val mensajeConfirmacion =
                    "$tipoUsuario $nombre $apellido registrado correctamente"

                Toast.makeText(
                    this,
                    mensajeConfirmacion,
                    Toast.LENGTH_SHORT
                ).show()

                // Abrir pantalla de Registro Exitoso
                val intent = Intent(
                    this,
                    RegistroExitosoActivity::class.java
                )

                startActivity(intent)

                // Cerrar la pantalla de registro
                finish()
            }
        }
    }
}
