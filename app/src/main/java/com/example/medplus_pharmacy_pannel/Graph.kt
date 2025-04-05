package com.example.medplus_pharmacy_pannel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medplus_pharmacy_pannel.repository.ShopkeeperRepoImp
import com.example.medplus_pharmacy_pannel.repository.ShopkeeperRepository
import com.example.medplus_pharmacy_pannel.viewModels.MainActivityViewModel
import com.example.medplus_pharmacy_pannel.viewModels.SharedViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object Graph {
    // Firebase Firestore instance
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    // Firebase Authentication instance
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    // Repository Implementation
    val repo: ShopkeeperRepository by lazy { ShopkeeperRepoImp(db) }
    // ViewModel Factory for SharedViewModel
    class SharedViewModelFactory(
        private val application: Application,
        private val repository: ShopkeeperRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    // ViewModel Factory for MainActivityViewModel
    class MainActivityViewModelFactory(
        private val repo: ShopkeeperRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
