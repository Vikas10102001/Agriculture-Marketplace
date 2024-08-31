package com.example.farmi.viewmodels.seller

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.AddProduct.repository.AddProductRepository
import com.example.farmi.data.Product
import com.example.farmi.repositories.EditProductRepo
import com.example.farmi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.net.URI
import javax.inject.Inject

@HiltViewModel
class EditProductViewModel @Inject constructor(
 private  val editProductRepo: EditProductRepo,
 private val addProductRepository: AddProductRepository
):ViewModel() {
    private val _editProductState = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val editProductState: StateFlow<Resource<Unit>> = _editProductState


    fun updateProduct(product: Product,images:List<Uri>?) {
        viewModelScope.launch {
            _editProductState.value = Resource.Loading()
            val newProduct = product
            if(!images.isNullOrEmpty()){
                val imageUploadResult= addProductRepository.uploadImages(images)
                if(imageUploadResult is Resource.Success){
                    val combinedImages = product.images?.toMutableList() ?: mutableListOf()
                    combinedImages.addAll(imageUploadResult.data ?: emptyList())
                    newProduct.images = combinedImages.toList()
                }
            }
            editProductRepo.updateProduct(newProduct)
                .catch { exception ->
                    _editProductState.value = Resource.Error(exception.message.toString())
                }
                .collect { resource ->
                    _editProductState.value = Resource.Success(Unit)
                }
        }
    }
}