package com.example.farmi.screens.seller

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CameraEnhance
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.Dp

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.farmi.AddProduct.screen.ImageSelectionButton
import com.example.farmi.AddProduct.viewmodel.LatLng
import com.example.farmi.AddProduct.viewmodel.fetchLocation
import com.example.farmi.AddProduct.viewmodel.requestLocationPermission
import com.example.farmi.screens.buyer.ButtonWithIcon
import com.example.farmi.util.Resource
import com.example.farmi.viewmodels.common.SharedViewModel
import com.example.farmi.viewmodels.seller.EditProductViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.Delay
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalPagerApi::class,
    ExperimentalMaterial3Api::class, InternalCoroutinesApi::class
)
@Composable
fun EditProductScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel = hiltViewModel(),
    editProductViewModel: EditProductViewModel = hiltViewModel()

) {

    val product by sharedViewModel.selectedProduct.collectAsState()
    val editProductResult  by editProductViewModel.editProductState.collectAsState()


    val pagerState = rememberPagerState(initialPage = 0)

    val context = LocalContext.current
    var location = remember {
        mutableStateOf<LatLng?>(null)
    }
    var images by rememberSaveable {
        mutableStateOf(listOf<Uri>())
    }

    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    val locationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            fetchLocation(fusedLocationClient, location)
        } else {
            // Handle location permission denied
        }
    }
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, handle accordingly
            // Here you might want to call the function to fetch images or any other action
        } else {
            // Permission denied, handle accordingly
            Toast.makeText(context, "Storage permission is required to select images", Toast.LENGTH_SHORT).show()
        }
    }
    var newProduct by  remember {
        mutableStateOf(product)
    }

    if(newProduct!=null){
        val imageSlider = newProduct!!.images
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(text="Product", style = MaterialTheme.typography.titleMedium) },
                    colors= TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                    ),

                    navigationIcon = {
                        IconButton(onClick = {

                            navController.popBackStack()
                        }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) {

//            Spacer(modifier = Modifier.height(it.calculateTopPadding()))


            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(top = it.calculateTopPadding())) {
                item(1) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        HorizontalPager(
                            count = if (imageSlider != null) imageSlider.size + 1 else 0,
                            state = pagerState,
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            itemSpacing = 4.dp,
                            modifier = Modifier
                                .height(200.dp)
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) { page ->
                            if (page < (imageSlider?.size ?: 0)) {
                                Card(
                                    shape = RoundedCornerShape(6.dp),

                                    ) {
                                    AsyncImage(
                                        model = imageSlider?.get(page),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            } else {
                                    Column(modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally) {
                                        if(images.size!=0){
                                            LazyRow(modifier = Modifier.fillMaxWidth(),
                                                contentPadding = PaddingValues(10.dp)
                                            ){
                                                items(images.size){index ->
                                                    AsyncImage(
                                                        model = images[index],
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(100.dp)
                                                            .padding(end = 4.dp),

                                                    )
                                                }
                                            }
                                        }
                                        ImageSelectionButton(
                                            storagePermissionLauncher,
                                            modifier = Modifier
                                                .height(48.dp)
                                                .padding(vertical = 4.dp, horizontal = 20.dp)

                                        ) { selectedImages ->
                                            images = selectedImages
                                        }
                                    }


                            }


                        }

                        HorizontalPagerIndicator(
                            pagerState = pagerState,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(8.dp),
                            activeColor = MaterialTheme.colorScheme.primary,
                            inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
                item(2) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .border(
                                    1.dp, Color.Gray, RectangleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                MyTextField("Name", newProduct!!.name, enabled = true) {
//                                newProduct!!.name = it
                                    newProduct = newProduct!!.copy(name = it)
                                }
                                Divider(modifier = Modifier.padding(15.dp, 0.dp))
                                MyTextField("Category", newProduct!!.category, enabled = true) {
                                    newProduct = newProduct!!.copy(category = it)
                                }
//                            Text(text = product?.name.toString())
//                            Text(text = newProduct?.name.toString())
                            }

                        }


                    }
                }
                item(3) {
                    Column(modifier = Modifier.fillMaxWidth()) {

                        Text(
                            text = "Price And Stock",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(8.dp, 0.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .border(1.dp, Color.Gray, RectangleShape)
                        ) {

                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp, 0.dp)
                            ) {


                                MyTextField(
                                    "Price",
                                    value = newProduct!!.price.toString(),
                                    isNumber = true,
                                    enabled = true
                                ) {
                                    if(it.isNotEmpty()){
                                        try {
                                            val doubleValue = it.toDouble()
                                            newProduct = newProduct!!.copy(price = doubleValue)
                                        } catch (e: Exception) {

//                                            Toast.makeText(context, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show()
                                            newProduct = newProduct!!.copy(price = 0.0) // Set a default value

                                        }
                                    }
                                }
                                Divider(modifier = Modifier.padding(15.dp, 0.dp))
                                MyTextField(
                                    "Quantity",
                                    newProduct!!.quantity.toString(),
                                    isNumber = true,
                                    enabled = true
                                ) {
                                    if(it.isNotEmpty()){
                                        try {
                                            val doubleValue = it.toDouble()
                                            newProduct = newProduct!!.copy(quantity = doubleValue)
                                        } catch (e: NumberFormatException) {

//                                            Toast.makeText(context, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show()
                                            newProduct = newProduct!!.copy(quantity = 0.0) // Set a default value

                                        }
                                    }

                                }
//                            Text(text = product?.price.toString())
//                            Text(text = newProduct?.price.toString())
                            }
                        }


                    }

                }
                item(4) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "Location",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(8.dp, 0.dp),
                            color = MaterialTheme.colorScheme.primary
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        ) {

                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp, 0.dp)
                            ) {


                                MyTextField(
                                    "Longitude",
                                    newProduct!!.location?.longitude.toString(),
                                    enabled = false
                                ) {

                                }
                                Divider(modifier = Modifier.padding(15.dp, 0.dp))
                                MyTextField(
                                    "Latitude",
                                    newProduct!!.location?.latitude.toString(),
                                    enabled = false
                                ) {

                                }
                                Divider(modifier = Modifier.padding(15.dp, 0.dp))
                                if(location.value!=null){
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly){
                                        Text(text = "Updated ",
                                            color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelSmall)
                                        Text(text = "Latitude "+location.value?.latitude.toString(),
                                            color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelSmall)
                                        Text(text = "Longitude "+location.value?.longitude.toString(),
                                            color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.labelSmall)
                                    }
                                }


                                OutlinedButton(
                                    onClick = {
                                        editProductViewModel.viewModelScope.launch {
                                            // First task: Request location permission
                                            requestLocationPermission(
                                                fusedLocationClient,
                                                locationPermissionLauncher,
                                                context,
                                                location
                                            )


                                            // Second task: Update newProduct with location after location permission is granted
                                            if (location.value != null) {
                                                Log.d(
                                                    "inside update location",
                                                    location.value!!.latitude.toString()
                                                )
                                                val currentLocation = location.value
                                                newProduct = newProduct!!.copy(
                                                    location = currentLocation?.let {
                                                        GeoPoint(
                                                            it.latitude,
                                                            it.longitude
                                                        )
                                                    } ?: GeoPoint(1.0, 0.0)
                                                )
                                            }
                                        }
                                    },


                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(6.dp, 0.dp),
                                ) {
                                    Text(text = "Update Location")
                                }
                            }
                        }

                    }
                }

                item(5) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Available For Sale",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(4.dp, 0.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        var checked by rememberSaveable {
                            mutableStateOf(newProduct!!.available ?: false)
                        }
                        Switch(checked = checked, onCheckedChange = {
                            checked = it
                            newProduct = newProduct!!.copy(available = checked)
                        },

                            colors = SwitchDefaults.colors(),
                            thumbContent = if (checked) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(SwitchDefaults.IconSize),
                                    )
                                }
                            } else {
                                null
                            }
                        )

                    }

                }
                item(7) {
                    when (editProductResult) {
                        is Resource.Error -> {
                            LaunchedEffect(key1 = Unit) {
                                Toast.makeText(
                                    context,
                                    "Something Went Wrong,Try Again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            Button(onClick = {
                                if (product!! != newProduct || images.size !=0) {
                                    editProductViewModel.updateProduct(newProduct!!, images = images)
                                } else {

                                    Toast.makeText(context, "Nothing changed", Toast.LENGTH_SHORT)
                                        .show()
                                }


                            },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)) {
                                Text(text = "Update Product")

                            }
                            Text(text = "Update Product")



                        }

                        is Resource.Loading -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.widthIn(20.dp)

                                )
                            }

                        }

                        is Resource.Success -> {
                            LaunchedEffect(key1 = Unit) {
                                Toast.makeText(
                                    context,
                                    "Product Updated Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

//                        navController.navigate("Home_Screen")
                            navController.popBackStack("Edit_Product_Screen", inclusive = true)
                            navController.navigate("Home_Screen")

                        }

                        is Resource.Unspecified -> {
                            Button(onClick = {
                                if (product!! != newProduct || images.size !=0) {
                                    editProductViewModel.updateProduct(newProduct!!, images = images)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "No Changes are there",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                            }, modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)) {
                                Text(text = "Update Product")

                            }
                        }
                    }

                }
            }
        }

    }


//    DisposableEffect(Unit) {
//        onDispose {
//            sharedViewModel.clearSelectedProduct()
//        }
//    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTextField(title:String,value:String?,enabled:Boolean? = true,isNumber:Boolean = false,onClick:(String)->Unit) {
    var textfieldvalue by remember {
        mutableStateOf(value)
    }
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(4.dp, 0.dp, 0.dp, 0.dp)
        , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.secondary
        )
        TextField(
            value = textfieldvalue?:"", // Replace with your initial text value
            onValueChange = {
                textfieldvalue = it
                    onClick(it)
//                if(!isNumber) {
//
//
//                    textfieldvalue = it
//                    onClick(it)
//                }
//                else{
//                    if (it.isEmpty() || it.matches( Regex("^\\d+\$"))) {
//                        textfieldvalue = it
//                    }
//                    if(textfieldvalue!=null && textfieldvalue!!.isNotEmpty()){
//                        onClick(it)
//                    }
//                }
                            },
            keyboardOptions= if(isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions(keyboardType = KeyboardType.Text) ,
            modifier = Modifier
                .background(
                    Color.Transparent
                )
                .widthIn(5.dp, Dp.Infinity),
            singleLine = true,

            colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent, // Remove focus indicator
                unfocusedIndicatorColor = Color.Transparent  // Remove default indicator
            ),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.End
            ) // Optional: Set text color
            ,
            enabled = enabled?:true,
            placeholder = { Text(text = "Enter "+title, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.End)}
        )
    }

}