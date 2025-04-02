package com.example.medplus_pharmacy_pannel.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.registerActivity.RegisterMyStoreActivity
import com.example.medplus_pharmacy_pannel.databinding.FragmentSigningBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SigningFragment : Fragment() {
    private var _binding: FragmentSigningBinding? = null
    private val binding get() = _binding!!
    val authActivity by lazy { activity as AuthenticationActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSigningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.Login.setOnClickListener {
            val emailEt = binding.emailEt.text.toString().trim()
            val passwordEt = binding.passwordEt.text.toString().trim()

            if (emailEt.isEmpty() || passwordEt.isEmpty()) {
                Toast.makeText(authActivity, "Enter Required Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailEt).matches()) {
                binding.emailEt.error = "Invalid Email"
                binding.emailEt.requestFocus()
                return@setOnClickListener
            }
            if (passwordEt.length < 6) {
                binding.passwordEt.error = "Password must be at least 6 characters"
                binding.passwordEt.requestFocus()
                return@setOnClickListener
            }

            // Firebase Authentication
            Graph.auth.signInWithEmailAndPassword(emailEt, passwordEt)
                .addOnSuccessListener {
                    Toast.makeText(authActivity, "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(authActivity, RegisterMyStoreActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace() // Prints full error in Logcat
                    Toast.makeText(authActivity, "Login Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
