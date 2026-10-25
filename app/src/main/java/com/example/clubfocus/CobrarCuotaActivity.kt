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
import com.example.clubfocus.data.entity.MedioPago
import com.example.clubfocus.data.entity.Promocion
import com.example.clubfocus.data.entity.TipoCuota
import com.example.clubfocus.data.repository.CuotaRepository
import com.example.clubfocus.helperDB.ClientesDBHelper
import com.example.clubfocus.helperDB.CuotasDBHelper
import com.example.clubfocus.helperDB.CarnetDBHelper
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
        val carnetHelper = CarnetDBHelper(this)

        // ID recibido desde la Activity anterior
        val idParam = intent.getIntExtra("clienteId", -1)

        val dniCliente = findViewById<EditText>(R.id.etBuscador)
        val concepto = findViewById<TextView>(R.id.textConceptoPago)
        val monto = findViewById<TextView>(R.id.textMonto)
        val total = findViewById<TextView>(R.id.textMontoTotal)
        val descuento = findViewById<TextView>(R.id.textDescuento)

        val btnConfirmarPago = findViewById<Button>(R.id.btnRegistrarPago)
        val txtVolver = findViewById<TextView>(R.id.txtVolver)

        val rgMedioPago = findViewById<RadioGroup>(R.id.rgMedioPago)
        val rbPagoEfectivo = findViewById<RadioButton>(R.id.rbPagoEfectivo)
        val rbPagoTarjeta = findViewById<RadioButton>(R.id.rbPagoTarjetaCred)
        val rbPagoDebito = findViewById<RadioButton>(R.id.rbPagoTarjetaDeb)

        val rgPromo = findViewById<RadioGroup>(R.id.rgPromo)
        val rbPromo3 = findViewById<RadioButton>(R.id.rbPromo3c)
        val rbPromo6 = findViewById<RadioButton>(R.id.rbPromo6c)

        // VOLVER
        txtVolver.setOnClickListener {
            finish()
        }

        fun actualizarImportes() {
            val cuotaActual = cuota ?: return

            val medioSeleccionado = rgMedioPago.checkedRadioButtonId
            val esCredito = medioSeleccionado == R.id.rbPagoTarjetaCred

            // Las promociones solo corresponden a tarjeta de crédito.
            if (!esCredito && rgPromo.checkedRadioButtonId != -1) {
                rgPromo.clearCheck()
                return
            }

            val promocion = when {
                esCredito && rgPromo.checkedRadioButtonId == R.id.rbPromo3c ->
                    Promocion.TRES_CUOTAS

                esCredito && rgPromo.checkedRadioButtonId == R.id.rbPromo6c ->
                    Promocion.SEIS_CUOTAS

                else -> Promocion.SIN_PROMOCION
            }

            // El repositorio calcula el descuento y actualiza la cuota.
            CuotaRepository.aplicarPromocion(cuotaActual, promocion)

            val importeDescuento =
                (cuotaActual.monto - cuotaActual.montoFinal).coerceAtLeast(0.0)

            descuento.text = getString(
                R.string.monto_formato,
                String.format(java.util.Locale.getDefault(), "%.2f", importeDescuento)
            )

            total.text = getString(
                R.string.monto_formato,
                String.format(
                    java.util.Locale.getDefault(),
                    "%.2f",
                    cuotaActual.montoFinal
                )
            )
        }

        // CUOTA PENDIENTE DEl CLIENTE
        fun cargarCuotaDeCliente(clienteEncontrado: Cliente?) {

            if (clienteEncontrado == null) {
                Toast.makeText(
                    this,
                    "No se encontró el cliente",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            cliente = clienteEncontrado
            dniCliente.setText(clienteEncontrado.dni)

            val cuotaEncontrada = cuotasHelper.obtenerCuotaImpagaPorCliente(clienteEncontrado.id)

            if (cuotaEncontrada == null) {
                cliente = null

                Toast.makeText(
                    this,
                    "El cliente no tiene cuotas pendientes de pago",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            cuota = cuotaEncontrada

            concepto.text = when (cuotaEncontrada.tipo) {
                TipoCuota.MENSUAL -> "Cuota Mensual"
                else -> "Cuota Diaria"
            }

            monto.text = getString(
                R.string.monto_formato,
                String.format(
                    java.util.Locale.getDefault(),
                    "%.2f",
                    cuotaEncontrada.monto
                )
            )

            // Al cargar una cuota, se empieza sin promoción.
            CuotaRepository.aplicarPromocion(
                cuotaEncontrada,
                Promocion.SIN_PROMOCION
            )

            rgPromo.clearCheck()
            rgMedioPago.check(R.id.rbPagoEfectivo)

            rbPagoEfectivo.isEnabled = true
            rbPagoTarjeta.isEnabled = true
            rbPagoDebito.isEnabled = true

            rbPromo3.isEnabled = false
            rbPromo6.isEnabled = false

            btnConfirmarPago.isEnabled = true

            actualizarImportes()
        }

        if (idParam != -1) {
            val clienteEncontrado = dbHelper.buscarPorId(idParam)

            if (clienteEncontrado != null) {
                cargarCuotaDeCliente(clienteEncontrado)
            } else {
                Toast.makeText(
                    this,
                    "No se encontró el cliente solicitado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // BUSCAR CLIENTE POR DNI
        fun buscarDni() {
            val dni = dniCliente.text.toString().trim()

            if (dni.isBlank()) {
                Toast.makeText(
                    this,
                    "Ingresá un DNI",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }

            val clienteEncontrado = dbHelper.buscarPorDni(dni)

            if (clienteEncontrado == null) {

                Toast.makeText(
                    this,
                    "No se encontró ningún cliente con ese DNI",
                    Toast.LENGTH_LONG
                ).show()
                return
            }

            cargarCuotaDeCliente(clienteEncontrado)
        }

        // Lupa del campo DNI.
        @SuppressLint("ClickableViewAccessibility")
        dniCliente.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = dniCliente.compoundDrawables[2]

                if (drawableEnd != null) {
                    val drawableWidth = drawableEnd.bounds.width()

                    if (
                        event.x >= dniCliente.width -
                        dniCliente.paddingEnd -
                        drawableWidth - 40
                    ) {
                        v.performClick()
                        buscarDni()
                        return@setOnTouchListener true
                    }
                }
            }
            false
        }

        // Búsqueda desde el teclado.
        dniCliente.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarDni()
                true
            } else {
                false
            }
        }

        // CAMBIO DE MEDIO DE PAGO
        rgMedioPago.setOnCheckedChangeListener { _, checkedId ->
            if (cuota == null) return@setOnCheckedChangeListener

            val esCredito = checkedId == R.id.rbPagoTarjetaCred

            rbPromo3.isEnabled = esCredito
            rbPromo6.isEnabled = esCredito

            if (!esCredito) {
                rgPromo.clearCheck()
            }

            actualizarImportes()
        }

        // CAMBIO DE PROMOCIÓN
        rgPromo.setOnCheckedChangeListener { _, _ ->
            if (cuota == null) return@setOnCheckedChangeListener
            actualizarImportes()
        }

        // REGISTRAR PAGO
        btnConfirmarPago.setOnClickListener {
            val clienteActual = cliente
            val cuotaActual = cuota

            if (clienteActual == null || cuotaActual == null) {
                Toast.makeText(
                    this,
                    "Buscá un cliente con una cuota pendiente",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val formaPago = when (rgMedioPago.checkedRadioButtonId) {
                R.id.rbPagoEfectivo -> MedioPago.EFECTIVO
                R.id.rbPagoTarjetaCred -> MedioPago.TARJETA_CREDITO
                R.id.rbPagoTarjetaDeb -> MedioPago.TARJETA_DEBITO
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

            // La promoción solo se acepta con tarjeta de crédito.
            val promocion = when {
                formaPago == MedioPago.TARJETA_CREDITO &&
                        rgPromo.checkedRadioButtonId == R.id.rbPromo3c ->
                    Promocion.TRES_CUOTAS

                formaPago == MedioPago.TARJETA_CREDITO &&
                        rgPromo.checkedRadioButtonId == R.id.rbPromo6c ->
                    Promocion.SEIS_CUOTAS

                else -> Promocion.SIN_PROMOCION
            }

            cuotaActual.medioPago = formaPago
            CuotaRepository.aplicarPromocion(cuotaActual, promocion)

            val detalle = when (promocion) {
                Promocion.TRES_CUOTAS -> "3 cuotas. 15% de descuento"
                Promocion.SEIS_CUOTAS -> "6 cuotas. 10% de descuento"
                else -> if (formaPago == MedioPago.EFECTIVO) {
                    "Pago en efectivo"
                } else {
                    "Pago con tarjeta sin promoción"
                }
            }

            // Registrar el pago en SQLite.
            val resultado = cuotasHelper.registrarPago(
                cuotaActual.id,
                fechaPago = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                montoFinal = cuotaActual.montoFinal,
                medioPago = formaPago,
                promocion = promocion
            )

            if (resultado == 1) {
                btnConfirmarPago.isEnabled = false

                if(clienteActual.esSocio){
                    carnetHelper.crearCarnet(clienteActual.nroSocio)
                }

                val intent = Intent(this, ConfirmarPagoActivity::class.java
                ).apply {
                    putExtra("esSocio", clienteActual.esSocio)
                    putExtra("detallePago", detalle)
                    putExtra("clienteDNI", clienteActual.dni)
                }

                startActivity(intent)
            } else {
                Toast.makeText(
                    this,
                    "No se pudo registrar el pago",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}