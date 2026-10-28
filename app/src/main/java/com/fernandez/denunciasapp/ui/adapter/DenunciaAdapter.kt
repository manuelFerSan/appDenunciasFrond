package com.fernandez.denunciasapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fernandez.denunciasapp.data.model.Denuncia
import com.fernandez.denunciasapp.databinding.ItemDenunciaBinding

class DenunciaAdapter(
    private val onClick: (Denuncia) -> Unit
) : RecyclerView.Adapter<DenunciaAdapter.VH>() {

    private val items = mutableListOf<Denuncia>()

    fun submitList(newItems: List<Denuncia>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun appendList(newItems: List<Denuncia>) {
        val start = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(start, newItems.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemDenunciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class VH(private val binding: ItemDenunciaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(d: Denuncia) {
            binding.tvTipo.text = d.tipo
            binding.tvDni.text = "DNI: ${d.dni}"
            binding.tvDireccion.text = d.direccion
            binding.tvEstado.text = d.estadoTexto()
            binding.tvFecha.text = d.fecha ?: ""
            val statusColor = if (d.estado == 1) 0xFFF59E0B.toInt() else 0xFF16A34A.toInt()
            binding.tvEstado.backgroundTintList = android.content.res.ColorStateList.valueOf(statusColor)
            binding.root.setOnClickListener { onClick(d) }
        }
    }
}