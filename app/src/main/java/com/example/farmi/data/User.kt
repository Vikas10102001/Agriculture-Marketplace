package com.example.farmi.data

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val phoneNumber: String,
    val userType: String
){
    constructor() : this("", "", "", "", "")
}
enum class UserType {
    CUSTOMER,
    PROVIDER
}