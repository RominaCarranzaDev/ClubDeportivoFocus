package com.example.clubfocus

import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.clubfocus.data.repository.ClienteRepository
import android.view.inputmethod.EditorInfo

class CobrarCuotaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobrar_cuota)

        val clienteRepository = ClienteRepository()
        val cuotaMensual = 45000
        val cuotaDiaria = 20000

        val dniCliente = findViewById<EditText>(R.id.etBuscador)
        var concepto = findViewById<TextView>(R.id.textConceptoPago)
        var monto = findViewById<TextView>(R.id.textMonto)
        var total = findViewById<TextView>(R.id.textMontoTotal)
        var descuento = findViewById<TextView>(R.id.textDescuento)

        var rgMedioPago = findViewById<RadioGroup>(R.id.rgMedioPago)
        var rbPagoEfectivo = findViewById<RadioButton>(R.id.rbPagoEfectivo)
        var rbPagoTarjeta = findViewById<RadioButton>(R.id.rbPagoTarjetaCred)
        var rbPagoDebito = findViewById<RadioButton>(R.id.rbPagoTarjetaDeb)

        var rgPromo = findViewById<RadioGroup>(R.id.rgPromo)
        var rbPromo3 = findViewById<RadioButton>(R.id.rbPromo3c)
        var rbPromo6 = findViewById<RadioButton>(R.id.rbPromo6c)

        val txtVolver = findViewById<TextView>(R.id.txtVolver)
        txtVolver.setOnClickListener {
            finish()
        }

        dniCliente.setOnEditorActionListener { _, actionId, _ ->
      //  Utiliza el teclado con lupa en lugar de enter
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val dni = dniCliente.text.toString().trim()

                if (dni.length > 6) {

                    val cliente = clienteRepository.buscarPorDni(dni)

                    if (cliente != null) {

                        Toast.makeText(this, "Cliente encontrado: ${cliente.nombre} ${cliente.apellido}", Toast.LENGTH_LONG).show()
                      //  Si exite el cliente se habilita el medio de pago
                      //  medio de pago efectivo por default

                        rbPagoEfectivo.isEnabled = true
                        rbPagoEfectivo.isChecked = true
                        rbPagoTarjeta.isEnabled = true
                        rbPagoDebito.isEnabled = true
                    //  Si el cliente es socio se carga la cuota mensual, sino la cuota diaria en el detalle de pago
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
                        // No existe cliente registrado con ese dni
                        Toast.makeText(this, "No se encontró ningún cliente con ese DNI",Toast.LENGTH_SHORT).show()
                        // Se deshabilitan medio de pago y promociones
                        rgMedioPago.clearCheck()
                        rbPagoEfectivo.isEnabled = false
                        rbPagoTarjeta.isEnabled = false
                        rbPagoDebito.isEnabled = false

                        rgPromo.clearCheck()
                        rbPromo3.isEnabled = false
                        rbPromo6.isEnabled = false
                    }
                } else {
                    // Si el DNI ingresado no cumple con el formato de minimo caracteres
                    Toast.makeText(this,"DNI no válido",Toast.LENGTH_LONG).show()
                }
                // Booleano obligatorio por IME SEARCH
                true
            } else {
                // Booleano obligatorio por IME SEARCH
                false
            }
        }

        rbPagoTarjeta.setOnCheckedChangeListener { _, checked ->
            // Solo si el medio de pago es con tarjeta de credito se habilitan las promos
            if (rbPagoTarjeta.isChecked) {
                rbPromo3.isEnabled = true
                rbPromo6.isEnabled = true
            }
            else {
                rgPromo.clearCheck()
                rbPromo3.isEnabled = false
                rbPromo6.isEnabled = false
            }

        }

        rgPromo.setOnCheckedChangeListener { _, checkedId ->
            // Limpieza del campo monto
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