package com.example.clubfocus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.clubfocus.R
import com.example.clubfocus.data.entity.SocioCuotaItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class SociosCuotaAdapter(
    private var cuotas: List<SocioCuotaItem>,
    private val onCuotaClick: (SocioCuotaItem) -> Unit
) : RecyclerView.Adapter<SociosCuotaAdapter.CuotaViewHolder>() {

    private var cuotaSeleccionadaId: Int? = null

    class CuotaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: LinearLayout = itemView.findViewById(R.id.cardSocio)
        val nombre: TextView = itemView.findViewById(R.id.txtNombreyNumSocio)
        val fecha: TextView = itemView.findViewById(R.id.txtFechaVenc)
        val estadoCuota: TextView = itemView.findViewById(R.id.txtEstadoCuota)
        val estadoSocio: TextView = itemView.findViewById(R.id.txtEstadoSocio)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuotaViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_socio_cuota, parent, false)
        return CuotaViewHolder(vista)
    }

    override fun onBindViewHolder(holder: CuotaViewHolder, position: Int) {
        val cuota = cuotas[position]
        val context = holder.itemView.context

        holder.nombre.text = if (!cuota.nroSocio.isNullOrBlank()) {
            "${cuota.nroSocio}  •  ${cuota.nombreCompleto}"
        } else {
            cuota.nombreCompleto
        }

        holder.fecha.text = if (cuota.fechaVencimiento.isNullOrBlank()) {
            "Sin fecha de vencimiento"
        } else {
            "Vence: ${cuota.fechaVencimiento}"
        }

        val fechaVenc = try {
            LocalDate.parse(cuota.fechaVencimiento, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        } catch (_: DateTimeParseException) {
            try {
                LocalDate.parse(cuota.fechaVencimiento)
            } catch (_: Exception) {
                null
            }
        }

        val hoy = LocalDate.now()

        // Regla: Únicamente dos estados posibles
        if (fechaVenc != null && hoy.isAfter(fechaVenc)) {
            // Pasó la fecha límite (a partir de las 00:00 del día siguiente)
            holder.estadoCuota.text = "Vencida"
            holder.estadoCuota.setBackgroundResource(R.drawable.label_danger)
            holder.estadoCuota.setTextColor(ContextCompat.getColor(context, R.color.badge_vencida_text))
            holder.estadoSocio.visibility = View.VISIBLE
        } else {
            // Es la fecha límite actual (hoy tiene acceso todavía)
            holder.estadoCuota.text = "Vence hoy"
            holder.estadoCuota.setBackgroundResource(R.drawable.label_warning)
            holder.estadoCuota.setTextColor(ContextCompat.getColor(context, R.color.badge_pendiente_text))
            holder.estadoSocio.visibility = View.GONE
        }

        val seleccionada = cuotaSeleccionadaId == cuota.cuotaId
        holder.card.setBackgroundResource(
            if (seleccionada) {
                R.drawable.bg_card_carnet_seleccionado
            } else {
                R.drawable.bg_borde_card
            }
        )

        holder.card.setOnClickListener {
            val posicionAnterior = cuotas.indexOfFirst {
                it.cuotaId == cuotaSeleccionadaId
            }

            cuotaSeleccionadaId = cuota.cuotaId

            if (posicionAnterior != -1) {
                notifyItemChanged(posicionAnterior)
            }

            val posicionActual = holder.bindingAdapterPosition
            if (posicionActual != RecyclerView.NO_POSITION) {
                notifyItemChanged(posicionActual)
            }

            onCuotaClick(cuota)
        }
    }

    override fun getItemCount(): Int = cuotas.size

    fun actualizarLista(nuevaLista: List<SocioCuotaItem>) {
        cuotas = nuevaLista
        notifyDataSetChanged()
    }
}