package com.example.medplus_pharmacy_pannel

interface MedicineInterface {
    fun tick(id: String , name: String , price : String)
    fun unTick(id: String , name: String, price: String)
}