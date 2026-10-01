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
import com.example.clubfocus.data.entity.Cliente
import com.example.clubfocus.data.entity.Cuota
import com.example.clubfocus.data.entity.EstadoCuota
import com.example.clubfocus.data.entity.MedioPago
import com.example.clubfocus.data.entity.Promocion
import com.example.clubfocus.data.entity.TipoCuota
import com.example.clubfocus.data.repository.ClienteRepository
import com.example.clubfocus.data.repository.CuotaRepository
import java.time.LocalDate

class CobrarCuotaActivity : AppCompatActivity() {

    // Una única instancia para toda la Activity
    private var cuota: Cuota? = null
    private var cliente: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobrar_cuota)

        // DNI recibido desde la Activity anterior
        val dniParam = intent.getStringExtra("clienteDNI")

        val dniCliente = findViewById<EditText>(R.id.etBuscador)
        val concepto = findViewById<TextView>(R.id.textConceptoPago)
        val monto = findViewById<TextView>(R.id.textMonto)
        val total = findViewById<TextView>(R.id.textMontoTotal)
        val descuento = findViewById<TextView>(R.id.textDescuento)
        val btnConfirmarPago = findViewById<Button>(R.id.btnRegistrarPago)

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

        // VOLVER
        txtVolver.setOnClickListener {
            finish()
        }

        // SI LLEGA EL DNI DESDE LA ACTIVITY ANTERIOR
        if (dniParam != null) {

            dniCliente.setText(dniParam)

            cliente = ClienteRepository.buscarPorDni(dniParam)

            if (cliente != null) {

                val clienteActual = cliente!!

                // Creamos la cuota inicial
                cuota = CuotaRepository.crearCuotaInicial(
                    clienteId = clienteActual.id,
                    tipo = if (clienteActual.esSocio) {
                        TipoCuota.MENSUAL
                    } else {
                        TipoCuota.DIARIA
                    }
                )

                // Mostramos concepto
                concepto.text =
                    if (clienteActual.esSocio) {
                        "Cuota Mensual"
                    } else {
                        "Cuota Diaria"
                    }

                //Cargar cuota
                val cuotaActual = cuota

                if (cuotaActual != null) {
                    monto.text = "$ ${cuotaActual.monto}"
                    total.text = "$ ${cuotaActual.montoFinal}"
                }

                // Habilitamos medios de pago
                rbPagoEfectivo.isEnabled = true
                rbPagoEfectivo.isChecked = true

                rbPagoTarjeta.isEnabled = true
                rbPagoDebito.isEnabled = true

            } else {

                Toast.makeText(
                    this,
                    "No se encontró ningún cliente con ese DNI",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // FUNCIÓN PARA BUSCAR EL DNI
        fun buscarDni() {

            val dni = dniCliente.text.toString().trim()

            if (dni.length > 6) {

                // Guardamos el cliente encontrado en la propiedad de la Activity.
                cliente = ClienteRepository.buscarPorDni(dni)

                val clienteActual = cliente

                if (clienteActual != null) {

                    Toast.makeText(
                        this,
                        "Cliente encontrado: ${clienteActual.nombre} ${clienteActual.apellido}",
                        Toast.LENGTH_LONG
                    ).show()

                    // Habilitamos medios de pago
                    rbPagoEfectivo.isEnabled = true
                    rbPagoEfectivo.isChecked = true

                    rbPagoTarjeta.isEnabled = true
                    rbPagoDebito.isEnabled = true

                    // Creamos la única instancia de cuota
                    cuota = CuotaRepository.crearCuotaInicial(
                        clienteId = clienteActual.id,
                        tipo = if (clienteActual.esSocio) {
                            TipoCuota.MENSUAL
                        } else {
                            TipoCuota.DIARIA
                        }
                    )

                    // Concepto de la cuota
                    concepto.text =
                        if (clienteActual.esSocio) {
                            "Cuota Mensual"
                        } else {
                            "Cuota Diaria"
                        }

                    // Obtenemos la cuota de forma segura
                    val cuotaActual = cuota ?: return

                    monto.text = "$ ${cuotaActual.monto}"
                    total.text = "$ ${cuotaActual.montoFinal}"

                    // Reiniciamos descuento
                    descuento.text = "$ 0"

                } else {

                    Toast.makeText(
                        this,
                        "No se encontró ningún cliente con ese DNI",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Eliminamos la cuota anterior
                    cuota = null

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

        // BUSCAR CON LA LUPA DEL CAMPO DNI
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

        // BUSCAR CON LA LUPA DEL TECLADO
        dniCliente.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                buscarDni()
                true

            } else {
                false
            }
        }

        // TARJETA DE CRÉDITO
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

        // PROMOCIONES
        rgPromo.setOnCheckedChangeListener { _, checkedId ->

            val cuotaActual =
                cuota ?: return@setOnCheckedChangeListener

            when (checkedId) {

                R.id.rbPromo3c -> {

                    CuotaRepository.aplicarPromocion(
                        cuotaActual,
                        Promocion.TRES_CUOTAS
                    )
                }

                R.id.rbPromo6c -> {

                    CuotaRepository.aplicarPromocion(
                        cuotaActual,
                        Promocion.SEIS_CUOTAS
                    )
                }

                else -> {

                    CuotaRepository.aplicarPromocion(
                        cuotaActual,
                        Promocion.SIN_PROMOCION
                    )
                }
            }

            // Calculamos el descuento
            val importeDescuento =
                cuotaActual.monto - cuotaActual.montoFinal

            descuento.text =
                "$ $importeDescuento"

            // Actualizamos el total
            total.text =
                "$ ${cuotaActual.montoFinal}"
        }

        // CONFIRMAR PAGO
        btnConfirmarPago.setOnClickListener {

            // Guardamos las propiedades en variables locales para evitar el error de Smart Cast.
            val clienteActual = cliente
            val cuotaActual = cuota

            if (clienteActual == null || cuotaActual == null) {

                Toast.makeText(
                    this,
                    "Primero ingresá un DNI válido",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
            // Validación para no duplicar pago diferenciando tipo de cliente
            val hoy = LocalDate.now()

            val tieneCuotaVigente = clienteActual.cuotas.any { cuota ->
                cuota.estado == EstadoCuota.PAGADA &&
                        cuota.fechaVencimiento >= hoy &&
                        (
                                (clienteActual.esSocio && cuota.tipo == TipoCuota.MENSUAL) ||
                                        (!clienteActual.esSocio && cuota.tipo == TipoCuota.DIARIA)
                                )
            }

            if (tieneCuotaVigente) {
                Toast.makeText(
                    this,
                    "El cliente ya tiene la cuota vigente paga",
                    Toast.LENGTH_LONG
                ).show()

                return@setOnClickListener
            }

            // MEDIO DE PAGO
            val formaPago = when (
                rgMedioPago.checkedRadioButtonId
            ) {

                R.id.rbPagoEfectivo ->
                    MedioPago.EFECTIVO

                R.id.rbPagoTarjetaCred ->
                    MedioPago.TARJETA_CREDITO

                R.id.rbPagoTarjetaDeb ->
                    MedioPago.TARJETA_DEBITO

                else -> null
            }

            if (formaPago == null) {

                Toast.makeText(
                    this,
                    "Seleccioná un medio de pago",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Guardamos el medio de pago en la cuota
            cuotaActual.medioPago = formaPago

            // PROMOCIÓN
            val promocion = when (
                rgPromo.checkedRadioButtonId
            ) {

                R.id.rbPromo3c ->
                    Promocion.TRES_CUOTAS

                R.id.rbPromo6c ->
                    Promocion.SEIS_CUOTAS

                else ->
                    Promocion.SIN_PROMOCION
            }

            //DETALLE DE PAGO
            val detalle = when (
                rgPromo.checkedRadioButtonId
            ) {

                R.id.rbPromo3c ->
                    "3 cuotas. 15% de descuento"

                R.id.rbPromo6c ->
                    "6 cuotas. 10% de descuento"

                else ->
                    "Pago único"
            }

            // Guardamos la promoción
            cuotaActual.promocion = promocion

            // CALCULAR TOTAL
            cuotaActual.montoFinal =
                CuotaRepository.calcularMontoFinal(
                    cuotaActual.monto,
                    promocion
                )

            // AGREGAR LA CUOTA AL CLIENTE
            clienteActual.cuotas.add(cuotaActual)

            cuotaActual.estado = EstadoCuota.PAGADA

            // ABRIR CONFIRMAR PAGO

            val intent = Intent(
                this,
                ConfirmarPagoActivity::class.java
            )

            intent.putExtra(
                "esSocio",
                clienteActual.esSocio
            )

            intent.putExtra(
                "detallePago",
                detalle
            )

            // Pasamos nuevamente el DNI
            intent.putExtra(
                "clienteDNI",
                clienteActual.dni
            )

            startActivity(intent)
        }
    }
}