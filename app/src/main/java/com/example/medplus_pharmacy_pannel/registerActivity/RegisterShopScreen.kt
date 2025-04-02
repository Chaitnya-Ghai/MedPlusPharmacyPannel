package com.example.medplus_pharmacy_pannel.registerActivity

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.R
import com.example.medplus_pharmacy_pannel.ShopData
import com.example.medplus_pharmacy_pannel.databinding.FragmentRegisterShopScreenBinding
import com.example.medplus_pharmacy_pannel.viewModels.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class RegisterShopScreen : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    val binding by lazy { FragmentRegisterShopScreenBinding.inflate(layoutInflater) }
    val viewModel: SharedViewModel by viewModels {
        Graph.SharedViewModelFactory(requireActivity().application, Graph.repo)
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

        val authId = Graph.auth.uid.toString()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validKey.collectLatest { key ->
                    if (key == 1) {
                        findNavController().navigate(R.id.waitingResponseFragment)
                    }
                }
            }
        }

        binding.saveBtn.setOnClickListener {
            binding.saveBtn.isEnabled = false  // Disable to prevent multiple clicks

            val shopName = binding.etShopName.text.toString().trim()
            val ownerName = binding.etOwnerName.text.toString().trim()
            val phoneNumber = binding.etPhoneNumber.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()

            // Validate fields before saving
            if (shopName.isEmpty() || ownerName.isEmpty() || phoneNumber.isEmpty() || address.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                binding.saveBtn.isEnabled = true  // Re-enable the button
                return@setOnClickListener
            }

            val shop = ShopData(
                authId = authId,
                shopName = shopName,
                ownerName = ownerName,
                phoneNumber = phoneNumber,
                address = address,
                inventory = emptyList(),
                shopImageUrl = "dummy data for now",
                licenseImageUrl = "https://example.com",  // Fixed URL format
                isVerified = 1
            )
            viewModel.registerShop(shop)
            findNavController().navigate(R.id.waitingResponseFragment)
        }
    }
}
