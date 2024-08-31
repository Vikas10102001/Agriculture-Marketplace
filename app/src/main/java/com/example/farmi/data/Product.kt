package com.example.farmi.data

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.PropertyName
import java.util.UUID


//data class Product(
//    val productId: String,
//    val name:String,
//    val category:String,
//    val price:Double,
//    val available:Boolean,
//    val images: List<String>,
//    val location: GeoPoint,
//    val ownerId: String,
//    val date:String,
//    val quantity:Double
//)
data class Product(
    @get:PropertyName("productId") @set:PropertyName("productId") var productId: String? = null,
    @get:PropertyName("name") @set:PropertyName("name") var name: String? = null,
    @get:PropertyName("category") @set:PropertyName("category") var category: String? = null,
    @get:PropertyName("price") @set:PropertyName("price") var price: Double? = null,
    @get:PropertyName("available") @set:PropertyName("available") var available: Boolean? = null,
    @get:PropertyName("customerImages") @set:PropertyName("customerImages") var customerImages: List<String>? = null,
    @get:PropertyName("images") @set:PropertyName("images") var images: List<String>? = null,
    @get:PropertyName("location") @set:PropertyName("location") var location: GeoPoint? = null,
    @get:PropertyName("ownerId") @set:PropertyName("ownerId") var ownerId: String? = null,
    @get:PropertyName("date") @set:PropertyName("date") var date: String? = null,
    @get:PropertyName("quantity") @set:PropertyName("quantity") var quantity: Double? = null
) {
    constructor() : this(null, null, null, null, null, null, null, null, null, null,null)
}

