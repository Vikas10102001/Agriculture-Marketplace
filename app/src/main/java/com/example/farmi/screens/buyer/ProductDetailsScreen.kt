package com.example.farmi.screens.buyer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import android.widget.ToggleButton
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column


import androidx.compose.foundation.layout.PaddingValues

//import androidx.compose.foundation.layout.ColumnScopeInstance.align
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.outlined.CameraEnhance
import androidx.compose.material.icons.outlined.Filter9Plus
import androidx.compose.material.icons.outlined.Scale
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.TextButton

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.farmi.AddProduct.screen.ImageSelectionButton
import com.example.farmi.AddProduct.viewmodel.LatLng
import com.example.farmi.AddProduct.viewmodel.fetchLocation
import com.example.farmi.AddProduct.viewmodel.requestLocationPermission
import com.example.farmi.R
import com.example.farmi.activities.MainActivity
import com.example.farmi.util.Resource
import com.example.farmi.viewmodels.buyer.ProductDetailsViewModel
import com.example.farmi.viewmodels.common.SharedViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "QueryPermissionsNeeded")
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    sharedViewModel: SharedViewModel,
    productDetailsViewModel: ProductDetailsViewModel = hiltViewModel()

) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val screenWidth = configuration.screenWidthDp.dp
    val product by sharedViewModel.selectedProduct.collectAsState()
    val pagerState = rememberPagerState(initialPage = 0)
    val context = LocalContext.current
    val enquiryEnabled = mutableStateOf(true)
    val reachProductEnabled= mutableStateOf(true)
    val customerPagerState = rememberPagerState(initialPage = 0)
    val addCustomerImagesResult by productDetailsViewModel.addCustomerImages.collectAsState()
    val isSellerImages = rememberSaveable {
        mutableStateOf(true)
    }

    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    var location = remember {
        mutableStateOf<LatLng?>(null)
    }
    var images by rememberSaveable {
        mutableStateOf(listOf<Uri>())
    }
    val showSelectedImages = remember{ mutableStateOf(false) }
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
    val ownerPhoneNumber by productDetailsViewModel.ownerPhoneNumber.collectAsState()

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

    if (product != null) {
        LaunchedEffect(Unit){
            product!!.ownerId?.let { it1 ->
                productDetailsViewModel.getOwnerPhoneNumber(
                    it1
                )
            }
//            Log.d("product details screen-> launched effect",ownerPhoneNumber.data.toString())
        }
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Product Details",
                        style = MaterialTheme.typography.titleLarge,
                        color= MaterialTheme.colorScheme.primary)
                            },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint= MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight / 2)
                            .clip(RoundedCornerShape(16.dp))
                            .background(color = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(0.9f),
                                verticalArrangement = Arrangement.Center
                            ) {
//                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(20.dp)
                                        .background(
                                            color = MaterialTheme.colorScheme.surface,
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(horizontal = 8.dp)
                                    ,
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,

                                ) {

                                    TextButton(
                                        onClick = { isSellerImages.value = true },
                                        modifier = Modifier
                                            .width(65.dp)
                                            .height(20.dp)
//
                                        ,
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSellerImages.value) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.secondaryContainer
                                        ),
                                        shape = RoundedCornerShape(
                                            topStart = 10.dp,
                                            bottomStart = 10.dp
                                        )
//                                        shape = RoundedCornerShape(10.dp)

                                    ) {
                                        Text(
                                            text = "Seller",
                                            color = if (isSellerImages.value) MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onSurface,
//                                            style = MaterialTheme.typography.button,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,

                                        )
                                    }

                                    Divider(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(20.dp),
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )

                                    TextButton(
                                        onClick = { isSellerImages.value = false },
                                        modifier = Modifier
                                            .width(65.dp)
                                            .height(20.dp)
//                                            .height(40.dp)

                                        ,

                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSellerImages.value) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.secondary
                                        ),
                                        shape = RoundedCornerShape(
                                            topEnd = 10.dp,
                                            bottomEnd = 10.dp
                                        )
//                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "Customer",
                                            color = if (isSellerImages.value) MaterialTheme.colorScheme.onSurface else Color.White,
//                                            style = MaterialTheme.typography.button,
                                            fontSize = 12.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                if(isSellerImages.value){
                                    if(product!!.images.isNullOrEmpty()){
                                        Text(text = "No Images are provided by Seller")

                                    }
                                    else{

                                        HorizontalPager(
                                            count = product!!.images?.size ?: 0,
                                            state = pagerState,
                                            itemSpacing = 8.dp,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp)
                                        ) { page ->
                                            Image(
                                                painter = rememberAsyncImagePainter(product!!.images?.get(page) ?: ""),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(16.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                    }
                                }
                                else{
                                    if(product!!.customerImages.isNullOrEmpty()){
                                        Text(text = "No Images are provided by Customers")
                                    }
                                    else{
                                        HorizontalPager(
                                            count = product!!.customerImages?.size ?: 0,
                                            state = customerPagerState,
                                            itemSpacing = 8.dp,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp)
                                        ) { page ->
                                            Image(
                                                painter = rememberAsyncImagePainter(product!!.customerImages?.get(page) ?: ""),
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clip(RoundedCornerShape(16.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }

                                    }

                                }

                            }
                            Column(modifier = Modifier.fillMaxWidth()) {
                                HorizontalPagerIndicator(
                                    pagerState = if(isSellerImages.value) pagerState else customerPagerState,
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(8.dp),
                                    activeColor = MaterialTheme.colorScheme.primary,
                                    inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = product!!.name ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProductDetailItem(
                            icon = Icons.Default.Category,
                            label = "Category",
                            value = product!!.category ?: "",
                            modifier = Modifier.weight(1f)
                        )
                        ProductDetailItem(
                            icon = Icons.Default.CurrencyRupee,
                            label = "Price(Per Kg)",
                            value = "${product!!.price ?: 0.0}",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProductDetailItem(
                            icon = Icons.Default.CheckCircle,
                            label = "Availability",
                            value = if (product!!.available == true) "In Stock" else "Out of Stock",
                            modifier = Modifier.weight(1f)
                        )
                        ProductDetailItem(
                            icon = Icons.Outlined.Scale,
                            label = "Quantity(in Kg)",
                            value = "${product!!.quantity ?: 0.0}",
                            modifier = Modifier.weight(1f)
                        )

                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProductDetailItem(
                            icon = Icons.Default.LocationOn,
                            label = "Location",
                            value = "${product!!.location?.latitude ?: 0.0}, ${product!!.location?.longitude ?: 0.0}"
                        )



                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                    ){
                        ProductDetailItem(
                            icon = Icons.Default.DateRange,
                            label = "Date",
                            value = product!!.date ?: ""
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),

                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ButtonWithIcon(
                            onClick = {

                                var phoneNumber:String?=null
                                Log.d("product details screen",product!!.ownerId.toString())
//                                product!!.ownerId?.let { it1 ->
//                                    productDetailsViewModel.getOwnerPhoneNumber(
//                                        it1
//                                    )
//                                }
                                Log.d("product details screen",ownerPhoneNumber.data.toString())
                                if(ownerPhoneNumber is Resource.Success){
                                    phoneNumber = ownerPhoneNumber.data
                                }
                                val phoneIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:$${phoneNumber}")
                                }

                                try {
                                    context.startActivity(phoneIntent, phoneIntent.extras)
                                } catch (e: Exception) {
                                    enquiryEnabled.value = false
                                }
                            },
                            enabled = enquiryEnabled.value,
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.Call,
                                    contentDescription = "Enquire",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            },
                            text = "Enquire",
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )

                        ButtonWithIcon(
                            onClick = {
                                val gmmIntentUri = Uri.parse("geo:${product!!.location?.latitude ?: 0.0},${product!!.location?.longitude ?: 0.0}?q=${product!!.location?.latitude ?: 0.0},${product!!.location?.longitude ?: 0.0}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")

                                try {
                                    context.startActivity(mapIntent, mapIntent.extras)
                                } catch (e: Exception) {
                                    reachProductEnabled.value = false
                                }
                            },
                            enabled = reachProductEnabled.value,
                            icon = {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Reach Product",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            },
                            text = "Reach Product",
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )

                        when (addCustomerImagesResult) {
                            is Resource.Loading -> {
                                CircularProgressIndicator()
                            }
                            is Resource.Success -> {
                                if (addCustomerImagesResult.data == false) {
                                    LaunchedEffect(Unit) {
                                        Toast.makeText(context, "You are not within 1Km of Range", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    LaunchedEffect(Unit) {
                                        Toast.makeText(context, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    if (location.value == null) {
                                        ButtonWithIcon(
                                            onClick = {
                                                productDetailsViewModel.viewModelScope.launch {
                                                    requestLocationPermission(
                                                        fusedLocationClient,
                                                        locationPermissionLauncher,
                                                        context,
                                                        location
                                                    )
                                                }
                                            },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Outlined.CameraEnhance,
                                                    contentDescription = "Add Images",
                                                    tint = MaterialTheme.colorScheme.onPrimary
                                                )
                                            },
                                            text = "Add Images",
                                            containerColor = MaterialTheme.colorScheme.primary,
                                            enabled = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                    } else {
                                        ImageSelectionButton(storagePermissionLauncher,modifier = Modifier.weight(1f).height(48.dp)
                                            .padding(4.dp)) { selectedImages ->
                                            images = selectedImages
                                            showSelectedImages.value = true
                                        }
                                    }
                                }
                            }
                            else -> {
                                if (location.value == null) {
                                    ButtonWithIcon(
                                        onClick = {
                                            productDetailsViewModel.viewModelScope.launch {
                                                requestLocationPermission(
                                                    fusedLocationClient,
                                                    locationPermissionLauncher,
                                                    context,
                                                    location
                                                )
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = Icons.Outlined.CameraEnhance,
                                                contentDescription = "Add Images",
                                                tint = MaterialTheme.colorScheme.onPrimary
                                            )
                                        },
                                        text = "Add Images",
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        enabled = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                } else {
                                    ImageSelectionButton(storagePermissionLauncher, modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .padding(4.dp)) { selectedImages ->
                                        images = selectedImages
                                        showSelectedImages.value = true
                                    }
                                }
                            }
                        }
                    }
                }
                if (showSelectedImages.value) {
                    AlertDialog(
                        onDismissRequest = { showSelectedImages.value = false
                            images= emptyList()
                                           },
                        title = { Text(text = "Selected Images", style = MaterialTheme.typography.headlineSmall) },
                        text = {
//
                            LazyVerticalGrid(modifier = Modifier.fillMaxWidth(), columns = GridCells.Adaptive(minSize = 100.dp)) {
//
                                items(images.size) { imageUri ->
                                    Image(
                                        painter = rememberAsyncImagePainter(images[imageUri]),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(100.dp)
                                            .clip(RoundedCornerShape(40.dp))
                                            .padding(8.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            Button(
                                onClick = {

                                    showSelectedImages.value = false
                                    if (location.value == null) {
                                                Log.d("product details screen","location value is null")
                                    } else {


                                        productDetailsViewModel.addCustomerImages(
                                            location.value!!.latitude,
                                            location.value!!.longitude,
                                            product!!,
                                            images
                                        )
                                    }
                                    images = emptyList()
                                          },
                            ) {
                                Text("Submit")
                            }
                        }
                    )
                }

            }
        }
    } else {
        Text(text = "Unable to Fetch Product")
    }
}
@Composable
fun ProductDetailItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier

) {
    Row(
        modifier = modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
@Composable
fun ButtonWithIcon(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: @Composable () -> Unit,
    text: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor.copy(alpha = 0.3f),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {

                icon()



            Spacer(modifier = Modifier.width(8.dp))
//            Text(text = text, style = MaterialTheme.typography.caption)

        }
    }
}


