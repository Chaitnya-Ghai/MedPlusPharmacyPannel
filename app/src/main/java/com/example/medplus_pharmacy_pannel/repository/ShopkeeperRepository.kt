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
    // Fetch Shopkeeper Details by Auth ID
    fun getShopkeeperDetails(authId: String): Flow<ShopData?>
    // Update Shopkeeper Details
    suspend fun updateShopDetails(authId: String, updatedData: Map<String, Any>): Boolean
    // Fetch all categories from Firestore (Real-time Flow)
    fun getAllCategory(): Flow<List<CategoryModel>>
    // Fetch available availableMedicinesToAdd from Firestore (Real-time Flow)
    fun getMedicinesFlow(): Flow<List<Medicine>>
    fun observeMedicineIds(authId: String): Flow<List<String>>
    // Add selected availableMedicinesToAdd to the shopkeeper's inventory (Batch Write)
    suspend fun addMedicinesToInventory(authId: String, newInventoryItems: List<InventoryItem> , medicineIds : List<String>): Boolean
    suspend fun deleteItemFromInventory(authId: String, medicineId: String): Boolean
}
