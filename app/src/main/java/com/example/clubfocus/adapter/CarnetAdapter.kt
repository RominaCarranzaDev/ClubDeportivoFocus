package com.example.clubfocus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.clubfocus.R
import com.example.clubfocus.data.entity.CarnetListado
import java.time.format.DateTimeFormatter

class CarnetAdapter(
    private var carnets: List<CarnetListado>,
    private val onCarnetClick: (CarnetListado) -> Unit
) : RecyclerView.Adapter<CarnetAdapter.CarnetViewHolder>() {
    private var carnetSeleccionadoId: Int? = null
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    inner class CarnetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtNombreSocio: TextView = itemView.findViewById(R.id.txtNombreSocio)
        val txtNroSocio: TextView = itemView.findViewById(R.id.txtNroSocio)
        val txtFechaVencimiento: TextView = itemView.findViewById(R.id.txtFechaVencimiento)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarnetViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carnet, parent, false)

        return CarnetViewHolder(vista)
    }

    override fun onBindViewHolder(
        holder: CarnetViewHolder,
        position: Int
    ) {
        val carnet = carnets[position]

        holder.txtNombreSocio.text =
            "${carnet.nombre} ${carnet.apellido}"

        holder.txtNroSocio.text =
            "N.º de socio: ${carnet.nroSocio}"

        holder.txtFechaVencimiento.text =
            "Vence: ${carnet.fechaVencimiento.format(formatter)}"

        // Indicar visualmente cuál está seleccionado.
        if (carnetSeleccionadoId == carnet.id) {
            holder.itemView.setBackgroundResource(
                R.drawable.bg_card_carnet_seleccionado
            )
        } else {
            holder.itemView.setBackgroundResource(
                R.drawable.bg_card_carnet
            )
        }

        holder.itemView.setOnClickListener {
            val posicion = holder.bindingAdapterPosition

            if (posicion != RecyclerView.NO_POSITION) {
                val carnetClickeado = carnets[posicion]

                carnetSeleccionadoId = carnetClickeado.id

                notifyDataSetChanged()

                onCarnetClick(carnetClickeado)
            }
        }
    }


    override fun getItemCount(): Int = carnets.size

    fun actualizarLista(nuevaLista: List<CarnetListado>) {
        carnets = nuevaLista
        carnetSeleccionadoId = null
        notifyDataSetChanged()
    }
}
