package com.example.medplus_pharmacy_pannel.repository

import android.util.Log
import com.example.medplus_pharmacy_pannel.Constants.Companion.pharmacist
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.Medicine
import com.example.medplus_pharmacy_pannel.ShopData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ShopkeeperRepoImp(private val db: FirebaseFirestore = Graph.db) : ShopkeeperRepository {
    override suspend fun registerShopkeeper(shopkeeper: ShopData): Boolean {
        return try {
            shopkeeper.isVerified = 1
            db.collection(pharmacist).document(shopkeeper.authId).set(shopkeeper).await()
            Log.d("Debug", "Shop registered successfully with isVerified = 1")
            true
        } catch (e: Exception) {
            Log.e("Debug", "registerShopkeeper error: ${e.message}")
            false
        }
    }


    override fun validated(authId: String): Flow<Int> = callbackFlow {
        val listener = db.collection(pharmacist).document(authId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Debug", "isShopkeeperValidated Error: ${e.message}")
                    return@addSnapshotListener
                }
                val verified = snapshot?.getLong("verified")?.toInt() ?: -1
                Log.d("Debug", "Fetched verified: $verified")
                trySend(verified).isSuccess
            }
        awaitClose { listener.remove() }
    }



    override fun getMedicinesFlow(): Flow<List<Medicine>> = callbackFlow {
        val listener = FirebaseFirestore.getInstance()
            .collection("medicines")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    close(e)
                    return@addSnapshotListener
                }
                val medicines = snapshot?.toObjects(Medicine::class.java) ?: emptyList()
                trySend(medicines).isSuccess
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addMedicinesToInventory(
        authId: String,
        medicines: List<Medicine>
    ): Boolean {
        return try {
            val inventoryRef = db.collection(pharmacist).document(authId)
            // Fetch existing inventory
            val snapshot = inventoryRef.get().await()
            val currentInventory = snapshot.toObject(ShopData::class.java)?.inventory ?: emptyList()
            val updatedInventory = (currentInventory + medicines).distinct() // Merge new medicines with the existing ones
            // Update Firestore with new inventory
            inventoryRef.update("inventory", updatedInventory).await()
            true
        } catch (e: Exception) {
            false
        }
    }


    override suspend fun getShopkeeperDetails(authId: String): ShopData? {
        return try {
            val document = db.collection(pharmacist)
                .document(authId)
                .get()
                .await()
            if (document.exists()) {
                document.toObject(ShopData::class.java)
            } else {
                null
            }
        }catch (e:Exception){
            Log.e("Firestore", "Error fetching shopkeeper details: ${e.message}", e)
            null
        }
    }

    override suspend fun updateShopDetails(
        authId: String,
        updatedData: Map<String, Any>)
    : Boolean =
        suspendCoroutine { continuation ->
            db.collection(pharmacist).document(authId).update(updatedData)
                .addOnCompleteListener {
                    if (it.isSuccessful)
                        continuation.resume(true)
                    else {
                        continuation.resumeWithException(
                            it.exception ?: Exception("Unknown error updating shop")
                        )
                    }
                }
    }
}
