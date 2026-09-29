package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clubfocus.data.repository.ClienteRepository

class CobrarCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobrar_cuota)

        // Ya NO creamos 'val clienteRepository = ClienteRepository()'
        // porque al ser 'object' usamos directamente 'ClienteRepository'

        val cuotaMensual = 45000
        val cuotaDiaria = 20000

        val dniCliente = findViewById<EditText>(R.id.etBuscador)
        val concepto = findViewById<TextView>(R.id.textConceptoPago)
        val monto = findViewById<TextView>(R.id.textMonto)
        val total = findViewById<TextView>(R.id.textMontoTotal)
        val descuento = findViewById<TextView>(R.id.textDescuento)

        val rgMedioPago = findViewById<RadioGroup>(R.id.rgMedioPago)
        val rbPagoEfectivo = findViewById<RadioButton>(R.id.rbPagoEfectivo)
        val rbPagoTarjeta = findViewById<RadioButton>(R.id.rbPagoTarjetaCred)
        val rbPagoDebito = findViewById<RadioButton>(R.id.rbPagoTarjetaDeb)

        val rgPromo = findViewById<RadioGroup>(R.id.rgPromo)
        val rbPromo3 = findViewById<RadioButton>(R.id.rbPromo3c)
        val rbPromo6 = findViewById<RadioButton>(R.id.rbPromo6c)

        val txtVolver = findViewById<TextView>(R.id.txtVolver)
        txtVolver.setOnClickListener {
            finish()
        }

        val btnConfirmarPago = findViewById<Button>(R.id.btnRegistrarPago)
        btnConfirmarPago.setOnClickListener {
            val intent = Intent(this, ConfirmarPagoActivity::class.java)
            startActivity(intent)
        }

        dniCliente.setOnEditorActionListener { _, actionId, _ ->
            // Utiliza la lupa del teclado en lugar del enter tradicional
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val dni = dniCliente.text.toString().trim()

                if (dni.length > 6) {

                    // Buscamos directamente en el objeto compartido Singleton
                    val cliente = ClienteRepository.buscarPorDni(dni)

                    if (cliente != null) {

                        Toast.makeText(this, "Cliente encontrado: ${cliente.nombre} ${cliente.apellido}", Toast.LENGTH_LONG).show()

                        // Habilitamos opciones de pago
                        rbPagoEfectivo.isEnabled = true
                        rbPagoEfectivo.isChecked = true
                        rbPagoTarjeta.isEnabled = true
                        rbPagoDebito.isEnabled = true

                        // Si el cliente es socio se le cobra cuota mensual, si no, la diaria
                        if (cliente.esSocio) {
                            concepto.text = "Cuota Mensual"
                            monto.text = "$ ${cuotaMensual}"
                            total.text = "$ ${cuotaMensual}"
                        } else {
                            concepto.text = "Cuota Diaria"
                            monto.text = "$ ${cuotaDiaria}"
                            total.text = "$ ${cuotaDiaria}"
                        }

                    } else {
                        // Si no existe el DNI en la lista
                        Toast.makeText(this, "No se encontró ningún cliente con ese DNI", Toast.LENGTH_SHORT).show()

                        // Deshabilitamos medios de pago
                        rgMedioPago.clearCheck()
                        rbPagoEfectivo.isEnabled = false
                        rbPagoTarjeta.isEnabled = false
                        rbPagoDebito.isEnabled = false

                        rgPromo.clearCheck()
                        rbPromo3.isEnabled = false
                        rbPromo6.isEnabled = false
                    }
                } else {
                    Toast.makeText(this, "DNI no válido", Toast.LENGTH_LONG).show()
                }
                true
            } else {
                false
            }
        }

        rbPagoTarjeta.setOnCheckedChangeListener { _, _ ->
            if (rbPagoTarjeta.isChecked) {
                rbPromo3.isEnabled = true
                rbPromo6.isEnabled = true
            } else {
                rgPromo.clearCheck()
                rbPromo3.isEnabled = false
                rbPromo6.isEnabled = false
            }
        }

        rgPromo.setOnCheckedChangeListener { _, checkedId ->
            val montoActual = monto.text.toString()
                .replace("$", "")
                .trim()
                .toDoubleOrNull() ?: 0.0

            when (checkedId) {
                R.id.rbPromo3c -> {
                    val importeDescuento = montoActual * 0.15
                    descuento.text = "- $ ${importeDescuento}"
                    total.text = "$ ${(montoActual - importeDescuento)}"
                }
                R.id.rbPromo6c -> {
                    val importeDescuento = montoActual * 0.10
                    descuento.text = "- $ ${importeDescuento}"
                    total.text = "$ ${(montoActual - importeDescuento)}"
                }
                else -> {
                    descuento.text = "- $ 0"
                    total.text = montoActual.toString()
                }
            }
        }
    }
}