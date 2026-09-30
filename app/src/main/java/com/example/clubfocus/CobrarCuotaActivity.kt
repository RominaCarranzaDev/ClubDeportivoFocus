package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
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

        val cuotaMensual = 45000
        val cuotaDiaria = 20000

        val dniCliente = findViewById<EditText>(R.id.etBuscador)
        val concepto = findViewById<TextView>(R.id.textConceptoPago)
        val monto = findViewById<TextView>(R.id.textMonto)
        val total = findViewById<TextView>(R.id.textMontoTotal)
        val descuento = findViewById<TextView>(R.id.textDescuento)

        val rgMedioPago =
            findViewById<RadioGroup>(R.id.rgMedioPago)

        val rbPagoEfectivo =
            findViewById<RadioButton>(R.id.rbPagoEfectivo)

        val rbPagoTarjeta =
            findViewById<RadioButton>(R.id.rbPagoTarjetaCred)

        val rbPagoDebito =
            findViewById<RadioButton>(R.id.rbPagoTarjetaDeb)

        val rgPromo =
            findViewById<RadioGroup>(R.id.rgPromo)

        val rbPromo3 =
            findViewById<RadioButton>(R.id.rbPromo3c)

        val rbPromo6 =
            findViewById<RadioButton>(R.id.rbPromo6c)

        val txtVolver =
            findViewById<TextView>(R.id.txtVolver)

        // ==========================================
        // VOLVER
        // ==========================================

        txtVolver.setOnClickListener {
            finish()
        }

        // ==========================================
        // FUNCIÓN PARA BUSCAR EL DNI
        // ==========================================

        fun buscarDni() {

            val dni = dniCliente.text.toString().trim()

            if (dni.length > 6) {

                val cliente =
                    ClienteRepository.buscarPorDni(dni)

                if (cliente != null) {

                    Toast.makeText(
                        this,
                        "Cliente encontrado: ${cliente.nombre} ${cliente.apellido}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Habilitamos medios de pago
                    rbPagoEfectivo.isEnabled = true
                    rbPagoEfectivo.isChecked = true

                    rbPagoTarjeta.isEnabled = true
                    rbPagoDebito.isEnabled = true

                    // Calculamos la cuota
                    if (cliente.esSocio) {

                        concepto.text = "Cuota Mensual"
                        monto.text = "$ $cuotaMensual"
                        total.text = "$ $cuotaMensual"

                    } else {

                        concepto.text = "Cuota Diaria"
                        monto.text = "$ $cuotaDiaria"
                        total.text = "$ $cuotaDiaria"
                    }

                } else {

                    Toast.makeText(
                        this,
                        "No se encontró ningún cliente con ese DNI",
                        Toast.LENGTH_SHORT
                    ).show()

                    rgMedioPago.clearCheck()

                    rbPagoEfectivo.isEnabled = false
                    rbPagoTarjeta.isEnabled = false
                    rbPagoDebito.isEnabled = false

                    rgPromo.clearCheck()

                    rbPromo3.isEnabled = false
                    rbPromo6.isEnabled = false
                }

            } else {

                Toast.makeText(
                    this,
                    "DNI no válido",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // ==========================================
        // BUSCAR CON LA LUPA DEL CAMPO DNI
        // ==========================================

        dniCliente.setOnTouchListener { _, event ->

            if (event.action == MotionEvent.ACTION_UP) {

                val drawableEnd =
                    dniCliente.compoundDrawables[2]

                if (drawableEnd != null) {

                    val drawableWidth =
                        drawableEnd.bounds.width()

                    if (
                        event.x >= dniCliente.width -
                        dniCliente.paddingEnd -
                        drawableWidth -
                        40
                    ) {

                        buscarDni()

                        return@setOnTouchListener true
                    }
                }
            }

            false
        }

        // ==========================================
        // BUSCAR CON LA LUPA DEL TECLADO
        // ==========================================

        dniCliente.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                buscarDni()

                true

            } else {

                false
            }
        }

        // ==========================================
        // TARJETA DE CRÉDITO
        // ==========================================

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

        // ==========================================
        // PROMOCIONES
        // ==========================================

        rgPromo.setOnCheckedChangeListener { _, checkedId ->

            val montoActual =
                monto.text.toString()
                    .replace("$", "")
                    .trim()
                    .toDoubleOrNull() ?: 0.0

            when (checkedId) {

                R.id.rbPromo3c -> {

                    val importeDescuento =
                        montoActual * 0.15

                    descuento.text =
                        "- $ $importeDescuento"

                    total.text =
                        "$ ${montoActual - importeDescuento}"
                }

                R.id.rbPromo6c -> {

                    val importeDescuento =
                        montoActual * 0.10

                    descuento.text =
                        "- $ $importeDescuento"

                    total.text =
                        "$ ${montoActual - importeDescuento}"
                }

                else -> {

                    descuento.text = "- $ 0"
                    total.text = "$ $montoActual"
                }
            }
        }

        // ==========================================
        // CONFIRMAR PAGO
        // ==========================================

        val btnConfirmarPago =
            findViewById<Button>(R.id.btnRegistrarPago)

        btnConfirmarPago.setOnClickListener {

            val dni =
                dniCliente.text.toString().trim()

            val cliente =
                ClienteRepository.buscarPorDni(dni)

            if (cliente != null) {

                // ------------------------------------------
                // MEDIO DE PAGO SELECCIONADO
                // ------------------------------------------

                val formaPago = when (
                    rgMedioPago.checkedRadioButtonId
                ) {

                    R.id.rbPagoEfectivo ->
                        "Efectivo"

                    R.id.rbPagoTarjetaCred ->
                        "Tarjeta Crédito"

                    R.id.rbPagoTarjetaDeb ->
                        "Tarjeta Débito"

                    else ->
                        "No especificado"
                }

                // ------------------------------------------
                // PROMOCIÓN SELECCIONADA
                // ------------------------------------------

                val detallePago = when (
                    rgPromo.checkedRadioButtonId
                ) {

                    R.id.rbPromo3c ->
                        "3 cuotas - 15% OFF"

                    R.id.rbPromo6c ->
                        "6 cuotas - 10% OFF"

                    else ->
                        "Sin promoción"
                }

                // ------------------------------------------
                // TOTAL CALCULADO
                // ------------------------------------------

                val totalPago =
                    total.text.toString()

                // ------------------------------------------
                // ABRIMOS CONFIRMAR PAGO
                // ------------------------------------------

                val intent = Intent(
                    this,
                    ConfirmarPagoActivity::class.java
                )

                intent.putExtra(
                    "esSocio",
                    cliente.esSocio
                )

                intent.putExtra(
                    "formaPago",
                    formaPago
                )

                intent.putExtra(
                    "detallePago",
                    detallePago
                )

                intent.putExtra(
                    "total",
                    totalPago
                )

                startActivity(intent)

            } else {

                Toast.makeText(
                    this,
                    "Primero ingresá un DNI válido",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}