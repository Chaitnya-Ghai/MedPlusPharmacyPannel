package com.example.medplus_pharmacy_pannel.interfaces

interface InventoryMedicineInterface {
    fun edit(medicineId: String, medicineName: String, medicinePrice: String)
    fun removeFromInventory(medicineId: String)
}
