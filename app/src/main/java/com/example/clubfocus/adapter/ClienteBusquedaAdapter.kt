package com.example.clubfocus.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubfocus.R
import com.example.clubfocus.data.entity.CarnetListado
import com.example.clubfocus.data.entity.Cliente
import java.time.format.DateTimeFormatter

class ClienteBusquedaAdapter(
    private val onClick: (Cliente) -> Unit
) : RecyclerView.Adapter<ClienteBusquedaAdapter.ClienteViewHolder>() {

    private val lista = mutableListOf<Cliente>()

    class ClienteViewHolder(val tvCliente: TextView) :
        RecyclerView.ViewHolder(tvCliente)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ClienteViewHolder {

        val tv = TextView(parent.context).apply {
            textSize = 15f
            setTextColor(Color.BLACK)
            setPadding(16, 18, 16, 18)
            setBackgroundColor(Color.WHITE)
        }

        return ClienteViewHolder(tv)
    }

    override fun onBindViewHolder(
        holder: ClienteViewHolder,
        position: Int
    ) {

        val cliente = lista[position]

        holder.tvCliente.text =
            "${cliente.apellido}, ${cliente.nombre} - DNI: ${cliente.dni}"

        holder.tvCliente.setOnClickListener {
            onClick(cliente)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Cliente>) {

        lista.clear()
        lista.addAll(nuevaLista)

        notifyDataSetChanged()
    }
}
