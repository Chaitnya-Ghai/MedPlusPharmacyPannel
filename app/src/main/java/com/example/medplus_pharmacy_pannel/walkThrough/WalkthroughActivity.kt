package com.example.medplus_pharmacy_pannel.walkThrough

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.medplus_pharmacy_pannel.R
import com.example.medplus_pharmacy_pannel.auth.AuthenticationActivity
import com.example.medplus_pharmacy_pannel.databinding.ActivityWalkthroughBinding

class WalkthroughActivity : AppCompatActivity() {
    val binding by lazy { ActivityWalkthroughBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager)
        val adapter = WalkthroughAdapter(getScreens())
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.prevButton.visibility = if (position == 0) View.INVISIBLE else View.VISIBLE
                binding.nextButton.visibility = if (position == adapter.itemCount - 1) View.GONE else View.VISIBLE
                binding.getStartedButton.visibility = if (position == adapter.itemCount - 1) View.VISIBLE else View.GONE
            }
        })
        binding.prevButton.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem - 1
        }

        binding.nextButton.setOnClickListener {
            viewPager.currentItem = viewPager.currentItem + 1
        }

        binding.getStartedButton.setOnClickListener {
            val intent = Intent(this, AuthenticationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun getScreens(): List<WalkthroughModel> {
        return listOf(
            WalkthroughModel(R.drawable.img1, "Shopkeepers! \n Welcome to MedPlus ", "Confirm orders, track payments, and manage your pharmacy with ease."),
            WalkthroughModel(R.drawable.img2, "Fast & Secure Transactions!", "Easily process customer orders and handle payments securely"),
            WalkthroughModel(R.drawable.img3, "Monitor Sales & Performance", "Gain insights into your daily earnings and improve your business.")
        )
    }
}