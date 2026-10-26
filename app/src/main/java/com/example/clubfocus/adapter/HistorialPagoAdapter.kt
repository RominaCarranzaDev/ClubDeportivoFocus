package com.example.clubfocus.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.clubfocus.R
import com.example.clubfocus.data.entity.EstadoCuota
import com.example.clubfocus.data.entity.MedioPago
import com.example.clubfocus.data.entity.PagoHistorialItem
import com.example.clubfocus.data.entity.TipoCuota
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class HistorialPagoAdapter(
    private var cuotas: List<PagoHistorialItem>
) : RecyclerView.Adapter<HistorialPagoAdapter.PagoViewHolder>() {

    private val formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    class PagoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtSocioInfo: TextView = itemView.findViewById(R.id.txtSocioInfo)
        val txtCuota: TextView = itemView.findViewById(R.id.txtCuota)
        val txtVenc: TextView = itemView.findViewById(R.id.txtVenc)
        val txtTipoCuota: TextView = itemView.findViewById(R.id.txtTipoCuota)
        val txtMedioPago: TextView = itemView.findViewById(R.id.txtMedioPago)
        val txtMonto: TextView = itemView.findViewById(R.id.txtMonto)
        val txtEstado: TextView = itemView.findViewById(R.id.txtEstado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_pago, parent, false)
        return PagoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PagoViewHolder, position: Int) {
        val item = cuotas[position]
        val context = holder.itemView.context

        holder.txtSocioInfo.text = "${item.nombreCompleto}  •  DNI: ${item.dni}"

        val mes = item.fechaVencimiento.month
            .getDisplayName(TextStyle.FULL, Locale("es", "ES"))
            .replaceFirstChar { it.uppercase() }
        val anio = item.fechaVencimiento.year
        holder.txtCuota.text = if (item.tipo == TipoCuota.MENSUAL) "Cuota $mes $anio" else "Cuota Diaria"

        holder.txtVenc.text = item.fechaVencimiento.format(formatterFecha)
        holder.txtTipoCuota.text = if (item.tipo == TipoCuota.MENSUAL) "Socio" else "No Socio"

        holder.txtMedioPago.text = when (item.medioPago) {
            MedioPago.EFECTIVO -> "Efectivo"
            MedioPago.TARJETA_CREDITO -> "Tarjeta de crédito"
            MedioPago.TARJETA_DEBITO -> "Tarjeta de débito"
            null -> "-"
        }

        holder.txtMonto.text = "$ ${item.montoFinal.toInt()}"

        when (item.estado) {
            EstadoCuota.PAGADA -> {
                holder.txtEstado.text = "Pagada"
                holder.txtEstado.setBackgroundResource(R.drawable.label_success)
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.badge_pagada_text))
            }
            EstadoCuota.PENDIENTE -> {
                holder.txtEstado.text = "Pendiente"
                holder.txtEstado.setBackgroundResource(R.drawable.label_warning)
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.badge_pendiente_text))
            }
            EstadoCuota.VENCIDA -> {
                holder.txtEstado.text = "Vencida"
                holder.txtEstado.setBackgroundResource(R.drawable.label_danger)
                holder.txtEstado.setTextColor(ContextCompat.getColor(context, R.color.badge_vencida_text))
            }
        }
    }

    override fun getItemCount(): Int = cuotas.size

    fun actualizarLista(nuevaLista: List<PagoHistorialItem>) {
        cuotas = nuevaLista
        notifyDataSetChanged()
    }
}