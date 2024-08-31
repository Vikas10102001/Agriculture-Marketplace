package com.example.farmi.MandiPrices.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.MandiPrices.data.response.MandiApiResponse
import com.example.farmi.MandiPrices.data.response.MarketPriceRequest
import com.example.farmi.MandiPrices.data.response.Record
import com.example.farmi.MandiPrices.repo.MarketPriceRepo
import com.example.farmi.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
//val records = listOf<Record>()
val records = listOf(

    // Punjab
    Record("Punjab", "Amritsar", "Amritsar Market", "Wheat", "HD 2967", "A", "2024-05-01", 18.0, 30.0, 24.0),
    Record("Punjab", "Ludhiana", "Ludhiana Market", "Rice", "Basmati", "A", "2024-05-02", 50.0, 75.0, 62.0),
    Record("Punjab", "Jalandhar", "Jalandhar Market", "Maize", "Yellow", "B", "2024-05-03", 15.0, 25.0, 20.0),
    Record("Punjab", "Patiala", "Patiala Market", "Barley", "Local", "C", "2024-05-04", 12.0, 20.0, 16.0),
    Record("Punjab", "Bathinda", "Bathinda Market", "Cotton", "MCU-5", "A", "2024-05-05", 100.0, 150.0, 125.0),
    Record("Punjab", "Ferozepur", "Ferozepur Market", "Potato", "Kufri", "B", "2024-05-06", 10.0, 18.0, 14.0),
    Record("Punjab", "Gurdaspur", "Gurdaspur Market", "Tomato", "Hybrid", "A", "2024-05-07", 8.0, 15.0, 12.0),
    Record("Punjab", "Hoshiarpur", "Hoshiarpur Market", "Onion", "Red", "A", "2024-05-08", 12.0, 22.0, 17.0),
    Record("Punjab", "Kapurthala", "Kapurthala Market", "Sugarcane", "CoJ 64", "A", "2024-05-09", 20.0, 30.0, 25.0),
    Record("Punjab", "Moga", "Moga Market", "Peas", "Green", "A", "2024-05-10", 40.0, 60.0, 50.0),

    // Rajasthan
    Record("Rajasthan", "Jaipur", "Jaipur Market", "Wheat", "Sharbati", "A", "2024-05-01", 18.0, 30.0, 24.0),
    Record("Rajasthan", "Jodhpur", "Jodhpur Market", "Bajra", "Local", "B", "2024-05-02", 15.0, 25.0, 20.0),
    Record("Rajasthan", "Kota", "Kota Market", "Soybean", "Black", "A", "2024-05-03", 20.0, 35.0, 28.0),
    Record("Rajasthan", "Udaipur", "Udaipur Market", "Maize", "Yellow", "B", "2024-05-04", 12.0, 22.0, 17.0),
    Record("Rajasthan", "Ajmer", "Ajmer Market", "Mustard", "Yellow", "A", "2024-05-05", 25.0, 40.0, 32.0),
    Record("Rajasthan", "Bikaner", "Bikaner Market", "Groundnut", "Bold", "A", "2024-05-06", 30.0, 50.0, 40.0),
    Record("Rajasthan", "Alwar", "Alwar Market", "Guar", "Local", "B", "2024-05-07", 35.0, 55.0, 45.0),
    Record("Rajasthan", "Bharatpur", "Bharatpur Market", "Wheat", "Durum", "A", "2024-05-08", 22.0, 35.0, 28.0),
    Record("Rajasthan", "Bhilwara", "Bhilwara Market", "Barley", "Local", "C", "2024-05-09", 10.0, 18.0, 14.0),
    Record("Rajasthan", "Pali", "Pali Market", "Cumin", "Local", "A", "2024-05-10", 100.0, 150.0, 125.0),

    // Haryana
    Record("Haryana", "Chandigarh", "Chandigarh Market", "Wheat", "HD 2967", "A", "2024-05-01", 18.0, 28.0, 23.0),
    Record("Haryana", "Faridabad", "Faridabad Market", "Rice", "Basmati", "A", "2024-05-02", 55.0, 80.0, 67.0),
    Record("Haryana", "Gurgaon", "Gurgaon Market", "Potato", "Kufri", "B", "2024-05-03", 11.0, 20.0, 15.0),
    Record("Haryana", "Hisar", "Hisar Market", "Barley", "Local", "C", "2024-05-04", 13.0, 22.0, 17.0),
    Record("Haryana", "Karnal", "Karnal Market", "Cotton", "MCU-5", "A", "2024-05-05", 110.0, 160.0, 135.0),
    Record("Haryana", "Panipat", "Panipat Market", "Tomato", "Hybrid", "A", "2024-05-06", 9.0, 17.0, 13.0),
    Record("Haryana", "Rohtak", "Rohtak Market", "Onion", "Red", "A", "2024-05-07", 14.0, 24.0, 19.0),
    Record("Haryana", "Ambala", "Ambala Market", "Sugarcane", "Co 86032", "A", "2024-05-08", 22.0, 32.0, 27.0),
    Record("Haryana", "Sirsa", "Sirsa Market", "Mustard", "Yellow", "A", "2024-05-09", 28.0, 45.0, 36.0),
    Record("Haryana", "Yamunanagar", "Yamunanagar Market", "Peas", "Green", "A", "2024-05-10", 42.0, 65.0, 54.0),

    // New Delhi
    Record("New Delhi", "New Delhi", "Azadpur Market", "Onion", "Red", "A", "2024-05-01", 12.0, 22.0, 17.0),
    Record("New Delhi", "New Delhi", "Okhla Market", "Potato", "White", "B", "2024-05-02", 13.0, 23.0, 18.0),
    Record("New Delhi", "New Delhi", "Ghaziabad Market", "Tomato", "Local", "C", "2024-05-03", 9.0, 17.0, 13.0),
    Record("New Delhi", "New Delhi", "Shahdara Market", "Cabbage", "Green", "A", "2024-05-04", 8.0, 15.0, 11.0),
    Record("New Delhi", "New Delhi", "Narela Market", "Carrot", "Red", "B", "2024-05-05", 7.0, 14.0, 10.0),
    Record("New Delhi", "New Delhi", "Najafgarh Market", "Cauliflower", "White", "A", "2024-05-06", 10.0, 20.0, 15.0),
    Record("New Delhi", "New Delhi", "Mehrauli Market", "Spinach", "Green", "B", "2024-05-07", 6.0, 12.0, 9.0),
    Record("New Delhi", "New Delhi", "Dwarka Market", "Radish", "White", "A", "2024-05-08", 5.0, 10.0, 8.0),
    Record("New Delhi", "New Delhi", "Kirti Nagar Market", "Pumpkin", "Yellow", "B", "2024-05-09", 11.0, 18.0, 14.0),
    Record("New Delhi", "New Delhi", "Rohini Market", "Brinjal", "Purple", "A", "2024-05-10", 12.0, 20.0, 16.0),
    // Maharashtra
    Record("Maharashtra", "Pune", "Pune Market", "Onion", "Red", "A", "2024-05-01", 10.0, 20.0, 15.0),
    Record("Maharashtra", "Mumbai", "Mumbai Market", "Potato", "White", "B", "2024-05-02", 12.5, 22.0, 18.0),
    Record("Maharashtra", "Nagpur", "Nagpur Market", "Tomato", "Local", "C", "2024-05-03", 8.0, 16.0, 12.0),
    Record("Maharashtra", "Nashik", "Nashik Market", "Grapes", "Green", "A", "2024-05-04", 30.0, 50.0, 40.0),
    Record("Maharashtra", "Aurangabad", "Aurangabad Market", "Banana", "Cavendish", "B", "2024-05-05", 15.0, 25.0, 20.0),
    Record("Maharashtra", "Solapur", "Solapur Market", "Wheat", "Sharbati", "A", "2024-05-06", 20.0, 35.0, 27.0),
    Record("Maharashtra", "Amravati", "Amravati Market", "Soybean", "Black", "A", "2024-05-07", 25.0, 45.0, 35.0),
    Record("Maharashtra", "Kolhapur", "Kolhapur Market", "Sugarcane", "Co 86032", "B", "2024-05-08", 18.0, 28.0, 23.0),
    Record("Maharashtra", "Sangli", "Sangli Market", "Turmeric", "Rajapuri", "A", "2024-05-09", 60.0, 90.0, 75.0),
    Record("Maharashtra", "Latur", "Latur Market", "Groundnut", "Bold", "A", "2024-05-10", 40.0, 65.0, 52.0),

    )

@HiltViewModel
class MarketPriceViewModel @Inject constructor(
    private val marketPriceRepo: MarketPriceRepo,
    private  val firestore: FirebaseFirestore
):ViewModel() {

    private val _marketPrice = MutableStateFlow<Resource<List<Record>>>(Resource.Unspecified())
    val marketPrice = _marketPrice.asStateFlow()
    private val _states = MutableStateFlow<Resource<List<String>>>(Resource.Unspecified())
    val states = _states.asStateFlow()
    private val _districts = MutableStateFlow<Resource<List<String>>>(Resource.Unspecified())
    val districts = _districts.asStateFlow()
    private val _commodities = MutableStateFlow<Resource<List<String>>>(Resource.Unspecified())
    val commodities = _commodities.asStateFlow()
    init {
        fetchStates()
        fetchCommodites()
        fetchMarketPrice(marketPriceRequest = MarketPriceRequest(0,10,
            mapOf<String,String?>(
                "filters[state.keyword]" to "",
                "filters[commodity]" to "",
                "filters[market]" to "",
                "filters[commodity]" to "",


            )
        ))
    }
    fun fetchMarketPrice(marketPriceRequest: MarketPriceRequest){

        viewModelScope.launch {

            try {

                _marketPrice.emit(Resource.Loading())
                val result = marketPriceRepo.fetchMarketPrices(marketPriceRequest)
                Log.d("market price viewmodel",result.active?:"active is empty")
                _marketPrice.emit(Resource.Success(result.records?: emptyList()))
                Log.d("market price viewmodel",result.records.toString())
            }
            catch (e:Exception){
                Log.d("market price viewmodel",e.message?:"inside error")
                _marketPrice.emit(Resource.Error("Something Went Wrong,Try Again"))
            }

        }
    }
    fun fetchStates(){
        firestore.collection("states").get()
            .addOnSuccessListener { documents->

                val stateslist: MutableList<String> = mutableListOf()
                for (document in documents){
                    val stateName = document.id
                    Log.d("a",stateName)
                    stateslist.add(stateName)

                }
                _states.value = Resource.Success(stateslist)
            }
            .addOnFailureListener {
                _states.value = Resource.Error("Unable to Load States")
            }
    }
    fun fetchDistrict(state:String){
        firestore.collection("states").document(state).get()
            .addOnSuccessListener {document->
                if (document != null && document.exists()) {
                    // Get all fields of the document
                    val districtList: MutableList<String> = mutableListOf()
                    val fields = document.data

                    if (fields != null) {
                        // Iterate over each field
                        for ((fieldName, value) in fields) {
                            // Check if the value is an array
                            if (value is List<*>) {

                                Log.d("Array",fieldName.toString())
                                districtList.add(fieldName)

                                // Traverse over the array
//                                for (item in value as List<String>) {
//                                    println(item)
//                                }
                            }
                        }
                    }
                    _districts.value = Resource.Success(districtList)
                } else {
                    _districts.value = Resource.Error("Document does not exist")
//                    println("Document does not exist")
                }

            }

    }
    fun fetchCommodites(){
        firestore.collection("commodities").get()
            .addOnSuccessListener { documents->
                val commoditiesList: MutableList<String> = mutableListOf()
                for(document in documents){
                    val fields = document.data
                    for ((fieldName, value) in fields) {
                        // Check if the value is an array
                        if (value is List<*>) {




//                             Traverse over the array
                                for (item in value as List<String>) {
                                        commoditiesList.add(item)
                                }
                        }
                    }
                }
                _commodities.value = Resource.Success(commoditiesList)
            }
            .addOnFailureListener {
                _commodities.value = Resource.Error("Failed to Fetch Commodities")
            }
    }


}
