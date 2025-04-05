package com.example.medplus_pharmacy_pannel.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.medplus_pharmacy_pannel.Medicine
import com.example.medplus_pharmacy_pannel.MedicineInterface
import com.example.medplus_pharmacy_pannel.databinding.MedicineItemBinding

class MedicineAdapter(
    var medicineList: ArrayList<Medicine>,
    private val interfaceMedicine: MedicineInterface
) : RecyclerView.Adapter<MedicineAdapter.ViewHolder>() {
    // Track selected medicine IDs
    private val selectedMedicineIds = mutableSetOf<String>()
    class ViewHolder(val binding: MedicineItemBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = MedicineItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = medicineList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val medicine = medicineList[position]

        holder.binding.price.text = medicine.productDetail?.originalPrice.toString()
        holder.binding.name.text = medicine.medicineName
        Glide.with(holder.itemView.context)
            .load(medicine.medicineImg)
            .into(holder.binding.img)

        // Set button state based on set
        holder.binding.radioButton.isChecked = selectedMedicineIds.contains(medicine.id)

        holder.binding.root.setOnClickListener {
            val isCurrentlySelected = selectedMedicineIds.contains(medicine.id)
            if (isCurrentlySelected) {
                selectedMedicineIds.remove(medicine.id)
                holder.binding.radioButton.isChecked = false
                interfaceMedicine.unTick(
                    medicine.id.toString(),
                    medicine.medicineName.toString(),
                    medicine.productDetail?.originalPrice.toString()
                )
            } else {
                medicine.id?.let { it1 -> selectedMedicineIds.add(it1) }
                holder.binding.radioButton.isChecked = true
                interfaceMedicine.tick(
                    medicine.id.toString(),
                    medicine.medicineName.toString(),
                    medicine.productDetail?.originalPrice.toString()
                )
            }
        }
    }

    fun updateList(newList: List<Medicine>) {
        medicineList.clear()
        medicineList.addAll(newList)
        selectedMedicineIds.clear()
        notifyDataSetChanged()
    }
}
