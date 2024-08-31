package com.example.farmi.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
//import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmi.data.UserType
import com.example.farmi.viewmodels.common.SharedViewModel
import com.example.farmi.viewmodels.common.UserTypeViewModel

sealed class BottomNavigationItem(
    val route:String,
    val title:String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
){
    object Home:BottomNavigationItem(
        "Home_Screen",
        "Home",
        Icons.Filled.Home,
        Icons.Outlined.Home
    )
    object Chat:BottomNavigationItem(
        "Chat_Screen",
        "Assistant",
        Icons.Filled.Face,
        Icons.Outlined.Face
    )
    object MarketPrice:BottomNavigationItem(
        "Market_Price_Screen",
        "Mandi",
        Icons.Filled.Money,
        Icons.Outlined.Money
    )
    object AddItem:BottomNavigationItem(
        "Add_Product_Screen",
        "Add",
        Icons.Filled.AddCircle,
        Icons.Outlined.Add
    )
    object Profile:BottomNavigationItem(
        "Profile_Screen",
        "Profile",
        Icons.Filled.Person,
        Icons.Outlined.Person
    )
    object BuyerHomeScreen:BottomNavigationItem(
        "Buyer_Home_Screen",
        "Home",
        Icons.Filled.Home,
        Icons.Outlined.Home
    )

}
val itemsofSeller = listOf(
    BottomNavigationItem.Home,
    BottomNavigationItem.Chat,
    BottomNavigationItem.MarketPrice,
    BottomNavigationItem.AddItem,
    BottomNavigationItem.Profile,

)
val itemsofBuyer = listOf(
    BottomNavigationItem.BuyerHomeScreen,
    BottomNavigationItem.Chat,
    BottomNavigationItem.MarketPrice,
    BottomNavigationItem.Profile,

    )
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavGraph(
    firebaseAuth: FirebaseAuth,
    userTypeViewModel: UserTypeViewModel = hiltViewModel()
){

    val navController = rememberNavController()
//    val currentRoute by navController.currentBackStackEntryAsState()
    val currentRoute by navController.currentBackStackEntryAsState()
    val userType by userTypeViewModel.userType.collectAsState()
    var items = mutableStateOf( itemsofSeller)
    Log.d("BottomnAVIGATION SCAFFOLD",userType.data.toString())
    if(userType.data == UserType.CUSTOMER){

        items.value = itemsofBuyer
    }
    val selectedItem by remember {
        derivedStateOf {
            items.value.find { it.route == currentRoute?.destination?.route }
        }
    }
    var showBottomBar by rememberSaveable { mutableStateOf(true) }
    var showTopBar by rememberSaveable { mutableStateOf(true) }
    showBottomBar = when (currentRoute?.destination?.route) {
        "Home_Screen" -> true // on this screen bottom bar should be hidden
        "Market_Price_Screen" -> true // here too
        "Add_Product_Screen"->true
        "Profile_Screen"->true
        "Buyer_Home_Screen"->true
        else -> false // in all other cases show bottom bar
    }
    showTopBar =when (currentRoute?.destination?.route) {
        "Home_Screen" -> true // on this screen bottom bar should be hidden
        "Market_Price_Screen" -> true // here too
        "Add_Product_Screen"->true
        "Profile_Screen"->true
        "Buyer_Home_Screen"->true
        else -> false // in all other cases show bottom bar
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = showTopBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {


            CenterAlignedTopAppBar(

                title = {

                    Text(
                        selectedItem?.title ?: " ",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color= MaterialTheme.colorScheme.primary
                    )
                },
                navigationIcon = {
                    if (selectedItem?.title != "Home" ) {
                        IconButton(onClick = {

                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint= MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                actions = {
                    if (selectedItem?.title != "Assistant") {

                        IconButton(onClick = {
                            navController.navigate("Chat_Screen")
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Face,
                                contentDescription = "Ai Assistant",
                                tint= MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

            )
        }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
            ) {
                

            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination
                items.value.forEachIndexed { index, bottomNavigationItem ->
                    if (bottomNavigationItem.route != "Chat_Screen") {


                    val isSelected = currentDestination?.route == bottomNavigationItem.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {

                                navController.navigate(bottomNavigationItem.route)
                                {
//                                popUpTo(navController.graph.findStartDestination().id)
                                    val x = mutableStateOf("Home_Screen")
                                    if(items== itemsofBuyer){
                                         x.value = "Buyer_Home_Screen"
                                    }
                                    popUpTo(x.value)
                                    {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                        },
                        label = {
                            Text(text = bottomNavigationItem.title)
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) {
                                    bottomNavigationItem.selectedIcon
                                } else {
                                    bottomNavigationItem.unselectedIcon
                                },
                                contentDescription = bottomNavigationItem.title
                            )

                        }
                    )
                }
                }

            }
            }
        }
    ) {innerpadding->
        Box(modifier = Modifier.padding(innerpadding)) {

            MasterNavigation(navController,firebaseAuth.currentUser,userType.data)
        }
    }
}