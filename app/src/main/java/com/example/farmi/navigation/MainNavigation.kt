package com.example.farmi.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.farmi.AddProduct.screen.AddProductScreen
import com.example.farmi.AiAssistant.screens.ChatScreen
import com.example.farmi.MandiPrices.screen.MarketPriceScreen
import com.example.farmi.authentication.screens.LoginScreen
import com.example.farmi.authentication.screens.RegisterScreen
import com.example.farmi.data.UserType
import com.example.farmi.screens.buyer.BuyerHomeScreen
import com.example.farmi.screens.buyer.ProductDetailScreen
import com.example.farmi.screens.common.ProfileScreen
import com.example.farmi.screens.seller.EditProductScreen
import com.example.farmi.screens.seller.HomeScreen
import com.example.farmi.viewmodels.common.SharedViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun MasterNavigation (
    navController: NavHostController,
    currentUser:FirebaseUser?,
    userType: UserType?,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val currentuser = Firebase.auth.currentUser

    var startDestination = "Auth_Graph"
    if(currentuser !=null){
//                    startDestination = "App_Navigation"
        if(userType == UserType.CUSTOMER){
            startDestination = "Buyer_App_Navigation"
        }
        else if(userType == UserType.PROVIDER){
            startDestination = "App_Navigation"
        }
        else{
            startDestination = "App_Navigation"
        }

//        startDestination = "test"
    }

    NavHost(navController = navController,startDestination = startDestination ){

        composable("test"){
            LoginScreen(navController = navController)
        }
        navigation(route= "Auth_Graph",startDestination = "Login_Screen"){
            composable("Login_Screen"){
                LoginScreen(navController)
            }
            composable("Register_Screen"){
                RegisterScreen(navController)
            }
        }
        navigation(route = "App_Navigation",startDestination = "Home_Screen" ){
            composable("Home_Screen"){
//            AddProductScreen()
                HomeScreen(sharedViewModel = sharedViewModel,navController)

            }
            composable("Chat_Screen"){
                ChatScreen()
            }
            composable("Market_Price_Screen"){
                MarketPriceScreen()
            }
            composable("Add_Product_Screen"){
                AddProductScreen()
            }
            composable("Profile_Screen"){
                ProfileScreen(navController = navController)
            }
            navigation(route="Edit_Product",startDestination = "Edit_Product_Screen"){
                composable("Edit_Product_Screen"){backStackEntry->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("Home_Screen")
                    }
                    val parentSharedViewModel = hiltViewModel<SharedViewModel>(parentEntry)
                    EditProductScreen(navController,sharedViewModel)
                }
            }

        }
        navigation(route = "Buyer_App_Navigation",startDestination = "Buyer_Home_Screen"){
            composable("Buyer_Home_Screen"){
                BuyerHomeScreen(navController)
            }
            composable("Chat_Screen"){
                ChatScreen()
            }
            composable("Market_Price_Screen"){
                MarketPriceScreen()
            }

            composable("Profile_Screen"){
                ProfileScreen(navController = navController)
            }
            navigation(route="Product_Detail",startDestination = "Product_Detail_Screen"){
                composable("Product_Detail_Screen"){backStackEntry->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("Buyer_Home_Screen")
                    }
                    val parentSharedViewModel = hiltViewModel<SharedViewModel>(parentEntry)
                    ProductDetailScreen(navController,parentSharedViewModel)
                }
            }


        }

    }

}