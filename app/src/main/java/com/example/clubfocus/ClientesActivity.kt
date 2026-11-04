package com.example.clubfocus

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clubfocus.adapter.ClienteBusquedaAdapter
import com.example.clubfocus.data.entity.Cliente
import com.example.clubfocus.data.repository.CuotaRepository.prepararCuotaInicial
import com.example.clubfocus.helperDB.ClientesDBHelper
import com.example.clubfocus.helperDB.CuotasDBHelper
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ClientesActivity : AppCompatActivity() {
    private lateinit var dbHelper: ClientesDBHelper

    private lateinit var etBuscarDni: EditText
    private lateinit var rvCoincidencias: RecyclerView
    private lateinit var scrollDatosCliente: ScrollView

    // Datos cliente
    private lateinit var etNombre: EditText
    private lateinit var etApellido: EditText
    private lateinit var etDni: EditText
    private lateinit var etFechaNacimiento: EditText
    private lateinit var etEmail: EditText
    private lateinit var rbAptoFisico: RadioButton
    private lateinit var tvEstadoSocio: TextView
    private lateinit var tvNroSocio: TextView

    // Botones
    private lateinit var btnDarBaja: Button
    private lateinit var btnAsociar: Button
    private lateinit var btnEditarDatos: Button
    private lateinit var btnGuardarCambios: Button
    private lateinit var btnCancelarEdicion: Button

    private lateinit var adapter: ClienteBusquedaAdapter

    private var clienteSeleccionado: Cliente? = null
    private var modoEdicion = false
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clientes)

        inicializarVistas()

        dbHelper = ClientesDBHelper(this)

        configurarRecyclerView()
        configurarBuscador()
        configurarEventos()

        val btnVolver = findViewById<TextView>(R.id.txtVolver)

        btnVolver.setOnClickListener {
            finish()
        }
    }

    private fun inicializarVistas() {

        etBuscarDni = findViewById(R.id.etBuscarDni)
        rvCoincidencias = findViewById(R.id.rvCoincidencias)
        scrollDatosCliente = findViewById(R.id.scrollDatosCliente)

        etNombre = findViewById(R.id.etNombre)
        etApellido = findViewById(R.id.etApellido)
        etDni = findViewById(R.id.etDni)
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento)
        etEmail = findViewById(R.id.etEmail)

        rbAptoFisico = findViewById(R.id.rbAptoFisico)
        tvEstadoSocio = findViewById(R.id.tvEstadoSocio)
        tvNroSocio = findViewById(R.id.tvNroSocio)

        btnDarBaja = findViewById(R.id.btnDarBaja)
        btnAsociar = findViewById(R.id.btnAsociar)
        btnEditarDatos = findViewById(R.id.btnEditarDatos)
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios)
        btnCancelarEdicion = findViewById(R.id.btnCancelarEdicion)
    }

    private fun configurarRecyclerView() {

        adapter = ClienteBusquedaAdapter { cliente ->
            seleccionarCliente(cliente)
        }

        rvCoincidencias.layoutManager = LinearLayoutManager(this)

        rvCoincidencias.adapter = adapter
    }

    // BUSCADOR POR DNI
    private fun configurarBuscador() {
        etBuscarDni.doAfterTextChanged { texto ->

            val dni = texto.toString().trim()

            clienteSeleccionado = null
            ocultarFormulario()

            if (dni.isEmpty()) {

                rvCoincidencias.visibility = View.GONE
                adapter.actualizarLista(emptyList())

            } else {

                val coincidencias = dbHelper.buscarPorDniParcial(dni)

                adapter.actualizarLista(coincidencias)

                rvCoincidencias.visibility =
                    if (coincidencias.isNotEmpty()) View.VISIBLE
                    else View.GONE
            }
        }
    }

    // SELECCIONAR CLIENTE
    private fun seleccionarCliente(cliente: Cliente) {

        clienteSeleccionado = cliente

        rvCoincidencias.visibility = View.GONE

        cargarDatosCliente(cliente)

        scrollDatosCliente.visibility = View.VISIBLE

        cambiarModoEdicion(false)
    }

    // CARGAR DATOS DEL CLIENTE
    private fun cargarDatosCliente(cliente: Cliente) {

        etNombre.setText(cliente.nombre)
        etApellido.setText(cliente.apellido)
        etDni.setText(cliente.dni)
        etFechaNacimiento.setText(cliente.fechaNacimiento.format(formatter))
        etEmail.setText(cliente.email)
        rbAptoFisico.isChecked = cliente.aptoFisico
        tvNroSocio.text = cliente.nroSocio.ifEmpty { "" }

        actualizarEstadoSocio(cliente)
    }

    // ESTADO DE SOCIO
    private fun actualizarEstadoSocio(cliente: Cliente) {

        if (cliente.esSocio) {

            tvEstadoSocio.text = "Tipo de cliente: Socio"

            btnDarBaja.visibility = View.VISIBLE
            btnAsociar.visibility = View.GONE

        } else {

            tvEstadoSocio.text = "Tipo de cliente: No socio"

            btnDarBaja.visibility = View.GONE
            btnAsociar.visibility = View.VISIBLE
        }

        btnEditarDatos.visibility = View.VISIBLE
    }

    // OCULTAR FORMULARIO
    private fun ocultarFormulario() {
        scrollDatosCliente.visibility = View.GONE
        btnDarBaja.visibility = View.GONE
        btnAsociar.visibility = View.GONE
        btnEditarDatos.visibility = View.GONE
    }

    private fun cambiarModoEdicion(editar: Boolean) {

        modoEdicion = editar

        etNombre.isEnabled = editar
        etApellido.isEnabled = editar
        etDni.isEnabled = editar
        etFechaNacimiento.isEnabled = editar
        etEmail.isEnabled = editar
        rbAptoFisico.isEnabled = editar

        btnEditarDatos.visibility =
            if (editar) View.GONE else View.VISIBLE

        btnGuardarCambios.visibility =
            if (editar) View.VISIBLE else View.GONE

        btnCancelarEdicion.visibility =
            if (editar) View.VISIBLE else View.GONE

        btnDarBaja.visibility =
            if (!editar && clienteSeleccionado?.esSocio == true)
                View.VISIBLE else View.GONE

        btnAsociar.visibility =
            if (!editar && clienteSeleccionado?.esSocio == false)
                View.VISIBLE else View.GONE
    }

    // EVENTOS DE ESCUCHA CLICK
    private fun configurarEventos() {

        btnDarBaja.setOnClickListener {
            darDeBajaSocio()
        }

        btnAsociar.setOnClickListener {
            asociarCliente()
        }

        btnEditarDatos.setOnClickListener {

            cambiarModoEdicion(true)
        }

        btnCancelarEdicion.setOnClickListener {

            clienteSeleccionado?.let { cliente ->
                cargarDatosCliente(cliente)
            }

            cambiarModoEdicion(false)
        }

        btnGuardarCambios.setOnClickListener {
            guardarCambios()
        }

    }


    private fun darDeBajaSocio() {

        val cliente = clienteSeleccionado ?: return

        val resultado = dbHelper.darDeBajaSocio(cliente.id)

        if (resultado > 0) {

            val clienteActualizado = dbHelper.buscarPorId(cliente.id)

            if (clienteActualizado != null) {
                clienteSeleccionado = clienteActualizado
                cargarDatosCliente(clienteActualizado)
                cambiarModoEdicion(false)
            }

            Toast.makeText(
                this,
                "Socio dado de baja correctamente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun asociarCliente() {

        val cliente = clienteSeleccionado ?: return

        val resultado = dbHelper.asociarCliente(cliente.id)

        if (resultado > 0) {

            val clienteActualizado = dbHelper.buscarPorId(cliente.id)

            if (clienteActualizado != null) {

                clienteSeleccionado = clienteActualizado
                cargarDatosCliente(clienteActualizado)
                cambiarModoEdicion(false)

                val cuota = prepararCuotaInicial(
                    clienteActualizado.id,
                    clienteActualizado.esSocio
                )

                val cuotasDBHelper = CuotasDBHelper(this)

                cuotasDBHelper.generarCuotaInicial(
                    cuota.clienteId,
                    cuota.tipo,
                    cuota.monto,
                    cuota.montoFinal,
                    cuota.fechaVencimiento
                )
            }

            Toast.makeText(
                this,
                "Cliente asociado correctamente",
                Toast.LENGTH_SHORT
            ).show()

        } else {
            Toast.makeText(
                this,
                "No se pudo asociar al cliente",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun guardarCambios() {

        val cliente = clienteSeleccionado ?: return

        // Obtener los datos ingresados
        val nombre = etNombre.text.toString().trim()
        val apellido = etApellido.text.toString().trim()
        val dni = etDni.text.toString().trim()
        val fechaNacimiento = etFechaNacimiento.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val aptoFisico = rbAptoFisico.isChecked

        // Validar campos obligatorios
        if (nombre.isEmpty() || apellido.isEmpty() ||
            dni.isEmpty() || fechaNacimiento.isEmpty()
        ) {
            Toast.makeText(
                this,
                "Completá los campos obligatorios",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validar que el DNI no pertenezca a otro cliente
        if (dni != cliente.dni && dbHelper.existeCliente(dni)) {
            etDni.error = "Este DNI ya está registrado"
            return
        }

        // Validar y convertir la fecha
        val fechaNacimientoLocal = try {
            LocalDate.parse(fechaNacimiento, formatter)
        } catch (e: Exception) {
            etFechaNacimiento.error = "Ingresá una fecha válida (dd/MM/yyyy)"
            return
        }

        // Actualizar en la base de datos
        val resultado = dbHelper.actualizarCliente(
            id = cliente.id,
            nombre = nombre,
            apellido = apellido,
            dni = dni,
            fechaNacimiento = fechaNacimientoLocal.format(formatter),
            email = email,
            aptoFisico = aptoFisico
        )

        if (resultado > 0) {

            // Recuperar los datos actualizados
            val clienteActualizado = dbHelper.buscarPorId(cliente.id)

            if (clienteActualizado != null) {

                clienteSeleccionado = clienteActualizado

                cargarDatosCliente(clienteActualizado)

                cambiarModoEdicion(false)

                Toast.makeText(
                    this,
                    "Datos actualizados correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }

        } else {
            Toast.makeText(
                this,
                "No se pudieron actualizar los datos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}