package com.example.medplus_pharmacy_pannel.repository

import android.util.Log
import com.example.medplus_pharmacy_pannel.CategoryModel
import com.example.medplus_pharmacy_pannel.Constants.Companion.category
import com.example.medplus_pharmacy_pannel.Constants.Companion.medicine
import com.example.medplus_pharmacy_pannel.Constants.Companion.pharmacist
import com.example.medplus_pharmacy_pannel.Graph
import com.example.medplus_pharmacy_pannel.InventoryItem
import com.example.medplus_pharmacy_pannel.Medicine
import com.example.medplus_pharmacy_pannel.ShopData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
    override fun getShopkeeperDetails(authId: String): Flow<ShopData?> = callbackFlow {
        val docRef = db.collection(pharmacist).document(authId)
        val registration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("Firestore", "Error: ${error.message}", error)
                trySend(null)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(ShopData::class.java))
            } else {
                trySend(null)
            }
        }
        awaitClose { registration.remove() }
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
    //get all categories
    override fun getAllCategory(): Flow<List<CategoryModel>> = callbackFlow {
        val listener = db.collection(category).addSnapshotListener { snapshots, e ->
            if (e != null) {
                close(e)
                return@addSnapshotListener
            }
            val categories = snapshots?.toObjects(CategoryModel::class.java) ?: emptyList()
            trySend(categories).isSuccess
        }
        awaitClose { listener.remove() }
    }
    //    get all availableMedicinesToAdd
    override fun getMedicinesFlow(): Flow<List<Medicine>> = callbackFlow {
        val listener = db.collection(medicine)
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

// adding items into inventory
    override suspend fun addMedicinesToInventory(
        authId: String,
        newInventoryItems: List<InventoryItem>,
        medicineIds: List<String>
    ): Boolean {
        return try {
            val docRef = db.collection(pharmacist).document(authId)
            // Get existing data
            val snapshot = docRef.get().await()
            val shopData = snapshot.toObject(ShopData::class.java)
            val currentInventory = shopData?.inventory ?: emptyList()// Get existing inventory
            val currentMedicineIds = shopData?.medicineId ?: emptyList()// Get existing medicineIds
            // Merge new items with current, avoiding duplicates by medicineId
            val updatedInventory = (currentInventory + newInventoryItems).distinctBy { it.medicineId }
            // Add only new IDs (not already present)
            val updatedMedicineIds = (currentMedicineIds + medicineIds).distinct()
            // update to Firestore
            docRef.update(
                mapOf(
                    "inventory" to updatedInventory,
                    "medicineId" to updatedMedicineIds
                )
            ).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteItemFromInventory(authId: String, medicineId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val docRef = db.collection(pharmacist).document(authId)
            val snapshot = docRef.get().await()
            val shopData = snapshot.toObject(ShopData::class.java)

            val updatedInventory = shopData?.inventory?.filterNot { it.medicineId == medicineId } ?: emptyList()
            val updatedMedicineIds = shopData?.medicineId?.filterNot { it == medicineId } ?: emptyList()

            val updatedData = mapOf(
                "inventory" to updatedInventory,
                "medicineId" to updatedMedicineIds
            )
            docRef.update(updatedData).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



    override fun observeMedicineIds(authId: String): Flow<List<String>> = callbackFlow{
        val docRef = db.collection(pharmacist).document(authId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val shopData = snapshot?.toObject(ShopData::class.java)
            val medicines = shopData?.medicineId ?: emptyList()
            trySend(medicines).isSuccess
        }
        awaitClose { listener.remove() }
    }




}




