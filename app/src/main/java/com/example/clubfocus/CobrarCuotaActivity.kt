package com.example.clubfocus

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clubfocus.data.repository.ClienteRepository

class CobrarCuotaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobrar_cuota)

        val clienteRepository = ClienteRepository()

        val cuotaMensual = 45000
        val cuotaDiaria = 20000

        val dniCliente = findViewById<EditText>(R.id.etBuscador)

        val concepto = findViewById<TextView>(R.id.textConceptoPago)
        val monto = findViewById<TextView>(R.id.textMonto)
        val total = findViewById<TextView>(R.id.textMontoTotal)
        val descuento = findViewById<TextView>(R.id.textDescuento)

        val rgMedioPago = findViewById<RadioGroup>(R.id.rgMedioPago)

        val rbPagoEfectivo =
            findViewById<RadioButton>(R.id.rbPagoEfectivo)

        val rbPagoTarjeta =
            findViewById<RadioButton>(R.id.rbPagoTarjetaCred)

        val rbPagoDebito =
            findViewById<RadioButton>(R.id.rbPagoTarjetaDeb)

        val rgPromo = findViewById<RadioGroup>(R.id.rgPromo)

        val rbPromo3 =
            findViewById<RadioButton>(R.id.rbPromo3c)

        val rbPromo6 =
            findViewById<RadioButton>(R.id.rbPromo6c)

        val txtVolver =
            findViewById<TextView>(R.id.txtVolver)

        // Botón CONFIRMAR PAGO
        val btnRegistrarPago =
            findViewById<Button>(R.id.btnRegistrarPago)


        // VOLVER
        txtVolver.setOnClickListener {
            finish()
        }


        // BUSCAR CLIENTE POR DNI
        dniCliente.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                val dni = dniCliente.text.toString().trim()

                if (dni.length > 6) {

                    val cliente =
                        clienteRepository.buscarPorDni(dni)

                    if (cliente != null) {

                        Toast.makeText(
                            this,
                            "Cliente encontrado: ${cliente.nombre} ${cliente.apellido}",
                            Toast.LENGTH_LONG
                        ).show()

                        // Habilitar medios de pago
                        rbPagoEfectivo.isEnabled = true
                        rbPagoEfectivo.isChecked = true

                        rbPagoTarjeta.isEnabled = true
                        rbPagoDebito.isEnabled = true


                        // Cargar cuota según sea socio o no
                        if (cliente.esSocio) {

                            concepto.text = "Cuota Mensual"
                            monto.text = "$ $cuotaMensual"
                            total.text = "$ $cuotaMensual"

                        } else {

                            concepto.text = "Cuota Diaria"
                            monto.text = "$ $cuotaDiaria"
                            total.text = "$ $cuotaDiaria"
                        }

                        // Reiniciar descuento
                        descuento.text = "- $ 0"

                        // Deshabilitar promociones hasta elegir
                        // nuevamente tarjeta de crédito
                        rgPromo.clearCheck()
                        rbPromo3.isEnabled = false
                        rbPromo6.isEnabled = false

                    } else {

                        Toast.makeText(
                            this,
                            "No se encontró ningún cliente con ese DNI",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Deshabilitar medios de pago
                        rgMedioPago.clearCheck()

                        rbPagoEfectivo.isEnabled = false
                        rbPagoTarjeta.isEnabled = false
                        rbPagoDebito.isEnabled = false

                        // Deshabilitar promociones
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

                true

            } else {

                false
            }
        }


        // PROMOCIONES SOLO CON TARJETA DE CRÉDITO
        rbPagoTarjeta.setOnCheckedChangeListener { _, checked ->

            if (checked) {

                rbPromo3.isEnabled = true
                rbPromo6.isEnabled = true

            } else {

                rgPromo.clearCheck()

                rbPromo3.isEnabled = false
                rbPromo6.isEnabled = false
            }
        }


        // CALCULAR DESCUENTO
        rgPromo.setOnCheckedChangeListener { _, checkedId ->

            val montoActual = monto.text.toString()
                .replace("$", "")
                .trim()
                .toDoubleOrNull() ?: 0.0

            when (checkedId) {

                // 3 cuotas - 15% de descuento
                R.id.rbPromo3c -> {

                    val importeDescuento =
                        montoActual * 0.15

                    descuento.text =
                        "- $ $importeDescuento"

                    total.text =
                        "$ ${montoActual - importeDescuento}"
                }

                // 6 cuotas - 10% de descuento
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


        // CONFIRMAR PAGO
        btnRegistrarPago.setOnClickListener {

            val dni = dniCliente.text.toString().trim()

            // Verificar DNI
            if (dni.length <= 6) {

                Toast.makeText(
                    this,
                    "Ingresá un DNI válido",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // Buscar cliente
            val cliente =
                clienteRepository.buscarPorDni(dni)

            if (cliente == null) {

                Toast.makeText(
                    this,
                    "No se encontró ningún cliente",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // Verificar medio de pago
            if (rgMedioPago.checkedRadioButtonId == -1) {

                Toast.makeText(
                    this,
                    "Seleccioná un medio de pago",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            // Abrir pantalla de confirmación
            val intent =
                Intent(this, ConfirmarPagoActivity::class.java)

            // Enviar si es socio
            intent.putExtra(
                "esSocio",
                cliente.esSocio
            )

            // Enviar DNI
            intent.putExtra(
                "dni",
                dni
            )

            // Enviar concepto
            intent.putExtra(
                "concepto",
                concepto.text.toString()
            )

            // Enviar total
            intent.putExtra(
                "total",
                total.text.toString()
            )

            startActivity(intent)
        }
    }
}