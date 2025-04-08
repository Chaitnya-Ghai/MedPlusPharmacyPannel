package com.example.medplus_pharmacy_pannel.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.InventoryItem
import com.example.medplus_pharmacy_pannel.MainActivity
import com.example.medplus_pharmacy_pannel.interfaces.MedicineInterface
import com.example.medplus_pharmacy_pannel.R
import com.example.medplus_pharmacy_pannel.adapters.MedicineAdapter
import com.example.medplus_pharmacy_pannel.databinding.FragmentManageMedicinesBinding
import com.example.medplus_pharmacy_pannel.viewModels.MainActivityViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddMedicinesFragment : Fragment() , MedicineInterface {
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentManageMedicinesBinding.inflate(layoutInflater) }
    private val mainActivity by lazy { activity as MainActivity }
    private val addNewItems : ArrayList<InventoryItem> = arrayListOf()
    private val medicineId : ArrayList<String> = arrayListOf()
    private val viewModel : MainActivityViewModel by viewModels{
        Graph.MainActivityViewModelFactory(Graph.repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchBar = mainActivity.binding.searchBar
        binding.medicinesRv.layoutManager= GridLayoutManager(mainActivity,3)
        val medicineAdapter= MedicineAdapter(arrayListOf(),this)
        binding.medicinesRv.adapter=medicineAdapter
// showing all not selected availableMedicinesToAdd

// Collect filtered availableMedicinesToAdd (real-time + search + exclude inventory)
        lifecycleScope.launch {
            viewModel.availableMedicinesToAdd.collectLatest { filteredList ->
                medicineAdapter.updateList(filteredList)
            }
        }
        // Search functionality
        searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { viewModel.updateSearchQuery(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { viewModel.updateSearchQuery(it) }
                return true
            }
        })
    }
    fun onFabClick() {
        if (addNewItems.isNotEmpty()) {
            lifecycleScope.launch {
                val success = viewModel.addMedicinesToInventory(addNewItems, medicineId)
                if (success) {
                    Toast.makeText(mainActivity, "Items added successfully", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.homeFragment)
                } else {
                    Toast.makeText(mainActivity, "Failed to add items", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(mainActivity, "No availableMedicinesToAdd selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun tick(id: String, name: String, price: String) {
        val info = InventoryItem(id, name, price)
        if (!addNewItems.contains(info)) {
            addNewItems.add(info)
            medicineId.add(id)
            Log.e("DEBUG", "tick: ${addNewItems}")
            Toast.makeText(mainActivity, "added", Toast.LENGTH_SHORT).show()
        }
    }
    override fun unTick(id: String, name: String, price: String) {
            val info = InventoryItem(medicineId = id, medicineName = name , shopMedicinePrice = price)
            addNewItems.remove(info)
            medicineId.remove(id)
        Toast.makeText(mainActivity, "removed", Toast.LENGTH_SHORT).show()
        }
}

