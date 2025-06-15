package com.example.medplus_pharmacy_pannel.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.MainActivity
import com.example.medplus_pharmacy_pannel.adapters.InventoryAdapter
import com.example.medplus_pharmacy_pannel.databinding.FragmentHomeBinding
import com.example.medplus_pharmacy_pannel.interfaces.InventoryMedicineInterface
import com.example.medplus_pharmacy_pannel.viewModels.MainActivityViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), InventoryMedicineInterface {
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val mainActivity by lazy { activity as MainActivity }
    private val adapter = InventoryAdapter(arrayListOf(), this)
    private val viewModel: MainActivityViewModel by viewModels {
        Graph.MainActivityViewModelFactory(Graph.repo)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inventoryRv.layoutManager = LinearLayoutManager(mainActivity)
        binding.inventoryRv.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.inventoryDisplayList.collectLatest {
                    adapter.updateData(it)
                }
            }
        }
        // Handle back button to exit the app
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        })
    }
    override fun edit(medicineId: String, medicineName: String, medicinePrice: String) {
        val input = EditText(mainActivity).apply {
            setText(medicinePrice)
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        }
        AlertDialog.Builder(mainActivity).apply {
            setTitle("Enter new price for $medicineName")
            setView(input)
            setPositiveButton("Save") { _, _ ->
                val newPrice = input.text.toString()
                if (newPrice.isBlank()) {
                    Toast.makeText(mainActivity, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                binding.pg.visibility = View.VISIBLE
                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        viewModel.updateMedicinePrice(medicineId, newPrice)
                    }
                    binding.pg.visibility = View.GONE
                    if (success) {
                        adapter.medicinePriceChanged(medicineId, newPrice)
                        Toast.makeText(mainActivity, "Price updated to ₹$newPrice", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(mainActivity, "Failed to update price", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("Cancel", null)
            setCancelable(true)
        }.show()
    }
    override fun removeFromInventory(medicineId: String) {
        AlertDialog.Builder(mainActivity).apply {
            setTitle("Remove Medicine")
            setMessage("Are you sure you want to remove this medicine?")
            setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    val success = withContext(Dispatchers.IO) {
                        viewModel.deleteItemFromInventory(medicineId)
                    }
                    if (success) {
                        adapter.removeItemById(medicineId)
                        Toast.makeText(mainActivity, "Medicine removed from inventory", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(mainActivity, "Failed to remove medicine", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("No", null)
            setCancelable(true)
        }.show()
    }
}