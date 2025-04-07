package com.example.medplus_pharmacy_pannel.interfaces

interface MedicineInterface {
    fun tick(id: String , name: String , price : String)
    fun unTick(id: String , name: String, price: String)
}