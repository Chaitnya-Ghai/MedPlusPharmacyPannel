package com.example.medplus_pharmacy_pannel

data class ShopData(
    val authId: String = "",
    val shopName: String = "",
    val ownerName: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val inventory: List<InventoryItem> = emptyList(),
    val medicineId: List<String> = emptyList(),//for faster query
    val licenseImageUrl: String = "",
    val shopImageUrl: String = "",
    var isVerified: Int ?=0,  // 0 for not Register , 1 for not verified, 2 for verified , 3 for rejected
){
    override fun equals(other: Any?): Boolean {
        return (other as? ShopData)?.authId == this.authId
    }
    override fun hashCode(): Int {
        return authId.hashCode()
    }
}
data class Order(
    val orderId: String = "",
    val customerId: String = "", // Firebase UID of user who placed order
    val customerName: String = "",
    val deliveryAddress: String = "",
    val orderTime: Long = System.currentTimeMillis(), // Store as timestamp
    val totalAmount: Double = 0.0,
    val status: String = "pending", // "pending", "shipped", "delivered", "cancelled"
    val items: List<OrderedMedicine> = emptyList()
)
data class OrderedMedicine(
    val medicineId: String = "",
    val medicineName: String = "",
    val quantity: Int = 0,
    val pricePerUnit: Double = 0.0
)


data class InventoryItem(
    var medicineId: String = "",
    var medicineName: String = "",
    var shopMedicinePrice: String = "",
){
    override fun equals(other: Any?): Boolean {
        return (other as? InventoryItem)?.medicineId == this.medicineId
    }
    override fun hashCode(): Int {
        return medicineId.hashCode()
    }
}

data class InventoryDisplayItem(
    var medicine: Medicine,
    var shopMedicinePrice: String
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

data class CategoryModel(
    var id: String? = null,
    var categoryName: String? = null,
    var imageUrl: String? = null,
)