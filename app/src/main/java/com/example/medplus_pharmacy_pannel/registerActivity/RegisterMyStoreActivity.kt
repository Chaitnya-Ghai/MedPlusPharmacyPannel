package com.example.medplus_pharmacy_pannel.registerActivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.MainActivity
import com.example.medplus_pharmacy_pannel.R
import com.example.medplus_pharmacy_pannel.databinding.ActivityRegisterMyStoreBinding
import com.example.medplus_pharmacy_pannel.viewModels.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterMyStoreActivity : AppCompatActivity() {
    val sharedViewModel: SharedViewModel by viewModels {
        Graph.SharedViewModelFactory(application, Graph.repo)
    }
    val binding by lazy { ActivityRegisterMyStoreBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.pgBar.visibility= View.VISIBLE
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val navController = supportFragmentManager.findFragmentById(R.id.register_my_store_graph)?.findNavController()
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedViewModel.validKey.collectLatest { key ->
                    Log.d("Debug", "validKey updated: $key") // Debugging
                    when (key) {
                        0 ->{
                            binding.pgBar.visibility= View.GONE
                            navController?.navigate(R.id.registerShopScreen)
                        }
                        1 -> {
                            binding.pgBar.visibility= View.GONE
                            navController?.navigate(R.id.waitingResponseFragment)
                        }
                        2 -> {
                            binding.pgBar.visibility= View.GONE
                            val intent = Intent(this@RegisterMyStoreActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        3 -> {
                            binding.pgBar.visibility= View.GONE
                            navController?.navigate(R.id.waitingResponseFragment)
                        }
                    }
                }
            }
        }
    }
}
