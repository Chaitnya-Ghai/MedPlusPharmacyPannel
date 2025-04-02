package com.example.medplus_pharmacy_pannel.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.medplus_pharmacy_pannel.Graph.auth
import com.example.medplus_pharmacy_pannel.R
import com.example.medplus_pharmacy_pannel.registerActivity.RegisterMyStoreActivity
import com.example.medplus_pharmacy_pannel.databinding.FragmentSignUpBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SignUpFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    val binding by lazy { FragmentSignUpBinding.inflate(layoutInflater) }
    val authActivity by lazy { activity as AuthenticationActivity }

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
        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_signingFragment)
        }
        binding.registerBtn.setOnClickListener {
            if (binding.emailEt.text.toString().isEmpty()) {
                binding.emailEt.error = "Please enter email"
                binding.emailEt.requestFocus()
                return@setOnClickListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.emailEt.text.toString()).matches()) {
                binding.emailEt.error = "Please enter valid email"
                binding.emailEt.requestFocus()
                return@setOnClickListener
            }
            if (binding.passwordEt.text.toString().isEmpty()) {
                binding.passwordEt.error = "Please enter password"
                binding.passwordEt.requestFocus()
                return@setOnClickListener
            }
            if (binding.passwordEt.text.toString().length < 6) {
                binding.passwordEt.error = "Password must be at least 6 characters"
                binding.passwordEt.requestFocus()
                return@setOnClickListener
            }
            if (binding.confirmPassword.text.toString().isEmpty()) {
                binding.confirmPassword.error = "Please enter confirm password"
                binding.confirmPassword.requestFocus()
                return@setOnClickListener
            }
            binding.registerBtn.isEnabled=false
            auth.createUserWithEmailAndPassword(binding.emailEt.text.toString(), binding.passwordEt.text.toString())
                .addOnSuccessListener {
                    Toast.makeText(authActivity, "Done", Toast.LENGTH_SHORT).show()
                    val intent = Intent(authActivity, RegisterMyStoreActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .addOnFailureListener {
                    binding.registerBtn.isEnabled=true
                    Toast.makeText(authActivity, "error", Toast.LENGTH_SHORT).show()
                }
        }
    }
}