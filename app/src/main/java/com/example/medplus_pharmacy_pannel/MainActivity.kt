package com.example.medplus_pharmacy_pannel

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.medplus_pharmacy_pannel.databinding.ActivityMainBinding
import com.example.medplus_pharmacy_pannel.fragments.AddMedicinesFragment

class MainActivity : AppCompatActivity() {
    val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var findNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.host) as NavHostFragment
        findNavController = navHostFragment.navController
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Setup Bottom Navigation View
        binding.bottomNavView.apply {
            background = null
            menu.getItem(2).isEnabled = false // Disable center placeholder item

            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.home -> {
                        binding.fab.setImageResource(R.drawable.add)
                        binding.fab.setOnClickListener {
                            findNavController.navigate(R.id.homeFragment)
                        }
                    }

                    R.id.profile -> {
                        binding.fab.setImageResource(R.drawable.add)
                        binding.fab.setOnClickListener {
                            // Using global action
                            findNavController.navigate(R.id.action_global_profile)
                        }
                    }
                }
                true
            }
        }

        // FAB click: default action to addMedicinesFragment
        binding.fab.setOnClickListener {
            findNavController.navigate(R.id.action_global_addMedicinesFragment)
        }

        // Change FAB dynamically based on destination
        findNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.addMedicinesFragment -> {
                    binding.fab.setImageResource(R.drawable.done)
                    binding.fab.setOnClickListener {
                        val fragment = supportFragmentManager.findFragmentById(R.id.host)
                            ?.childFragmentManager?.fragments?.firstOrNull()
                        if (fragment is AddMedicinesFragment) {
                            fragment.onFabClick()
                        }
                    }
                }

                else -> {
                    binding.fab.setImageResource(R.drawable.add)
                    binding.fab.setOnClickListener {
                        findNavController.navigate(R.id.action_global_addMedicinesFragment)
                    }
                }
            }
        }
    }
}
