package com.example.medplus_pharmacy_pannel.repository

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
    // Add selected medicines to the shopkeeper's inventory (Batch Write)
    suspend fun addMedicinesToInventory(authId: String, medicines: List<Medicine>): Boolean
    // Fetch Shopkeeper Details by Auth ID
    suspend fun getShopkeeperDetails(authId: String): ShopData?
    // Update Shopkeeper Details
    suspend fun updateShopDetails(authId: String, updatedData: Map<String, Any>): Boolean
}
