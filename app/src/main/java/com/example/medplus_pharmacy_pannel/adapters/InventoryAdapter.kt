package com.example.medplus_pharmacy_pannel.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medplus_pharmacy_pannel.InventoryDisplayItem
import com.example.medplus_pharmacy_pannel.databinding.InventoryItemBinding
import com.example.medplus_pharmacy_pannel.interfaces.InventoryMedicineInterface

class InventoryAdapter(val list: ArrayList<InventoryDisplayItem> , val listener: InventoryMedicineInterface) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {
    class ViewHolder(val binding: InventoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = InventoryItemBinding.inflate(LayoutInflater.from(parent.context) , parent , false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvPrice.text = list[position].shopMedicinePrice
        holder.binding.tvEdit.setOnClickListener {
            listener.edit(list[position].medicine.id.toString() , list[position].medicine.medicineName.toString() , list[position].shopMedicinePrice)
        }
        holder.binding.tvMedicineName.text = list[position].medicine.medicineName
        Glide.with(holder.itemView.context).load(list[position].medicine.medicineImg).into(holder.binding.image)
    }

    fun updateData(newList: List<InventoryDisplayItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}
