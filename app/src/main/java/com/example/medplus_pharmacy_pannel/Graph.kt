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
    val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val repo: ShopkeeperRepository by lazy { ShopkeeperRepoImp(db) }
//  Provide ViewModel Factory for SharedViewModel
    class SharedViewModelFactory(private val application: Application, private val repository: ShopkeeperRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SharedViewModel(application, repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
//
    class MainActivityViewModelFactory(private val repo: ShopkeeperRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}