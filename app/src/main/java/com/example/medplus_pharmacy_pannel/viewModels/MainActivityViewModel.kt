package com.example.medplus_pharmacy_pannel.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.medplus_pharmacy_pannel.CategoryModel
import com.example.medplus_pharmacy_pannel.InventoryItem
import com.example.medplus_pharmacy_pannel.Medicine
import com.example.medplus_pharmacy_pannel.repository.ShopkeeperRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainActivityViewModel(private val repo: ShopkeeperRepository) : ViewModel() {
    // Expose all medicines from Firebase
    val allMedicines: StateFlow<List<Medicine>> = repo.getMedicinesFlow()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val getAllCategory : StateFlow<List<CategoryModel>> = repo.getAllCategory().stateIn(viewModelScope , SharingStarted.WhileSubscribed(5000L) , emptyList())
    fun getAllMedicinesByCategory(categoryId: String): StateFlow<List<Medicine>> = repo.getMedicinesFlow().stateIn(viewModelScope , SharingStarted.WhileSubscribed(5000L) , emptyList())

    fun getMedicinesInInventory(authId: String): StateFlow<List<Medicine>> = repo.getMedicinesFlow().stateIn(viewModelScope , SharingStarted.WhileSubscribed(5000L) , emptyList())

    // Mutable search query
    private val searchQuery = MutableStateFlow("")
    fun updateSearchQuery(query: String) {
        searchQuery.value = query // Update search query from UI
    }

    // Search flow: fetches all medicines if query is blank, else searches by query
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val searchFlow: StateFlow<List<Medicine>> = searchQuery
        .debounce(300)  //Waits for 300ms before firing request (to avoid extra calls while typing).
        .distinctUntilChanged()// Avoid sending the same query multiple times if user types the same thing.
        .flatMapLatest { query -> //new query aye to purani search cancel.
            if (query.isBlank()) {
                repo.getMedicinesFlow() // Show all medicines on empty search
            } else {
                repo.searchMedByName(query)
                    .catch { emit(emptyList()) }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    val searchResultsLiveData = searchFlow.asLiveData()// Expose search results to XML-based UI

    // Reusable task runner
    private fun manageTask(action: suspend () -> Unit)= viewModelScope.launch { action() }
    fun addMedicinesToInventory(id: String, inventoryItems: List<InventoryItem> , medicineIds: List<String>) =
        manageTask { repo.addMedicinesToInventory(authId = id, inventoryItems , medicineIds ) }
    fun updateShopDetails(authId: String, updatedData: Map<String, Any>) =
        manageTask { repo.updateShopDetails(authId, updatedData) }
    fun getShopkeeperDetails(authId: String) =
        manageTask { repo.getShopkeeperDetails(authId) }
    //  Add order-related functions (pending, history)
}




//categoryId?.let {
//            db.collection(medicines)
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