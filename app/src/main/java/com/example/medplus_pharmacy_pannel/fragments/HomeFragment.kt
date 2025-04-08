package com.example.medplus_pharmacy_pannel.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

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
                    Log.d("InventorySize", "Size: ${it.size}")
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
        AlertDialog.Builder(mainActivity).apply {
            setTitle("Enter new price for $medicineName")
            val input = EditText(mainActivity).apply {
                setText(medicinePrice)
            }
            setView(input)
            setPositiveButton("Save") { _, _ ->
                binding.pg.visibility = View.VISIBLE
                val newPrice = input.text.toString()
                lifecycleScope.launch {
                    val success = viewModel.updateMedicinePrice(medicineId, newPrice)
                    if (success) {
                        binding.pg.visibility = View.GONE
                        adapter.medicinePriceChanged(medicineId, newPrice)
                        Toast.makeText(
                            mainActivity,
                            "price updated \n to ${newPrice}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        binding.pg.visibility = View.GONE
                        Toast.makeText(mainActivity, "not Updated", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            setNegativeButton("Cancel", null)
        }.show()
    }

    override fun removeFromInventory(medicineId: String) {
        AlertDialog.Builder(mainActivity).apply {
            setTitle("Are you sure you want to remove this medicine?")
            setPositiveButton("Yes") { _, _ ->
                lifecycleScope.launch {
                    val success = viewModel.deleteItemFromInventory(medicineId)
                    if (success) {
                        Toast.makeText(mainActivity, "Medicine removed from inventory", Toast.LENGTH_SHORT).show()
                        adapter.removeItemById(medicineId)
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
