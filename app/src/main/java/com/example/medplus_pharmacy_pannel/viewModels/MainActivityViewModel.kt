package com.example.medplus_pharmacy_pannel.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medplus_pharmacy_pannel.Medicine
import com.example.medplus_pharmacy_pannel.repository.ShopkeeperRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivityViewModel(val repo: ShopkeeperRepository):ViewModel() {
    val medicineList : StateFlow<List<Medicine>> = repo.getMedicinesFlow().stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5000L), emptyList())

    fun manageTask( action : suspend ()->Unit){
        viewModelScope.launch {
            action()
        }
    }
    fun addMedicinesToInventory(id: String, medicines: List<Medicine>) = manageTask { repo.addMedicinesToInventory(authId = id , medicines) }
    fun updateShopDetails(authId: String, updatedData: Map<String, Any>) = manageTask { repo.updateShopDetails(authId, updatedData) }
    fun getShopkeeperDetails(authId: String) = manageTask { repo.getShopkeeperDetails(authId) }
// implement other functions here; such as : orders which are pending , order history (completed , cancelled)
}