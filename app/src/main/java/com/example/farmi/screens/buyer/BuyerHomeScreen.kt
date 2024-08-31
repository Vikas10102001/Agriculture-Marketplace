package com.example.farmi.screens.buyer

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.farmi.AddProduct.viewmodel.LatLng
import com.example.farmi.AddProduct.viewmodel.fetchLocation
import com.example.farmi.AddProduct.viewmodel.requestLocationPermission
import com.example.farmi.MandiPrices.data.response.MarketPriceRequest
import com.example.farmi.MandiPrices.screen.FilterDropdown
import com.example.farmi.screens.seller.ProductItem
import com.example.farmi.ui.theme.FarmiPrimary
import com.example.farmi.viewmodels.buyer.BuyerHomeScreenViewModel
import com.example.farmi.viewmodels.common.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerHomeScreen(
    navController: NavController,
    buyerHomeScreenViewModel: BuyerHomeScreenViewModel = hiltViewModel(),
    sharedViewModel:SharedViewModel = hiltViewModel()

) {
    val allProducts = buyerHomeScreenViewModel.allProducts.collectAsState()
    val showFilters = remember { mutableStateOf(false) }
    val range = mutableStateOf(90.0)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context= LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    var location = remember {
        mutableStateOf<LatLng?>(null)
    }
    val permissionDenied = remember { mutableStateOf(false) }
    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            fetchLocation(fusedLocationClient, location)
            permissionDenied.value = false
        } else {
            permissionDenied.value = true
            // Handle location permission denied
        }
    }

    LaunchedEffect(Unit) {
        requestLocationPermission(
            fusedLocationClient,
            locationPermissionLauncher,
            context,
            location
        )
    }
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar ={
            TopAppBar(
                title = { Text("Filters", style = MaterialTheme.typography.titleSmall) },
                actions = {
                    if(showFilters.value){
                        IconButton(
                            onClick = { showFilters.value = !showFilters.value }
                        ){
                            Icon(imageVector = Icons.Filled.FilterListOff, contentDescription ="Filters")
                        }
                        IconButton(
                            onClick = {
                                range.value= 90.0
                            }
                        ){
                            Icon(imageVector = Icons.Filled.Clear, contentDescription ="Filters",tint = Color.Red)
                        }
                        IconButton(
                            onClick = {
                                showFilters.value = !showFilters.value
                                if(location.value !=null){
                                    buyerHomeScreenViewModel.fetchProducts(location.value!!.latitude,
                                        location.value!!.longitude,range.value)

                                }
                            }
                        ){
                            Icon(imageVector = Icons.Filled.Check, contentDescription ="Filters",tint = Color.Green)
                        }


                    }
                    else{
                        IconButton(
                            onClick = { showFilters.value = !showFilters.value }
                        ){
                            Icon(imageVector = Icons.Filled.FilterList, contentDescription ="Filters")
                        }
                    }
                },
//                scrollBehavior = scrollBehavior

//                backgroundColor = MaterialTheme.colors.primary,
//                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp, top = it.calculateTopPadding())
//                .verticalScroll(scrollState)
        ) {
            if(showFilters.value){

//                Spacer(modifier = Modifier.height(45.dp))
                DistanceFilter(
                    range = range.value,
                    onRangeChange = { range.value = it }
                )
            }
            Text(
                text = "All Products",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )

            when{
                location.value==null && !permissionDenied.value->{
                    Text("Fetching location...", style = MaterialTheme.typography.bodyLarge)
                }
                permissionDenied.value -> {
                    Column {
                        Text("Location not provided", style = MaterialTheme.typography.bodyLarge)
                        Button(onClick = {
                           buyerHomeScreenViewModel.viewModelScope.launch {
                               requestLocationPermission(
                                   fusedLocationClient,
                                   locationPermissionLauncher,
                                   context,
                                   location
                               )
                           }
                        }) {
                            Text("Request Permission Again")
                        }
                    }
                }
                else-> {
                    LaunchedEffect(Unit){
                        location.value?.latitude?.let { it1 ->
                            buyerHomeScreenViewModel.fetchProducts(
                                it1, location.value!!.longitude,range.value)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(allProducts.value.data?.size ?: 0) { index ->
                            val product = allProducts.value.data?.get(index)
                            if (product != null) {
                                ProductItem(
                                    product = product,
                                    editProduct = {
                                        sharedViewModel.setSelectedProduct(product)
//                                navController.navigate("Edit_Product")
//                                Log.d("Buyer Home Screen",sharedViewModel.selectedProduct.value?.name.toString())

                                        navController.navigate("Product_Detail_Screen")


                                    }
                                )
                            }
                        }
                    }
                }
            }


        }
    }


}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistanceFilter(
    range: Double,
    onRangeChange: (Double) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Distance Range (km)",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column {
                Slider(
                    value = range.toFloat(),
                    onValueChange = { onRangeChange(it.toInt().toDouble()) },
                    valueRange = 1f..500f,
                    steps = 49,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "1 km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )

                    OutlinedTextField(
                        value = String.format("%.1f", range),
                        onValueChange = {
                            if (it.isNotBlank()) {
                                onRangeChange(it.toDoubleOrNull() ?: range)
                            }
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .padding(start = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Range") },
                        colors = OutlinedTextFieldDefaults.colors(
                            //                            textColor = MaterialTheme.colorScheme.onSurface,
                            focusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                        )
                    )

                    Text(
                        text = "500 km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}
