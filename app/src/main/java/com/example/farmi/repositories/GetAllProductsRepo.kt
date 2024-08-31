package com.example.farmi.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.farmi.data.Product
import com.example.farmi.util.Resource
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetAllProductsRepo  @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
){
//    private val currentUser = auth.currentUser
//    suspend fun getProductsForCurrentUser(): LiveData<Resource<List<Product>>> {
//        val resultLiveData = MutableLiveData<Resource<List<Product>>>()
//
//        currentUser?.let { user ->
//            try {
//                val result = firestore.collection("products")
//                    .whereEqualTo("ownerId", user.uid)
//
//                    .get()
//                    .await()
//                Log.d("result",result.documents.size.toString())
//                val productList = mutableListOf<Product>()
//
//                var allProducts = result.toObjects(Product::class.java)
//                Log.d("allproducts",allProducts.toString())
//                for (document in result.documents) {
//
//                    Log.d("count","1")
////                    val product = document.toObject(Product::class.java)
////                    Log.d("product->",product?.name.toString())
////                    product?.let {
////                        productList.add(it)
////                    }
//                }
//
//                resultLiveData.postValue(Resource.Success(allProducts))
//            } catch (e: Exception) {
//                resultLiveData.postValue(Resource.Error(e.message.toString()))
//            }
//        } ?: run {
//            resultLiveData.postValue(Resource.Error("User not logged in"))
//        }
//
//        return resultLiveData
//    }
    private val currentUser = auth.currentUser


     suspend fun getProductsForCurrentUser(): Flow<Resource<List<Product>>> = flow {
//         currentUser?.let { Log.d("get all products repo", it.uid) }
        currentUser?.let { user ->
            try {
//                Log.d("get all products  repo","current user is not null")
                emit(Resource.Loading())
                val result = firestore.collection("products")
                    .whereEqualTo("ownerId", user.uid)
                    .get()
                    .await()
//                Log.d("get all product repo",result.toString())
                val productList = result.toObjects(Product::class.java)
//                Log.d("get all products repo",productList.toString())
                emit(Resource.Success(productList))

            } catch (e: Exception) {
                Log.d("get all products repo","inside error"+e.message)
                emit(Resource.Error(e.message.toString()))
            }
        } ?: emit(Resource.Error("User not logged in"))
    }
    suspend fun getProductsBasedOnLocation(latitude: Double, longitude: Double, range: Double):Flow<Resource<List<Product>>> = flow {
            try {
                val userLocation = GeoLocation(latitude, longitude)

                val querySnapshot: QuerySnapshot = firestore.collection("products").get().await()
                val productList = mutableListOf<Product>()

                for (document in querySnapshot.documents) {
                    val location = document.getGeoPoint("location") ?: continue

                    val distance = GeoFireUtils.getDistanceBetween(userLocation, GeoLocation(location.latitude,location.longitude))
                    val distanceInKm = distance / 1000.0 // Convert to kilometers
                    Log.d("get all products repo",distanceInKm.toString())
                    if (distanceInKm <= range) {

                        val product = document.toObject(Product::class.java)
                        productList.add(product!!)
                    }
                }
                emit(Resource.Success(productList))
//                return productList
            }
            catch (e:Exception){
                emit(Resource.Error(e.message.toString()))
            }
    }
}