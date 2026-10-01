package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.clubfocus.data.repository.ClienteRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RegistrarSocioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registrar_socio)

        // 1. Botón Volver (cierra la pantalla actual)
        val tvVolver = findViewById<TextView>(R.id.tvVolver)
        tvVolver.setOnClickListener {
            finish()
        }

        // 2. Vinculamos todos los componentes visuales del XML
        val rbSocio = findViewById<RadioButton>(R.id.rbSocio)
        val cbAptoFisico = findViewById<CheckBox>(R.id.cbAptoFisico)
        val etNombre = findViewById<EditText>(R.id.etNombre)
        val etApellido = findViewById<EditText>(R.id.etApellido)
        val etDni = findViewById<EditText>(R.id.etDni)
        val etFechaNacimiento = findViewById<EditText>(R.id.etFechaNacimiento)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val btnGuardarRegistro = findViewById<Button>(R.id.btnGuardarRegistro)

        // 3. Formateador automático para la fecha (agrega las /)
        etFechaNacimiento.doAfterTextChanged { texto ->
            val cadena = texto.toString()
            if ((cadena.length == 2 || cadena.length == 5) && !cadena.endsWith("/")) {
                etFechaNacimiento.setText("$cadena/")
                etFechaNacimiento.setSelection(etFechaNacimiento.text.length)
            }
        }

        // 4. Acción del Botón Guardar Registro (UN SOLO escuchador)
        btnGuardarRegistro.setOnClickListener {

            // A. Leemos todos los datos ingresados
            val nombre = etNombre.text.toString().trim()
            val apellido = etApellido.text.toString().trim()
            val dni = etDni.text.toString().trim()
            val fechaTexto = etFechaNacimiento.text.toString().trim()
            val correo = etCorreo.text.toString().trim()

            // B. Leemos las opciones seleccionadas
            val presentoApto = cbAptoFisico.isChecked
            val esSocio = rbSocio.isChecked

            // C. Validamos que ningún campo obligatorio esté vacío
            if (nombre.isEmpty() || apellido.isEmpty() || dni.isEmpty() || fechaTexto.isEmpty() || correo.isEmpty()) {

                Toast.makeText(
                    this,
                    "Por favor, completa todos los campos del formulario",
                    Toast.LENGTH_SHORT
                ).show()

            } else if (!presentoApto) {

                // Validación de la regla del club
                Toast.makeText(
                    this,
                    "Debe presentar el Apto Físico para registrarse",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                // D. Convertimos la fecha de String a LocalDate de forma segura
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val fechaNacimientoLocalDate = LocalDate.parse(fechaTexto, formatter)

                // E. Guardamos en el objeto compartido del Repositorio
                ClienteRepository.agregarCliente(
                    nombre = nombre,
                    apellido = apellido,
                    dni = dni,
                    fechaNacimiento = fechaNacimientoLocalDate,
                    telefono = "",
                    email = correo,
                    aptoFisico = presentoApto,
                    activo = true,
                    esSocio = esSocio,
                )

                // F. Mensaje de éxito y navegación
                val tipoUsuario = if (esSocio) "Socio" else "No Socio"
                Toast.makeText(
                    this,
                    "$tipoUsuario $nombre $apellido registrado correctamente",
                    Toast.LENGTH_SHORT
                ).show()

                // G. Viajamos a la pantalla de confirmación
                val intent = Intent(this, CobrarCuotaActivity::class.java)

                intent.putExtra("clienteDNI", dni)

                startActivity(intent)

                // H. Destruimos esta pantalla para no volver atrás
                finish()
            }
        }
    }
}