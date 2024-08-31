package com.example.farmi.authentication.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmi.R
import com.example.farmi.authentication.viewmodels.UserLoginViewModel
import com.example.farmi.data.UserType
import com.example.farmi.util.Resource
import com.example.farmi.viewmodels.common.UserTypeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    userLoginViewModel: UserLoginViewModel = hiltViewModel(),
    userTypeViewModel: UserTypeViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val login = userLoginViewModel.login.collectAsState()
    val userType = userTypeViewModel.userType.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(
//                        MaterialTheme.colorScheme.primary,
////                        MaterialTheme.colorScheme.primaryVariant
//                    )
//                )
//            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.farmi_app_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 10.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            focusedBorderColor = MaterialTheme.colorScheme.onPrimary,
//                            cursorColor = MaterialTheme.colorScheme.onSecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            focusedBorderColor = MaterialTheme.colorScheme.primary,
//                            cursorColor = MaterialTheme.colorScheme.primaryVariant
                        )
                    )
//                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = { navController.navigate("Register_Screen") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "Don't have an account? Register", color = MaterialTheme.colorScheme.primary)
                    }

//                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { userLoginViewModel.loginWithEmailAndPassword(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colorScheme.primaryVariant,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Login")
                    }
                }
            }
//            Text(text = "ankit")
            when (login.value) {
                is Resource.Error -> {
                    Toast.makeText(context, login.value.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }

                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 24.dp),
//                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 4.dp
                    )
                }

                is Resource.Success -> {
                    userTypeViewModel.getUserType()
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                    }
                    Log.d("login screen", userType.value.data.toString())
                    if (userType.value.data == UserType.CUSTOMER) {
                        navController.navigate("Buyer_App_Navigation")
                        {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    } else {
                        navController.navigate("App_Navigation"){
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }

                is Resource.Unspecified -> {}
            }
        }
    }
}