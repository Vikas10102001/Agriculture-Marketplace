package com.example.farmi.viewmodels.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.data.User
import com.example.farmi.data.UserType
import com.example.farmi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserTypeViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
):ViewModel() {

    private val _userType = MutableStateFlow<Resource<UserType>>(Resource.Unspecified())
    val userType = _userType.asStateFlow()

    init {
        getUserType()
    }
     fun getUserType(
    ){
        val currentUser = auth.currentUser
        if(currentUser !=null){
            viewModelScope.launch {
                 determineUserType()
            }
        }
    }
    private suspend fun determineUserType(){
        firestore.collection("user").document(auth.uid!!)
            .addSnapshotListener{ result,error->
                if(error!=null){
                    viewModelScope.launch {
                        _userType.emit(Resource.Error(error.message.toString()))
                    }
                }
                else{
                    val currentUser = result?.toObject(User::class.java)
                    currentUser?.let {
                        viewModelScope.launch {
                            if(currentUser.userType== UserType.CUSTOMER.toString()){
                                _userType.emit(Resource.Success(UserType.CUSTOMER))
                            }
                            else{
                                _userType.emit(Resource.Success(UserType.PROVIDER))
                            }

                        }
                    }
                }
            }
    }



}