package com.example.medplus_pharmacy_pannel.registerActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.medplus_pharmacy_pannel.MainActivity
import com.example.medplus_pharmacy_pannel.R
import com.example.medplus_pharmacy_pannel.databinding.FragmentWaitingResponseBinding
import com.example.medplus_pharmacy_pannel.viewModels.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class WaitingResponseFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    val sharedViewModel: SharedViewModel by activityViewModels()
    val binding : FragmentWaitingResponseBinding by lazy { FragmentWaitingResponseBinding.inflate(layoutInflater) }
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
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.validKey.collectLatest { key ->
                    Log.d("Debug", "validKey in WaitingResponseFragment: $key") // Debugging
                    if (key == 2) {
//                      "Your shop registration is under review. You will be notified once verified."
                        val intent = Intent(requireContext(), MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    if (key == 3) {
                        binding.wait.visibility = View.GONE
                        binding.rejected.visibility = View.VISIBLE
                        binding.exit.visibility = View.VISIBLE
                        binding.exit.setOnClickListener {
                            requireActivity().finishAffinity()
                        }
                    }
                }
            }
        }
    }
}