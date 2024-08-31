package com.example.farmi.viewmodels.buyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.farmi.data.Product
import com.example.farmi.repositories.GetAllProductsRepo
import com.example.farmi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyerHomeScreenViewModel @Inject constructor(
    private val getAllProductsRepo: GetAllProductsRepo
):ViewModel() {
    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val allProducts: StateFlow<Resource<List<Product>>> = _allProducts


    fun fetchProducts(lat:Double,lng:Double,range: Double) {
        viewModelScope.launch {
            getAllProductsRepo.getProductsBasedOnLocation(lat,lng,range)
                .catch { exception ->
                    _allProducts.value = Resource.Error(exception.message.toString())
                }
                .collect { resource ->
                    _allProducts.value = resource
                }
        }
    }

}