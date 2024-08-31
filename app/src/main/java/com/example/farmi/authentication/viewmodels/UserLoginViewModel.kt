package com.example.farmi.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.authentication.repository.UserLoginRepository
import com.example.farmi.util.Resource
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserLoginViewModel @Inject constructor(
    private val loginRepository: UserLoginRepository

): ViewModel() {

    private val _login = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val login: StateFlow<Resource<FirebaseUser>> = _login


    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _login.value = Resource.Loading()
            val result = loginRepository.signInWithEmailAndPassword(email, password)
            _login.value = result
        }
    }

}