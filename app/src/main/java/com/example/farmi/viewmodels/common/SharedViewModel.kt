package com.example.farmi.viewmodels.common

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.farmi.data.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(

) : ViewModel() {
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    fun setSelectedProduct(product: Product) {
        _selectedProduct.value = product
        Log.d("shared view model",product.name.toString())
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }
}