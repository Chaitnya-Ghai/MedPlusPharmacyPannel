package com.example.medplus_pharmacy_pannel.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medplus_pharmacy_pannel.InventoryDisplayItem
import com.example.medplus_pharmacy_pannel.databinding.InventoryItemBinding
import com.example.medplus_pharmacy_pannel.interfaces.InventoryMedicineInterface

class InventoryAdapter(
    private val list: ArrayList<InventoryDisplayItem>,
    private val listener: InventoryMedicineInterface
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: InventoryItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = InventoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvMedicineName.text = item.medicine.medicineName
            tvPrice.text = item.shopMedicinePrice
            Glide.with(holder.itemView.context).load(item.medicine.medicineImg).into(image)
            tvEdit.setOnClickListener {
                listener.edit(item.medicine.id.orEmpty(), item.medicine.medicineName.orEmpty(), item.shopMedicinePrice)
            }
            tvRemove.setOnClickListener {
                listener.removeFromInventory(item.medicine.id.orEmpty())
            }
        }
    }
    fun updateData(newList: List<InventoryDisplayItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
    fun medicinePriceChanged(medicineId: String, newPrice: String) {
        val index = list.indexOfFirst { it.medicine.id == medicineId }
        if (index != -1) {
            list[index].shopMedicinePrice = newPrice
            notifyItemChanged(index)
        }
    }
    fun removeItemById(id: String) {
        val index = list.indexOfFirst { it.medicine.id == id }
        if (index != -1) {
            list.removeAt(index)
            notifyItemRemoved(index)
        }

    }

}
