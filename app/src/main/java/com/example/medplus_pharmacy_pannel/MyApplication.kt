package com.example.medplus_pharmacy_pannel

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)  // Enables offline caching
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }
}