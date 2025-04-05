package com.example.medplus_pharmacy_pannel.repository

import com.example.medplus_pharmacy_pannel.CategoryModel
import com.example.medplus_pharmacy_pannel.InventoryItem
import com.example.medplus_pharmacy_pannel.Medicine
import com.example.medplus_pharmacy_pannel.ShopData
import kotlinx.coroutines.flow.Flow


interface ShopkeeperRepository {
    // Register a new shopkeeper
    suspend fun registerShopkeeper(shopkeeper: ShopData): Boolean
    // Check if the shopkeeper is validated (Real-time Flow) , also check shopkeeper is already registered
    fun validated(authId: String): Flow<Int>
    // Fetch available medicines from Firestore (Real-time Flow)
    fun getMedicinesFlow(): Flow<List<Medicine>>
// fetch medicines by name from Firestore (Real-time Flow)
    fun searchMedByName(name: String): Flow<List<Medicine>>

    // Fetch all categories from Firestore (Real-time Flow)
    fun getAllCategory(): Flow<List<CategoryModel>>

    // Add selected medicines to the shopkeeper's inventory (Batch Write)
    suspend fun addMedicinesToInventory(authId: String, newInventoryItems: List<InventoryItem> , medicineIds : List<String>): Boolean
    // Fetch Shopkeeper Details by Auth ID
    suspend fun getShopkeeperDetails(authId: String): ShopData?
    // Update Shopkeeper Details
    suspend fun updateShopDetails(authId: String, updatedData: Map<String, Any>): Boolean
}
