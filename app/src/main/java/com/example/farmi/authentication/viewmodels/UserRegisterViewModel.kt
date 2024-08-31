package com.example.farmi.authentication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.authentication.repository.UserRegisterRepository
import com.example.farmi.data.User
import com.example.farmi.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRegisterViewModel @Inject constructor(
    private  val registerRepository: UserRegisterRepository

): ViewModel() {
    val register : Flow<Resource<User>> = registerRepository.register
    fun createAccountWithEmailAndPassword(user: User, password: String) {
        viewModelScope.launch {
            registerRepository.createAccountWithEmailAndPassword(user, password)
        }
    }
}