package com.example.medplus_pharmacy_pannel

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // Enables offline caching
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
}