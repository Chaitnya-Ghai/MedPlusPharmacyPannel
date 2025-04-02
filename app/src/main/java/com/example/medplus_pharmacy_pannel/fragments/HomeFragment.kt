package com.example.medplus_pharmacy_pannel.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.MainActivity
import com.example.medplus_pharmacy_pannel.R
import com.example.medplus_pharmacy_pannel.databinding.FragmentHomeBinding
import com.example.medplus_pharmacy_pannel.viewModels.MainActivityViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity() // Exits the app completely
            }
        })
    }
}