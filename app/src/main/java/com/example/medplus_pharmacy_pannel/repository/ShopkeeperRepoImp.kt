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

    override fun searchMedByName(name: String): Flow<List<Medicine>> = callbackFlow {
        val listener = db.collection(medicine).orderBy("medicineName")
            .startAt(name)
            .endAt(name + "\uf8ff")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val medicines = snapshot?.toObjects(Medicine::class.java) ?: emptyList()
                trySend(medicines)
            }
        awaitClose { listener.remove() }
    }

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

            val currentInventory = shopData?.inventory ?: emptyList()
            val currentMedicineIds = shopData?.medicineId ?: emptyList()

            // Merge new items with current, avoiding duplicates by medicineId
            val updatedInventory = (currentInventory + newInventoryItems).distinctBy { it.medicineId }

            // Add only new IDs (not already present)
            val updatedMedicineIds = (currentMedicineIds + medicineIds).distinct()

            // Single update to Firestore
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
