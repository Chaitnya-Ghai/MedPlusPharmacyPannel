package com.example.medplus_pharmacy_pannel.viewModels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.ShopData
import com.example.medplus_pharmacy_pannel.repository.ShopkeeperRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SharedViewModel(application: Application, private val repo: ShopkeeperRepository) : AndroidViewModel(application){
    private val auth = Graph.auth // Ensure auth is initialized properly
    val currentUser = auth.currentUser
    val validKey : StateFlow<Int> = repo.validated(currentUser?.uid.toString()).stateIn(viewModelScope , SharingStarted.WhileSubscribed(5000L), 0)
    private fun manage(action: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                action()
            } catch (e: Exception) {
                Log.e("ViewModel", "Error: ${e.message}")
            }
        }
    }
    fun registerShop(shopData:ShopData) = manage { repo.registerShopkeeper(shopData) }
}
