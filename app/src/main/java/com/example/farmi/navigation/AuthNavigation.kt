package com.example.farmi.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.farmi.authentication.screens.LoginScreen
import com.example.farmi.authentication.screens.RegisterScreen

@Composable
fun NavGraphBuilder.AuthNavigation(
    navController: NavController
) {
    navigation(route = "Auth_Graph",startDestination = "Login_Screen"){
        composable("Login_Screen"){
            LoginScreen(navController)
        }
        composable("Register_Screen"){
            RegisterScreen(navController)
        }
    }
//    NavHost(navController = navController, startDestination = "Login_Screen" ){
//        composable("Login_Screen"){
//            LoginScreen(navController)
//        }
//        composable("Register_Screen"){
//            RegisterScreen(navController)
//        }
//    }

}