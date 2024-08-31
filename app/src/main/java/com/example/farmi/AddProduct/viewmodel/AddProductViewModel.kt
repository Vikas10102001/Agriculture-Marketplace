package com.example.farmi.AddProduct.viewmodel

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.AddProduct.repository.AddProductRepository
import com.example.farmi.data.Product
import com.example.farmi.util.Resource
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import android.Manifest
import android.content.Context
import androidx.activity.result.ActivityResultLauncher
import com.google.firebase.firestore.FirebaseFirestore


@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val addProductRepository: AddProductRepository
):ViewModel(){

    private val _addProductResult = MutableStateFlow<Resource<Product>>(Resource.Unspecified())
    val addProductResult : StateFlow<Resource<Product>> = _addProductResult

//    fun uploadImagesAndAddProduct(product: Product, images: List<Uri>,
//                                  location: LatLng?){
//        viewModelScope.launch {
//            _addProductResult.value= Resource.Loading()
//            val imageUploadResult = addProductRepository.uploadImages(images)
//            if(imageUploadResult is Resource.Success){
//                val newProduct = Product(
//                    productId = UUID.randomUUID().toString(),
//                    name = product.name,
//                    category = product.category,
//                    price = product.price,
//                    available = product.available,
//                    date= SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
//                    ownerId =product.ownerId ,
//                    location = location?.let { GeoPoint(it.latitude, it.longitude) } ?: GeoPoint(0.0, 0.0),
//                    images = imageUploadResult.data ?: emptyList() ,
//                    quantity = product.quantity
//                )
////                _addProductResult.value= Resource.Loading()
//                _addProductResult.value = addProductRepository.addProduct(newProduct)
//            }
//            else{
//                _addProductResult.value = Resource.Error("Failed to Upload Images")
//            }
//        }
//    }
fun uploadImagesAndAddProduct(product: Product, images: List<Uri>, location: LatLng?) {
    viewModelScope.launch {
        _addProductResult.value = Resource.Loading()

        val imageUploadResult = addProductRepository.uploadImages(images)

        if (imageUploadResult is Resource.Success) {
            // Create a new product with the provided data
            val newProduct = Product(
                name = product.name,
                category = product.category,
                price = product.price,
                available = product.available,
                date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()),
                ownerId = product.ownerId,
                location = location?.let { GeoPoint(it.latitude, it.longitude) } ?: GeoPoint(0.0, 0.0),
                images = imageUploadResult.data ?: emptyList(),
                quantity = product.quantity
            )

            // Reference the collection "products"
            val productsCollection = FirebaseFirestore.getInstance().collection("products")

            // Add the product to Firestore with auto-generated document ID
            productsCollection.add(newProduct)
                .addOnSuccessListener { documentReference ->
                    // Retrieve the auto-generated document ID
                    val productId = documentReference.id

                    // Update the product ID with the auto-generated ID
                    newProduct.productId = productId

                    // Update the product in Firestore with the assigned product ID
                    documentReference.set(newProduct)
                        .addOnSuccessListener {
                            _addProductResult.value = Resource.Success(newProduct) // Unit since no data to return
                        }
                        .addOnFailureListener { e ->
                            _addProductResult.value = Resource.Error("Failed to update product with generated ID: ${e.message}")
                        }
                }
                .addOnFailureListener { e ->
                    _addProductResult.value = Resource.Error("Failed to add product with generated ID: ${e.message}")
                }
        } else {
            _addProductResult.value = Resource.Error("Failed to Upload Images")
        }
    }
}



}

suspend fun requestLocationPermission(
    fusedLocationProviderClient: FusedLocationProviderClient,
    locationPermissionLauncher: ActivityResultLauncher<String>,
    context: Context,
    location: MutableState<LatLng?>,

    ) {
    when {
        // Check if permission is already granted
        androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
            fetchLocation(fusedLocationProviderClient,location)
        }
        // Request permission if not granted
        else -> {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}
data class LatLng(
    val latitude: Double,
    val longitude: Double
)
@SuppressLint("MissingPermission")
fun fetchLocation(
    fusedLocationClient: FusedLocationProviderClient,
    location: MutableState<LatLng?>
) {
    fusedLocationClient.lastLocation
        .addOnSuccessListener { locationResult ->
            locationResult?.let { locationData ->
                location.value = LatLng(locationData.latitude,locationData.longitude)
                Log.d("location", location.value!!.latitude.toString())
            } ?: run {
                location.value = null // Handle no location available
            }
        }
        .addOnFailureListener { exception ->
            location.value = null // Handle failure to retrieve location
        }
}