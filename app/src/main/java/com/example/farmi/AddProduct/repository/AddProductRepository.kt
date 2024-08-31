package com.example.farmi.AddProduct.repository

import android.net.Uri
import com.example.farmi.data.Product
import com.example.farmi.util.Constants.Product_Collection
import com.example.farmi.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class AddProductRepository  @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
){
    suspend fun addProduct(product: Product):Resource<Product>{
        return try{
            firestore.collection(Product_Collection).add(product).await()
            Resource.Success(product)
        }
        catch (e:Exception){
            Resource.Error(e.message ?: "Failed to add product")
        }
    }
    suspend fun uploadImages(images: List<Uri>): Resource<List<String>> {
        val uploadedImageUrls = mutableListOf<String>()
        return try {
            for (imageUri in images) {
                val storageRef = storage.reference.child("images/${UUID.randomUUID()}")
                val uploadTask = storageRef.putFile(imageUri).await()
                val imageUrl = storageRef.downloadUrl.await().toString()
                uploadedImageUrls.add(imageUrl)
            }
            Resource.Success(uploadedImageUrls)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to upload images")
        }
    }

}