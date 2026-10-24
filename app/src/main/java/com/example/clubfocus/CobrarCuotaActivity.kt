package com.example.clubfocus

import android.annotation.SuppressLint
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
import com.example.clubfocus.data.repository.CuotaRepository
import com.example.clubfocus.helperDB.ClientesDBHelper
import com.example.clubfocus.helperDB.CuotasDBHelper
import java.time.LocalDate

class CobrarCuotaActivity : AppCompatActivity() {

    // Una única instancia para toda la Activity
    private var cuota: Cuota? = null
    private var cliente: Cliente? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cobrar_cuota)

        // Instancias de los Helpers de la base de datos
        val dbHelper = ClientesDBHelper(this)
        val cuotasHelper = CuotasDBHelper(this)

        // DNI recibido desde la Activity anterior
        val dniParam = intent.getStringExtra("clienteDNI")

        val dniCliente = findViewById<EditText>(R.id.etBuscador)
        val concepto = findViewById<TextView>(R.id.textConceptoPago)
        val monto = findViewById<TextView>(R.id.textMonto)
        val total = findViewById<TextView>(R.id.textMontoTotal)
        val descuento = findViewById<TextView>(R.id.textDescuento)
        val btnConfirmarPago = findViewById<Button>(R.id.btnRegistrarPago)

        val rgMedioPago = findViewById<RadioGroup>(R.id.rgMedioPago)
        val rbPagoEfectivo = findViewById<RadioButton>(R.id.rbPagoEfectivo)
        val rbPagoTarjeta = findViewById<RadioButton>(R.id.rbPagoTarjetaCred)
        val rbPagoDebito = findViewById<RadioButton>(R.id.rbPagoTarjetaDeb)

        val rgPromo = findViewById<RadioGroup>(R.id.rgPromo)
        val rbPromo3 = findViewById<RadioButton>(R.id.rbPromo3c)
        val rbPromo6 = findViewById<RadioButton>(R.id.rbPromo6c)

        val txtVolver = findViewById<TextView>(R.id.txtVolver)

        // VOLVER
        txtVolver.setOnClickListener {
            finish()
        }

        // SI LLEGA EL DNI DESDE LA ACTIVITY ANTERIOR
        if (dniParam != null) {
            dniCliente.setText(dniParam)
            cliente = dbHelper.buscarPorDni(dniParam)

            if (cliente != null) {
                val clienteActual = cliente!!

                // Creamos la cuota inicial con los valores de negocio
                cuota = CuotaRepository.crearCuotaInicial(
                    clienteId = clienteActual.id,
                    tipo = if (clienteActual.esSocio) {
                        TipoCuota.MENSUAL
                    } else {
                        TipoCuota.DIARIA
                    }
                )

                // Mostramos concepto
                concepto.text = if (clienteActual.esSocio) "Cuota Mensual" else "Cuota Diaria"

                // Cargar cuota en los TextViews
                val cuotaActual = cuota
                if (cuotaActual != null) {
                    monto.text = getString(R.string.monto_formato, cuotaActual.monto.toString())
                    total.text = getString(R.string.monto_formato, cuotaActual.montoFinal.toString())
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

        // FUNCIÓN PARA BUSCAR EL DNI MANUALMENTE
        fun buscarDni() {
            val dni = dniCliente.text.toString().trim()

            if (dni.length > 6) {
                cliente = dbHelper.buscarPorDni(dni)
                val clienteActual = cliente

                if (clienteActual != null) {
                    Toast.makeText(
                        this,
                        getString(R.string.cliente_encontrado_format, clienteActual.nombre, clienteActual.apellido),
                        Toast.LENGTH_LONG
                    ).show()

                    rbPagoEfectivo.isEnabled = true
                    rbPagoEfectivo.isChecked = true
                    rbPagoTarjeta.isEnabled = true
                    rbPagoDebito.isEnabled = true

                    cuota = CuotaRepository.crearCuotaInicial(
                        clienteId = clienteActual.id,
                        tipo = if (clienteActual.esSocio) {
                            TipoCuota.MENSUAL
                        } else {
                            TipoCuota.DIARIA
                        }
                    )

                    concepto.text = if (clienteActual.esSocio) "Cuota Mensual" else "Cuota Diaria"

                    val cuotaActual = cuota ?: return
                    monto.text = getString(R.string.monto_formato, cuotaActual.monto.toString())
                    total.text = getString(R.string.monto_formato, cuotaActual.montoFinal.toString())
                    descuento.text = getString(R.string.monto_formato, "0")

                } else {
                    Toast.makeText(
                        this,
                        "No se encontró ningún cliente con ese DNI",
                        Toast.LENGTH_SHORT
                    ).show()

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
                Toast.makeText(this, "DNI no válido", Toast.LENGTH_LONG).show()
            }
        }

        // BUSCAR CON LA LUPA DEL CAMPO DNI
        @SuppressLint("ClickableViewAccessibility")
        dniCliente.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = dniCliente.compoundDrawables[2]
                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()
                    if (event.x >= dniCliente.width - dniCliente.paddingEnd - drawableWidth - 40) {
                        v.performClick()
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
            val cuotaActual = cuota ?: return@setOnCheckedChangeListener

            when (checkedId) {
                R.id.rbPromo3c -> CuotaRepository.aplicarPromocion(cuotaActual, Promocion.TRES_CUOTAS)
                R.id.rbPromo6c -> CuotaRepository.aplicarPromocion(cuotaActual, Promocion.SEIS_CUOTAS)
                else -> CuotaRepository.aplicarPromocion(cuotaActual, Promocion.SIN_PROMOCION)
            }

            val importeDescuento = cuotaActual.monto - cuotaActual.montoFinal
            descuento.text = getString(R.string.monto_formato, importeDescuento.toString())
            total.text = getString(R.string.monto_formato, cuotaActual.montoFinal.toString())
        }

        // CONFIRMAR PAGO
        btnConfirmarPago.setOnClickListener {
            val clienteActual = cliente
            val cuotaActual = cuota

            if (clienteActual == null || cuotaActual == null) {
                Toast.makeText(this, "Primero ingresá un DNI válido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 1. VALIDACIÓN EN SQLITE: Evitar duplicar pago si la cuota sigue vigente
            if (cuotasHelper.tieneCuotaVigente(clienteActual.dni)) {
                Toast.makeText(
                    this,
                    "El cliente ya tiene la cuota vigente paga",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // MEDIO DE PAGO
            val formaPago = when (rgMedioPago.checkedRadioButtonId) {
                R.id.rbPagoEfectivo -> MedioPago.EFECTIVO
                R.id.rbPagoTarjetaCred -> MedioPago.TARJETA_CREDITO
                R.id.rbPagoTarjetaDeb -> MedioPago.TARJETA_DEBITO
                else -> null
            }

            if (formaPago == null) {
                Toast.makeText(this, "Seleccioná un medio de pago", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            cuotaActual.medioPago = formaPago

            // PROMOCIÓN
            val promocion = when (rgPromo.checkedRadioButtonId) {
                R.id.rbPromo3c -> Promocion.TRES_CUOTAS
                R.id.rbPromo6c -> Promocion.SEIS_CUOTAS
                else -> Promocion.SIN_PROMOCION
            }

            val detalle = when (rgPromo.checkedRadioButtonId) {
                R.id.rbPromo3c -> "3 cuotas. 15% de descuento"
                R.id.rbPromo6c -> "6 cuotas. 10% de descuento"
                else -> "Pago único"
            }

            cuotaActual.promocion = promocion
            cuotaActual.montoFinal = CuotaRepository.calcularMontoFinal(cuotaActual.monto, promocion)
            cuotaActual.estado = EstadoCuota.PAGADA

            // Mantenemos la lista en memoria para pantallas secundarias
            clienteActual.cuotas.add(cuotaActual)

            // 2. PERSISTENCIA EN SQLITE: Guardado del pago en la tabla cuotas
            val resultado = cuotasHelper.registrarPago(
                dniCliente = clienteActual.dni,
                tipo = cuotaActual.tipo.name,
                monto = cuotaActual.monto,
                montoFinal = cuotaActual.montoFinal,
                fechaPago = LocalDate.now().toString(),
                fechaVencimiento = cuotaActual.fechaVencimiento.toString(),
                medioPago = formaPago.name,
                estado = "PAGADA",
                promocion = promocion.name
            )

            // 3. Verificamos que SQLite haya insertado la fila con éxito
            if (resultado != -1L) {
                val intent = Intent(this, ConfirmarPagoActivity::class.java).apply {
                    putExtra("esSocio", clienteActual.esSocio)
                    putExtra("detallePago", detalle)
                    putExtra("clienteDNI", clienteActual.dni)
                }
                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "Error al registrar el pago en la base de datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}