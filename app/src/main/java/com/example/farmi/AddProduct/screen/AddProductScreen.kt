package com.example.farmi.AddProduct.screen

import android.Manifest
import android.app.ProgressDialog.show
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CameraEnhance
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.farmi.AddProduct.viewmodel.AddProductViewModel
import com.example.farmi.AddProduct.viewmodel.fetchLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.example.farmi.AddProduct.viewmodel.LatLng
import com.example.farmi.AddProduct.viewmodel.requestLocationPermission
import com.example.farmi.R
import com.example.farmi.data.Product
import com.example.farmi.screens.seller.MyTextField
import com.example.farmi.util.Resource
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun AddProductScreen(
    addProductViewModel: AddProductViewModel = hiltViewModel()
) {
    var name by rememberSaveable {
        mutableStateOf("")
    }
    var category by rememberSaveable {
        mutableStateOf("")
    }
    var price by rememberSaveable {
        mutableStateOf("")
    }
    var quantity by rememberSaveable {
        mutableStateOf("")
    }
    var available by rememberSaveable {
        mutableStateOf<Boolean>(false)
    }
    var location = remember {
        mutableStateOf<LatLng?>(null)
    }
    var images by rememberSaveable {
        mutableStateOf(listOf<Uri>())
    }
    val result = addProductViewModel.addProductResult.collectAsState()
    val context= LocalContext.current

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
    val pagerState = rememberPagerState(initialPage = 0)

//    val imageSlider = !!.images
    LazyColumn(modifier = Modifier.fillMaxSize()){
        item(1){
            Column (
                modifier = Modifier.fillMaxSize()
            ) {
                HorizontalPager(
                    count= if (images != null) images.size + 1 else 0,
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    itemSpacing=4.dp,
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) { page ->
                    if (page < (images?.size ?: 0)) {
                        Card(
                            shape = RoundedCornerShape(6.dp),

                            ) {
                            AsyncImage(
                                model= images?.get(page),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    else{
                        ImageSelectionButton(storagePermissionLauncher,
                            modifier = Modifier
                                .height(48.dp)
                                .padding(vertical = 4.dp, horizontal = 20.dp)) { selectedImages ->
                            images = selectedImages // Pass URIs directly
                        }
                    }


                }

                HorizontalPagerIndicator(
                    pagerState = pagerState,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
            }
        }
        item(2){
            Column(modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .border(
                        1.dp, Color.Gray, RectangleShape
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        MyTextField("Name", name,enabled = true) {
                          name = it
                        }
                        Divider()
                        MyTextField("Category", category,enabled = true) {
                            category = it
                        }
//                            Text(text = product?.name.toString())
//                            Text(text = newProduct?.name.toString())
                    }

                }


            }
        }
        item(3){
            Column(modifier = Modifier.fillMaxWidth()) {

                Text(text = "Price And Stock",fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(8.dp,0.dp),color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .border(1.dp, Color.Gray, RectangleShape)) {

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(2.dp, 0.dp)) {


                        MyTextField("Price", price.toString(), isNumber = true, enabled = true) {
                            if(it.isNotEmpty()){
                                try {
                                    val doubleValue = it.toDouble()
                                    price = doubleValue.toString()
                                } catch (e: NumberFormatException) {

//                                    Toast.makeText(context, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show()
                                    price="0.0"

                                }
                            }
                        }
                        Divider()
                        MyTextField("Quantity", quantity.toString(), isNumber = true, enabled = true) {
                            if(it.isNotEmpty()){
                                try {
                                    val doubleValue = it.toDouble()
                                    quantity = doubleValue.toString()
                                } catch (e: NumberFormatException) {

//                                    Toast.makeText(context, "Invalid input. Please enter a valid number.", Toast.LENGTH_SHORT).show()
                                    quantity="0.0"

                                }
                            }
                        }
//                            Text(text = product?.price.toString())
//                            Text(text = newProduct?.price.toString())
                    }
                }


            }

        }
        item(4){
            Column(modifier = Modifier
                .fillMaxWidth()
            ){
                Text(text = "Location", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(8.dp,0.dp),color = androidx.compose.material3.MaterialTheme.colorScheme.primary)

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .border(1.dp, Color.Gray, RectangleShape)) {

                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(2.dp, 0.dp)) {


                        MyTextField("Longitude", location.value?.longitude.toString(),enabled = false) {

                        }
                        Divider()
                        MyTextField("Latitude", location.value?.latitude.toString(),enabled = false) {

                        }
                        Divider()
                        if(location.value!=null){
                            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly){
                                Text(text = "Updated ",
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                                Text(text = "Latitude "+location.value?.latitude.toString(),
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                                Text(text = "Longitude "+location.value?.longitude.toString(),
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
                            }
                        }
                        OutlinedButton(
                            onClick = {

                                addProductViewModel.viewModelScope.launch{
                                        requestLocationPermission(
                                            fusedLocationClient,
                                            locationPermissionLauncher,
                                            context,
                                            location
                                        )
                                    }


                                    // Check if location permission was granted


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

        item(5){
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                , horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                Text(text = "Available For Sale", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(4.dp,0.dp),color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                var checked by rememberSaveable {
                    mutableStateOf(available?:false)
                }
                Switch(checked = checked, onCheckedChange ={checked = it
                    available = checked
                },

                    colors= SwitchDefaults.colors(),
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
        item(7){
            when(result.value){
                is Resource.Error -> {
                    LaunchedEffect(key1 = Unit){
                        Toast.makeText(context,"Something Went Wrong,Try Again",Toast.LENGTH_SHORT).show()
                    }

                }
                is Resource.Loading -> {
                    Row(modifier = Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator(modifier = Modifier.widthIn(20.dp)

                        )
                    }

                }
                is Resource.Success -> {
                    LaunchedEffect(key1 = Unit){
                        Toast.makeText(context,"Product Added Successfully",Toast.LENGTH_SHORT).show()
                    }



                }
                is Resource.Unspecified -> {
                    Button(onClick = {
                        addProductViewModel.uploadImagesAndAddProduct(
                            Product(
                                productId = "",
                                name= name,
                                category = category,
                                price = price.toDouble(),
                                available = available,
                                images = emptyList(),
                                location = GeoPoint(0.0, 0.0),
                                ownerId = Firebase.auth.currentUser?.uid.toString(),
                                date="",
                                quantity = quantity.toDouble()
                            ),images,location.value
                        )

                    }, modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                        Text(text = "Add Product")

                    }
                }
            }

        }
    }
//    Column(modifier = Modifier.padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center) {
//
//        OutlinedTextField(
//            value = name,
//            onValueChange = { name = it },
//            label = { Text("Name") }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = category,
//            onValueChange = { category = it },
//            label = { Text("Category") }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
////
////        TextField(
////            value = price.toString(),
////            onValueChange = { price = it },
////            label = { Text("Price") }
////        )
//        OutlinedTextField(
//            value = price,
//            onValueChange = {
//                if (it.isEmpty() || it.matches( Regex("^\\d+\$"))) {
//                    price = it
//                }
//            },
//            label = { Text("Price") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        OutlinedTextField(
//            value = quantity,
//            onValueChange = {
//                if (it.isEmpty() || it.matches( Regex("^\\d+\$"))) {
//                    quantity = it
//                }
//            },
//            label = { Text("Quantity in Kg") },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Row {
//            Checkbox(
//                checked = available,
//                onCheckedChange = { available = it },
//                modifier = Modifier.padding(end = 8.dp)
//            )
//            Text("Available")
//        }
//        Button(
//            onClick = {
//                requestLocationPermission(fusedLocationClient, locationPermissionLauncher, context,location)
//            }
//        )
//        {
//            Text("Get Current Location")
//        }
//        ImageSelectionButton(storagePermissionLauncher ) { selectedImages ->
//            images = selectedImages // Pass URIs directly
//        }
//        LazyRow(modifier = Modifier
//            .fillMaxWidth()
//            .height(100.dp)) {
//            items(images.size) { index ->
//                val imageUri = images[index]
//                Log.d("image uri -> ",imageUri.toString())
//                Image(
//                    painter = rememberAsyncImagePainter(imageUri),
//                    contentDescription = null,
//                    modifier = Modifier
//                        .width(100.dp)
//                        .padding(10.dp)
//                )
//            }
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(text = location.value?.longitude.toString())
//        Text(text = location.value?.latitude.toString())
//        Button(
//            onClick = {
//                addProductViewModel.uploadImagesAndAddProduct(
//                    Product(
//                        productId = "",
//                        name= name,
//                        category = category,
//                        price = price.toDouble(),
//                        available = available,
//                        images = emptyList(),
//                        location = GeoPoint(0.0, 0.0),
//                        ownerId = Firebase.auth.currentUser?.uid.toString(),
//                        date="",
//                        quantity = quantity.toDouble()
//                    ),images,location.value
//                )
//            }
//        )
//        {
//            Text("Submit")
//        }
//        when(result.value){
//            is Resource.Error ->{
//                Toast.makeText(context, result.value.message.toString(), Toast.LENGTH_SHORT)
//                    .show()
//            }
//
//            is Resource.Loading -> {
//                CircularProgressIndicator(Modifier.size(20.dp))
//            }
//            is Resource.Success -> {
//                Toast.makeText(context, "Product Added Successfully", Toast.LENGTH_SHORT)
//                    .show()
//            }
//            is Resource.Unspecified -> {
//
//            }
//        }
//    }



}
@Composable
fun ImageSelectionButton(storagePermissionLauncher: ActivityResultLauncher<String>,modifier: Modifier = Modifier, onImagesSelected: (List<Uri>) -> Unit) {
    val context = LocalContext.current
    val imagesUriState = remember { mutableStateOf<List<Uri>>(emptyList()) }

    val getContent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            imagesUriState.value = uris
            onImagesSelected(uris)
        }
    )

//    Button(
//        onClick = {
//            if (hasStoragePermission(context)) {
//                getContent.launch("image/*")
//            } else {
//                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
//            }
//        },
//        modifier = Modifier.padding(vertical = 8.dp),
//        colors = ButtonDefaults.buttonColors(
//            containerColor = MaterialTheme.colors.secondary,
//            contentColor = MaterialTheme.colors.onSecondary
//        ),
//        shape = RoundedCornerShape(8.dp)
//    ) {
//        Text("Add Images")
//    }
    Button(
        onClick = {
            if (hasStoragePermission(context)) {
                getContent.launch("image/*")
            } else {
                storagePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(28.dp),
//        elevation = ButtonDefaults.Elevation(
//            defaultElevation = 8.dp,
//            pressedElevation = 16.dp
//        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraEnhance,
                contentDescription = "Add Images",
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Add Images", style = MaterialTheme.typography.titleMedium)
        }
    }

}
fun hasStoragePermission(context: Context): Boolean {

    if(context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED){
        return  true
    }
    return false
}
