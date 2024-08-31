package com.example.farmi.screens.seller

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.farmi.data.Product
import com.example.farmi.ui.theme.LightGreen
import com.example.farmi.ui.theme.LightRed
import com.example.farmi.viewmodels.common.SharedViewModel
import com.example.farmi.viewmodels.seller.SellerHomeScreenViewModel

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    sharedViewModel: SharedViewModel = hiltViewModel(),
    navController: NavController,
    sellerHomeScreenViewModel: SellerHomeScreenViewModel = hiltViewModel()
) {
    val allProducts = sellerHomeScreenViewModel.allProducts.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()
    Log.d("Seller Home Screen",allProducts.value.data.toString())


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
//                .verticalScroll(scrollState)
        ) {
            Text(
                text = "All Products",
                    style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

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
                                navController.navigate("Edit_Product")
//                                navController.navigate("Product_Detail")
                            }
                        )
                    }
                }
            }
        }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductItem(product: Product, editProduct: () -> Unit) {
    var available = true
    if (product.available != true) {
        available = false
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = editProduct
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(product.images?.get(0))
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = product.name ?: "",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Category: ${product.category}",
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price: Rs. ${product.price.toString()}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = if (available) LightGreen else LightRed,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}