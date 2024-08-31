package com.example.farmi.MandiPrices.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.farmi.MandiPrices.data.response.MarketPriceRequest
import com.example.farmi.MandiPrices.data.response.Record
import com.example.farmi.MandiPrices.viewmodel.MarketPriceViewModel
import com.example.farmi.MandiPrices.viewmodel.records
import com.example.farmi.R
import com.example.farmi.ui.theme.LightGreen
import com.example.farmi.util.Resource


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketPriceScreen(
    marketPriceViewModel: MarketPriceViewModel = hiltViewModel()
) {
    val itemsList = marketPriceViewModel.marketPrice.collectAsState()
    val states = marketPriceViewModel.states.collectAsState()
    val districts = marketPriceViewModel.districts.collectAsState()
    val commodities = marketPriceViewModel.commodities.collectAsState()

    val selectedState = remember { mutableStateOf("") }
    val selectedDistrict = remember { mutableStateOf("") }
    val selectedCommodity = remember { mutableStateOf("") }
    val showFilters = remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        topBar = {
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
                                selectedState.value=""
                                selectedDistrict.value=""
                                selectedCommodity.value=""
                            }
                        ){
                            Icon(imageVector = Icons.Filled.Clear, contentDescription ="Filters clear", tint = MaterialTheme.colorScheme.error)
                        }
                        IconButton(
                            onClick = { marketPriceViewModel.fetchMarketPrice(
                                marketPriceRequest = MarketPriceRequest(0,10,
                                    mapOf<String,String?>(
                                        "filters[state.keyword]" to selectedState.value,
                                        "filters[district]" to selectedDistrict.value,
                                        "filters[commodity]" to selectedCommodity.value,
                                    )
                                )
                            )
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
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )

//                backgroundColor = MaterialTheme.colors.primary,
//                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) {innerPadding->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top=20.dp)
        ) {

            if(showFilters.value) {

                Spacer(modifier = Modifier.height(45.dp))
                states.value.data?.let { it1 ->
                    commodities.value.data?.let { it2 ->
                        FilterSection(
                            states = it1,
                            districts = districts.value,
                            commodities = it2,
                            selectedState = selectedState.value,
                            selectedDistrict = selectedDistrict.value,
                            selectedCommodity = selectedCommodity.value,
                            onStateChanged = {
                                selectedState.value = it
//                                marketPriceViewModel.fetchDistrict(it)
                            },
                            onDistrictChanged = { selectedDistrict.value = it }
                        ) { selectedCommodity.value = it }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (itemsList.value.data != null && itemsList.value.data!!.isNotEmpty()) {
                MarketPriceList(marketPrices = itemsList.value.data!!)
            } else {
                MarketPriceList(marketPrices = records)
//                Text(
//                    text = "No data available",
//                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = innerPadding.calculateTopPadding()),
//                    style = MaterialTheme.typography.headlineSmall,
//                    color = MaterialTheme.colorScheme.error
//
//                )
            }
        }
    }
}

@Composable
fun FilterSection(
    states: List<String>,
    districts: Resource<List<String>>,
    commodities: List<String>,
    selectedState: String,
    selectedDistrict: String,
    selectedCommodity: String,
    onStateChanged: (String) -> Unit,
    onDistrictChanged: (String) -> Unit,
    onCommodityChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                FilterDropdown(
                    items = states,
                    selectedItem = selectedState,
                    onItemSelected = onStateChanged,
                    label = "State",
                    modifier = Modifier.weight(1f)

                )
                FilterDropdown(
                    items = commodities,
                    selectedItem = selectedCommodity,
                    onItemSelected = onCommodityChanged,
                    label = "Commodity",
                    modifier = Modifier.weight(1f)
                )
            }



//            districts.data?.let {
//                FilterDropdown(
//                    items = it,
//                    selectedItem = selectedDistrict,
//                    onItemSelected = onDistrictChanged,
//                    label = "District",
//                    modifier = Modifier.fillMaxWidth()
//                )
//            }


        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun FilterDropdown(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown




        OutlinedButton(
            onClick = { expanded = !expanded },
            modifier = modifier
//                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (selectedItem.isNotEmpty()) selectedItem else label,
                modifier = Modifier
//                    .weight(1f)
                    .padding(horizontal = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(imageVector = icon, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

}

@Composable
fun MarketPriceList(marketPrices: List<Record>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(marketPrices.size) { index ->
            MarketPriceItem(marketPrice = marketPrices[index])
            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
//                color =  MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
            )
        }

    }
}

@Composable
fun MarketPriceItem(marketPrice: Record) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                marketPrice.commodity?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleMedium,
                        color= MaterialTheme.colorScheme.secondary
                    )
                }
            }



        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "${marketPrice.market},",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp),
                            color= MaterialTheme.colorScheme.tertiary
                        )
                        Text(
                            text = "${marketPrice.state}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(start = 4.dp),
                            color= MaterialTheme.colorScheme.tertiary
                        )
                    }

                }
            }

            Column(
                modifier = Modifier.align(Alignment.CenterVertically),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "Modal Price: ${
                        marketPrice.modal_price
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color= MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Min: ${marketPrice.min_price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color= MaterialTheme.colorScheme.tertiary
                )
                Text(
                    text = "Max: ${marketPrice.max_price}",
                    style = MaterialTheme.typography.bodyMedium,
                    color= MaterialTheme.colorScheme.tertiary

                )
            }
        }
     }
    }
}
