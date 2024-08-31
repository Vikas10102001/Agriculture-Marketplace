package com.example.farmi.authentication.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.farmi.R
import com.example.farmi.authentication.viewmodels.UserRegisterViewModel
import com.example.farmi.data.User
import com.example.farmi.data.UserType
import com.example.farmi.util.Resource
import java.util.UUID


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    userRegisterViewModel: UserRegisterViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val result = userRegisterViewModel.register.collectAsState(initial = Resource.Unspecified())
    val context = LocalContext.current
    var userType by remember { mutableStateOf(UserType.CUSTOMER) }

    Box(
        modifier = Modifier
            .fillMaxSize(),
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(
//                        MaterialTheme.colors.primary,
//                        MaterialTheme.colors.primaryVariant
//                    )
//                )
//            )
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
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(text = "Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            focusedBorderColor = MaterialTheme.colors.primaryVariant,
//                            cursorColor = MaterialTheme.colors.primaryVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            focusedBorderColor = MaterialTheme.colors.primaryVariant,
//                            cursorColor = MaterialTheme.colors.primaryVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text(text = "Phone Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            focusedBorderColor = MaterialTheme.colors.primaryVariant,
//                            cursorColor = MaterialTheme.colors.primaryVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
//                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "User Type:",
                                style = MaterialTheme.typography.bodyLarge,
//                                modifier = Modifier.weight(1f)
                            )
                        }

                        Column(
//                            modifier = Modifier.weight(2f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally

                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                RadioButton(
                                    selected = userType == UserType.CUSTOMER,
                                    onClick = { userType = UserType.CUSTOMER },
                                    colors = RadioButtonDefaults.colors(
//                                selectedColor = MaterialTheme.colors.primaryVariant,
//                                unselectedColor = MaterialTheme.colors.onBackground
                                    )
                                )
                                Text(
                                    text = "Customer",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {

                                RadioButton(
                                    selected = userType == UserType.PROVIDER,
                                    onClick = { userType = UserType.PROVIDER },
                                    colors = RadioButtonDefaults.colors(
//                                selectedColor = MaterialTheme.colors.primaryVariant,
//                                unselectedColor = MaterialTheme.colors.onBackground
                                    )
                                )
                                Text(
                                    text = "Provider",
                                    style = MaterialTheme.typography.bodyLarge,
//                                    modifier = Modifier.weight(1f)
                                )
                            }

                        }



                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
//                            focusedBorderColor = MaterialTheme.colors.primaryVariant,
//                            cursorColor = MaterialTheme.colors.primaryVariant
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val id = UUID.randomUUID().toString()
                            val user = User(id, name, email, phoneNumber, userType.toString())
                            userRegisterViewModel.createAccountWithEmailAndPassword(user, password)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
//                            containerColor = MaterialTheme.colors.primaryVariant,
//                            contentColor = MaterialTheme.colors.onPrimary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "Register")
                    }
                }
            }

            when (result.value) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 4.dp
                    )
                }
                is Resource.Success -> {

                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                        navController.navigate("Login_Screen")
                    }
                }
                is Resource.Error -> {
                    LaunchedEffect(Unit ) {
                        Toast.makeText(context, result.value.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                is Resource.Unspecified -> {}
            }
        }
    }
}