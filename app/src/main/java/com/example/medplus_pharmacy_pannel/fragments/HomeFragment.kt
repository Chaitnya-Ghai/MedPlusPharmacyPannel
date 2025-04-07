package com.example.medplus_pharmacy_pannel.fragments

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

class HomeFragment : Fragment() , InventoryMedicineInterface {
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentHomeBinding.inflate(layoutInflater) }
    private val mainActivity by lazy { activity as MainActivity }
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

        binding.inventoryRv.layoutManager=LinearLayoutManager(mainActivity)
        val adapter = InventoryAdapter(arrayListOf(),this)
        binding.inventoryRv.adapter= adapter

        lifecycleScope.launch {
            viewModel.inventoryDisplayList.collectLatest {
                adapter.updateData(it)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity() // Exits the app completely
            }
        })
    }

    override fun edit(medicineId: String, medicineName: String, medicinePrice: String) {
        AlertDialog.Builder(mainActivity).apply {
            setTitle("Edit Medicine Price")
            val input = android.widget.EditText(mainActivity)
            input.setText(medicinePrice)
            setView(input)
            setPositiveButton("Save") { _, _ ->
                val newPrice = input.text.toString()
                lifecycleScope.launch {
                    viewModel.updateMedicinePrice(medicineId = medicineId ,newPrice)//change in firestore inventory 
                }
            }
            setNegativeButton("Cancel", null)
        }.show()
    }

    override fun removeFromInventory(
        medicineId: String,
        medicineName: String,
        medicinePrice: String
    ) {
        lifecycleScope.launch {}
    }
}