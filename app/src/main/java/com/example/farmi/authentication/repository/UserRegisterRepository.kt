package com.example.farmi.authentication.repository

import com.example.farmi.data.User
import com.example.farmi.util.Constants.User_Collection
import com.example.farmi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRegisterRepository @Inject constructor(
    private  val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

//    private val _validation = Channel<RegisterFieldsState>()
//    val validation: Flow<RegisterFieldsState> = _validation.receiveAsFlow()

    suspend fun createAccountWithEmailAndPassword(user: User, password: String) {
        if (shouldRegister(user, password)) {
            _register.value = Resource.Loading()
            try {
                val authResult = firebaseAuth.createUserWithEmailAndPassword(user.email, password).await()
                authResult.user?.let { saveUserInfo(it.uid, user) }
            } catch (e: Exception) {
                _register.value = Resource.Error(e.message.toString())
            }
        } else {
//            val registerFieldsState = RegisterFieldsState(
//                validateEmail(user.email),
//                validatePassword(password),
//                validateFirstName(user.firstName)
//            )
//            _validation.send(registerFieldsState)
        }
    }

    private suspend fun saveUserInfo(userUid: String, user: User) {
        try {
            firestore.collection(User_Collection)
                .document(userUid)
                .set(user)
                .await()
            _register.value = Resource.Success(user)
        } catch (e: Exception) {
            _register.value = Resource.Error(e.message.toString())
        }
    }
    private fun shouldRegister(user: User, password: String): Boolean {
//        val emailValidation = validateEmail(user.email)
//        val passwordValidation = validatePassword(password)
//        val firstNameValidation = validateFirstName(user.firstName)
//        return emailValidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success && firstNameValidation is RegisterValidation.Success
        return true
    }
}