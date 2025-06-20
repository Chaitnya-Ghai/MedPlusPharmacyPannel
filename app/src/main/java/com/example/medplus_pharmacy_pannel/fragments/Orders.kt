package com.example.medplus_pharmacy_pannel.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.MainActivity
import com.example.medplus_pharmacy_pannel.R
import com.example.medplus_pharmacy_pannel.databinding.FragmentOrdersBinding
import com.example.medplus_pharmacy_pannel.viewModels.MainActivityViewModel

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Orders : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private val binding by lazy { FragmentOrdersBinding.inflate(layoutInflater) }
    private val mainActivity by lazy { activity as MainActivity }
    private val viewModel : MainActivityViewModel by viewModels {
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
        // Add your fragment-specific logic here





        binding.ordersRv.layoutManager = LinearLayoutManager(requireContext())

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Orders.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Orders().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}