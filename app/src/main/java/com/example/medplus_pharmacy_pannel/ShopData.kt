package com.example.medplus_pharmacy_pannel

data class ShopData(
    val authId: String = "",
    val shopName: String = "",
    val ownerName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val inventory: List<InventoryItem> = emptyList(),
    val licenseImageUrl: String = "",
    val shopImageUrl: String = "",
    var isVerified: Int ?=0,  // 0 for not Register , 1 for not verified, 2 for verified , 3 for rejected
)

data class InventoryItem(
    var medicineId: String = "",
    var medicineName: String = "",
    var shopMedicinePrice: Double = 0.0,
)
data class Medicine(
    var id :String?=null,
    var medicineName :String?=null,
    var description :String?=null,
    var medicineImg :String?=null,
    var belongingCategory: MutableList<String>?=null,
    var dosageForm:String?=null,
    var unit :String?=null,
    var ingredients :String?=null,
    var howToUse :String?=null,
    var precautions :String?=null,
    var storageInfo :String?=null,
    var sideEffects :String?=null,
    var productDetail: ProductDetail?=null
)
data class ProductDetail(
    var expiryDate: String?=null,
    var brandName: String?=null,
    var originalPrice: String?=null,
)