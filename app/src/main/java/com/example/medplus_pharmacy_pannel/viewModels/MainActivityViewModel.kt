package com.example.medplus_pharmacy_pannel.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medplus_pharmacy_pannel.CategoryModel
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.InventoryDisplayItem
import com.example.medplus_pharmacy_pannel.InventoryItem
import com.example.medplus_pharmacy_pannel.Medicine
import com.example.medplus_pharmacy_pannel.ShopData
import com.example.medplus_pharmacy_pannel.repository.ShopkeeperRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repo: ShopkeeperRepository) : ViewModel() {
    private val authId = Graph.auth.uid.toString()
    private val _shopData = MutableStateFlow<ShopData?>(null)
    private val shopData: StateFlow<ShopData?> = _shopData
    private val searchQuery = MutableStateFlow("")
    // Initializing shop data
    init {
        viewModelScope.launch {
            repo.getShopkeeperDetails(authId).collectLatest {
                _shopData.value = it
            }
        }
    }
    suspend fun deleteItemFromInventory(medicineId: String): Boolean {
        val currentShopData = shopData.value ?: return false
        val updatedInventory = currentShopData.inventory.filter { it.medicineId != medicineId }
        val updatedMedicineIds = currentShopData.medicineId.filter { it != medicineId }
        val updatedData = mapOf("inventory" to updatedInventory, "medicineId" to updatedMedicineIds)

        return if (updateShopDetails(updatedData)) {
            // Optional: re-fetch the latest data only once
            val updatedShopData = repo.getShopkeeperDetails(authId).first()
            _shopData.value = updatedShopData
            true
        } else {
            false
        }
    }


    // Exposing categories and medicines
    val getAllCategory: StateFlow<List<CategoryModel>> =
        repo.getAllCategory().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allMedicines: StateFlow<List<Medicine>> =
        repo.getMedicinesFlow().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val medicineIdsFlow: StateFlow<List<String>> =
        repo.observeMedicineIds(authId).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) {
        searchQuery.value = query
    }

    // Medicines NOT in inventory and matching query
//    get real-time + filtered list
    val availableMedicinesToAdd: StateFlow<List<Medicine>> = combine(allMedicines, medicineIdsFlow, searchQuery) { allMeds, inventoryIds, query ->
        val inventorySet = inventoryIds.toSet()
        allMeds.filter { it.id !in inventorySet && it.medicineName?.contains(query, true) == true }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Display inventory items with their shop prices
    val inventoryDisplayList: StateFlow<List<InventoryDisplayItem>> = combine(
        allMedicines,
        shopData.map { it?.inventory },
    ) { allMeds, inventory ->
        if (!inventory.isNullOrEmpty()){
            println("InventoryItems: $inventory")
            inventory.map { inventoryItem ->
                val medicine = allMeds.find { it.id == inventoryItem.medicineId } ?: Medicine()
                InventoryDisplayItem(medicine, inventoryItem.shopMedicinePrice)
            }
        }else{
            emptyList()
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    suspend fun addMedicinesToInventory(
        inventoryItems: List<InventoryItem>,
        medicineIds: List<String>
    ): Boolean {
        return repo.addMedicinesToInventory(authId, inventoryItems, medicineIds)
    }
    suspend fun updateShopDetails(updatedData: Map<String, Any>) :Boolean{ return repo.updateShopDetails(authId, updatedData) }


    suspend fun updateMedicinePrice(medicineId: String, newPrice: String): Boolean {
        val currentShopData = shopData.value ?: return false
        val updatedInventory = currentShopData.inventory.map {
            if (it.medicineId == medicineId) it.copy(shopMedicinePrice = newPrice) else it
        }
        val updatedData = mapOf("inventory" to updatedInventory)
        if (updateShopDetails(updatedData)) {
            // Optional but helpful for fast UI sync (even before listener triggers)
            _shopData.value = currentShopData.copy(inventory = updatedInventory)
            return true
        }
        return false
    }
    //  Add order-related functions (pending, history)
}




//categoryId?.let {
//            db.collection(availableMedicinesToAdd)
//                .whereArrayContains("belongingCategory", it)
//                .get()
//                .addOnSuccessListener { snapshot ->
//                    filterList.clear() // Prevent duplication
//                    for (document in snapshot) {
//                        filterList.add(document.toObject(MedicineModel::class.java))
//                    }
//                    binding.selectedRv.adapter?.notifyDataSetChanged() // Refresh RecyclerView
//                    binding.loader.visibility = View.GONE // Hide loader
//                    Toast.makeText(mainActivity, "Data fetched successfully", Toast.LENGTH_SHORT).show()
//                }
//                .addOnFailureListener {
//                    binding.loader.visibility = View.GONE // Hide loader on failure
//                    Toast.makeText(mainActivity, "Error fetching data", Toast.LENGTH_SHORT).show()
//                }
//        }