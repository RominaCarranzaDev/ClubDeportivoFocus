package com.example.clubfocus
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvCerrarSesion = findViewById<TextView>(R.id.tvCerrarSesion)
        val btnRegistrarSocio = findViewById<Button>(R.id.btnRegistrarSocio)
        val btnCobrarCuota = findViewById<Button>(R.id.btnCobroCuotas)


        tvCerrarSesion.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("Esta seguro que desea cerrar la sesión?")
                .setPositiveButton("Aceptar") { _, _ ->
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnRegistrarSocio.setOnClickListener {
            val intent = Intent(this, RegistrarSocioActivity::class.java)

            startActivity(intent)
        }

        btnCobrarCuota.setOnClickListener {
            val intentCobro = Intent(this, CobrarCuotaActivity::class.java)

            startActivity(intentCobro)
        }
    }
}