package com.example.clubfocus

import android.os.Bundle
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.example.clubfocus.helperDB.UsuariosDBHelper

class AdministrarUsuariosActivity : AppCompatActivity() {

    private lateinit var usuariosHelper: UsuariosDBHelper
    private lateinit var contenedorAdministradores: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_administrar_usuarios)

        usuariosHelper = UsuariosDBHelper(this)
        contenedorAdministradores = findViewById(R.id.contenedorAdministradores)

        val usuarioActual = getSharedPreferences("ClubFocusPrefs", MODE_PRIVATE)
            .getString("usuarioLogueado", "")
            .orEmpty()
        val rolActual = usuariosHelper.obtenerRolPorUsuario(usuarioActual)

        if (!rolActual.equals("SuperAdmin", ignoreCase = true)) {
            Toast.makeText(this, R.string.admin_panel_solo_superadmin, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        findViewById<TextView>(R.id.tvVolverAdministradores).setOnClickListener {
            finish()
        }

        val etUsuario = findViewById<EditText>(R.id.etAdminUsuario)
        val etContrasena = findViewById<EditText>(R.id.etAdminContrasena)
        findViewById<Button>(R.id.btnCrearAdministrador).setOnClickListener {
            val usuario = etUsuario.text.toString().trim()
            val contrasena = etContrasena.text.toString()

            if (usuario.isBlank() || contrasena.isBlank()) {
                Toast.makeText(this, R.string.admin_panel_campos_obligatorios, Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = usuariosHelper.crearAdministrador(usuario, contrasena)
            if (id != -1L) {
                etUsuario.text.clear()
                etContrasena.text.clear()
                Toast.makeText(this, R.string.admin_panel_creado, Toast.LENGTH_SHORT).show()
                cargarAdministradores()
            } else {
                Toast.makeText(this, R.string.admin_panel_error_crear, Toast.LENGTH_SHORT).show()
            }
        }

        cargarAdministradores()
    }

    private fun cargarAdministradores() {
        contenedorAdministradores.removeAllViews()
        val administradores = usuariosHelper.obtenerAdministradores()

        if (administradores.isEmpty()) {
            contenedorAdministradores.addView(TextView(this).apply {
                text = getString(R.string.admin_panel_vacio)
                setPadding(0, 12, 0, 12)
                setTextColor(getColor(R.color.secondary))
                textSize = 14f
            })
            return
        }

        administradores.forEach { administrador ->
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(8), dp(12), dp(8))
                setBackgroundResource(R.drawable.bg_borde_card)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply {
                    bottomMargin = dp(12)
                }
            }

            val nombre = TextView(this).apply {
                text = administrador.usuario
                textSize = 16f
                setTextColor(getColor(R.color.primary))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
                layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
            }

            val botonEliminar = MaterialButton(this).apply {
                text = getString(R.string.admin_panel_eliminar)
                setTextColor(getColor(R.color.white))
                backgroundTintList = ColorStateList.valueOf(getColor(R.color.danger))
                cornerRadius = dp(12)
                minimumHeight = dp(48)
                insetTop = 0
                insetBottom = 0
                textSize = 12f
                setOnClickListener {
                    confirmarEliminacion(administrador.id, administrador.usuario)
                }
            }

            fila.addView(nombre)
            fila.addView(botonEliminar)
            contenedorAdministradores.addView(fila)
        }
    }

    private fun confirmarEliminacion(id: Int, usuario: String) {
        AlertDialog.Builder(this)
            .setTitle(R.string.admin_panel_confirmar_titulo)
            .setMessage(getString(R.string.admin_panel_confirmar_mensaje, usuario))
            .setNegativeButton("Cancelar", null)
            .setPositiveButton(R.string.admin_panel_eliminar) { _, _ ->
                if (usuariosHelper.eliminarAdministrador(id) > 0) {
                    Toast.makeText(this, R.string.admin_panel_eliminado, Toast.LENGTH_SHORT).show()
                    cargarAdministradores()
                }
            }
            .show()
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
