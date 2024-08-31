package com.example.farmi.viewmodels.common

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.data.User
import com.example.farmi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private  val auth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {

    val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()
    init {
        getUser()
    }

    fun getUser(){
        viewModelScope.launch {
            _user.emit(Resource.Loading())
            Log.d("profile view model",auth.uid?:"ankit")
        }
        db.collection("user").document(auth.uid!!)

            .addSnapshotListener{ result,error->
                if(error!=null){
                    viewModelScope.launch {
                        _user.emit(Resource.Error(error.message.toString()))
                    }
                }
                else{
                    val currentuser = result?.toObject(User::class.java)
                    currentuser?.let {
                        viewModelScope.launch {
                            _user.emit(Resource.Success(currentuser))
                        }
                    }

                }

            }
    }
    fun logout(){
        auth.signOut()
    }
}