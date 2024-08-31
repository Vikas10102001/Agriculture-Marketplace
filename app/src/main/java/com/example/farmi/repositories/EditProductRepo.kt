package com.example.farmi.repositories

import android.util.Log
import com.example.farmi.data.Product
import com.example.farmi.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EditProductRepo @Inject constructor(
    private val firestore: FirebaseFirestore
) {
//    suspend  fun getProductForEditing(productId: String): Flow<Resource<Product>> = flow {
//        emit(Resource.Loading())
//        try {
//            val productSnapshot = firestore.collection("products").document(productId).get().await()
//            val product = productSnapshot.toObject(Product::class.java)
//
//            emit(Resource.Success(product))
//        } catch (e: Exception) {
//            emit(Resource.Error(e.message.toString()))
//        }
//    }
   suspend fun updateProduct(product: Product): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            Log.d("edit product repo",product.customerImages?.size.toString())
            product.productId?.let { firestore.collection("products").document(it).set(product).await() }
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
    suspend fun deleteProduct(productId: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            firestore.collection("products").document(productId).delete().await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }
    suspend fun getOwnerNumber(ownerID:String): Flow<Resource<String>> = flow{
        emit(Resource.Loading())
//        try{
//            var phoneNumber:Any? =null
//            firestore.collection("users").document(ownerID).get().addOnSuccessListener { document->
//                 phoneNumber = document.get("ownerId")
//
//            }
//            Log.d("edit product repo",phoneNumber.toString())
//            emit(Resource.Success(phoneNumber.toString()))
//
//        }
//        catch (e:Exception){
//            emit(Resource.Error(e.message.toString()))
//        }
        try {
            val documentSnapshot = firestore.collection("user").document(ownerID).get().await()
            val phoneNumber = documentSnapshot.getString("phoneNumber")
            if (phoneNumber != null) {
                emit(Resource.Success(phoneNumber))
            } else {
                emit(Resource.Error("Phone number not found"))
                Log.d("edit product repoo","number not found")
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
            Log.d("edit product repoo",e.message.toString())
        }

    }
}