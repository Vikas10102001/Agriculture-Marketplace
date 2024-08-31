package com.example.farmi.viewmodels.buyer

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.AddProduct.repository.AddProductRepository
import com.example.farmi.data.Product
import com.example.farmi.repositories.EditProductRepo
import com.example.farmi.util.Resource
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.GeoPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject
@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
   private val  editProductRepo: EditProductRepo,
    private val addProductRepository: AddProductRepository
):ViewModel() {
    private val _addCustomerImages = MutableStateFlow<Resource<Boolean>>(Resource.Unspecified())
    val addCustomerImages: StateFlow<Resource<Boolean>> = _addCustomerImages
    private val _ownerPhoneNumber = MutableStateFlow<Resource<String>>(Resource.Unspecified())
    val ownerPhoneNumber:StateFlow<Resource<String>> = _ownerPhoneNumber

    fun addCustomerImages(latitude:Double,longitude:Double,product: Product,images:List<Uri>){

        val userLocation = GeoLocation(latitude, longitude)
        val distance = GeoFireUtils.getDistanceBetween(userLocation, GeoLocation(product.location?.latitude ?:0.0,product.location?.longitude?:0.0))
        val distanceInKm = distance / 1000.0 // Convert to kilometers
        if(distanceInKm <= 1.0){
            viewModelScope.launch {
                _addCustomerImages.emit(Resource.Loading())
                try{
                    val imageUploadResult = addProductRepository.uploadImages(images)
                    Log.d("product details viewmodel",imageUploadResult.data.toString())
                    if(imageUploadResult is Resource.Success){
                        val combinedImages = product.customerImages?.toMutableList() ?: mutableListOf()
                        combinedImages.addAll(imageUploadResult.data ?: emptyList())
                        val newProduct = Product(
                            productId=product.productId,
                            name = product.name,
                            category = product.category,
                            price = product.price,
                            available = product.available,
                            date = product.date,
                            ownerId = product.ownerId,
                            location = product.location,
                            images = product.images,
                            quantity = product.quantity,
                            customerImages = combinedImages
                        )
                        Log.d("product details viewmodel",combinedImages.toString())

//                        product.customerImages = combinedImages.toList()
                        editProductRepo.updateProduct(newProduct).catch {
                            _addCustomerImages.emit(Resource.Error("Unable To Upload Images"))
                        }.collect{
                            Log.d("product details viewmodel","updated")
                        }
                        _addCustomerImages.emit(Resource.Success(true))
                    }
                    else{
                        _addCustomerImages.emit(Resource.Error("Unable To Upload Images"))
                    }

                }
                catch (e:Exception){
                    _addCustomerImages.emit(Resource.Error("Unable To Upload Images"))
                }


            }
        }
        else{
            viewModelScope.launch {
                _addCustomerImages.emit(Resource.Success(false))
            }


        }
    }
    fun getOwnerPhoneNumber(ownerId:String){
        viewModelScope.launch {
            _ownerPhoneNumber.emit( Resource.Loading())
           editProductRepo.getOwnerNumber(ownerId)
               .catch {
                   _ownerPhoneNumber.value = Resource.Error(it.message.toString())
                   Log.d("productsviewmodel",_ownerPhoneNumber.value.data.toString())
               }
               .collect{ it->
                   _ownerPhoneNumber.value =it
                   Log.d("productsviewmodel",_ownerPhoneNumber.value.data.toString())
               }



        }
    }
}