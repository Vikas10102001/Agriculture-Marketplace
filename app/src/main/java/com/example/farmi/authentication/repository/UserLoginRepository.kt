package com.example.farmi.authentication.repository

import com.example.farmi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserLoginRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    suspend fun signInWithEmailAndPassword(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred!")
        }
    }
}