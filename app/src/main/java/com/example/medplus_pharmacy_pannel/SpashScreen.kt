package com.example.medplus_pharmacy_pannel

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.medplus_pharmacy_pannel.databinding.ActivitySpashScreenBinding
import com.example.medplus_pharmacy_pannel.registerActivity.RegisterMyStoreActivity
import com.example.medplus_pharmacy_pannel.walkThrough.WalkthroughActivity
import com.google.firebase.auth.FirebaseAuth

class SpashScreen : AppCompatActivity() {
    private val auth: FirebaseAuth = Graph.auth
    val binding by lazy { ActivitySpashScreenBinding.inflate(layoutInflater) }
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            startActivity(Intent(this, WalkthroughActivity::class.java))
        } else {
            startActivity(Intent(this, RegisterMyStoreActivity::class.java))
        }
        finish()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        auth.addAuthStateListener(authStateListener)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        auth.removeAuthStateListener(authStateListener)
    }
}
