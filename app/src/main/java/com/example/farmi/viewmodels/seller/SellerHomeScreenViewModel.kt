package com.example.farmi.viewmodels.seller

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmi.data.Product
import com.example.farmi.repositories.GetAllProductsRepo
import com.example.farmi.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SellerHomeScreenViewModel @Inject constructor(
    private val getAllProductsRepo: GetAllProductsRepo,
    private val db: FirebaseFirestore
) : ViewModel() {
    private val _allProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val allProducts: StateFlow<Resource<List<Product>>> = _allProducts

    val name = FirebaseAuth.getInstance().currentUser?.displayName

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            getAllProductsRepo.getProductsForCurrentUser()
                .catch { exception ->
                    _allProducts.value = Resource.Error(exception.message.toString())
                }
                .collect { resource ->

                    _allProducts.value = resource
                }
        }
    }
    fun postMarketDataToFirestore() {
        // Initialize Firebase


        // Your market data

        val marketData = mapOf(
            "Andhra Pradesh" to mapOf(
                "Anantapur" to listOf("Anantapur Market", "Kalyandurg Market")
            ),
            "Telangana" to mapOf(
                "Hyderabad" to listOf("Begum Bazar", "Koti Market"),
                "Warangal" to listOf("Warangal Market", "Hanamkonda Market"),
                "Karimnagar" to listOf("Huzzurabad", "Manakodur")
            ),
            "Bihar" to mapOf(
                "Madhubani" to listOf("Jainagar"),
                "Rohtas" to listOf("Kochas (Balthari)"),
                "Araria" to listOf("Forbesganj")
            ),
            "Chandigarh" to mapOf(
                "Chandigarh" to listOf("Chandigarh(Grain/Fruit)")
            ),
            "Chattisgarh" to mapOf(
                "Bijapur" to listOf("Bijapur"),
                "Bilaspur" to listOf("Sakri")
            ),
            "Gujarat" to mapOf(
                "Amreli" to listOf("Bagasara"),
                "Anand" to listOf("Anand(Veg,Yard,Anand)"),
                "Dahod" to listOf("Devgadhbaria", "Davgadbaria(Piplod)"),
                "Narmada" to listOf("Rajpipla"),
                "Surendranagar" to listOf("Vadhvan"),
                "Vadodara(Baroda)" to listOf("Bodeliu"),
                "Devbhumi Dwarka" to listOf("Bhanvad")
            ),
            "Haryana" to mapOf(
                "Ambala" to listOf("Barara", "Mullana", "Mullana(saha)"),
                "Gurgaon" to listOf("Gurgaon"),
                "Mewat" to listOf("FerozpurZirkha(Nagina)")
            ),
            "Jammu and Kashmir" to mapOf(
                "Rajouri" to listOf("Rajouri (F&V)"),
                "Udhampur" to listOf("Udhampur")
            ),
            "Kerala" to mapOf(
                "Alappuzha" to listOf("Cherthala", "Harippad"),
                "Ernakulam" to listOf("Mazhuvannur"),
                "Kannur" to listOf("Kannur"),
                "Kollam" to listOf("Anchal"),
                "Kozhikode(Calicut)" to listOf("Perambra"),
                "Thiruvananthapuram" to listOf("Parassala", "Pothencode")
            ),
            "Maharashtra" to mapOf(
                "Satara" to listOf("Vai")
            ),
            "Nagaland" to mapOf(
                "Kohima" to listOf("Jalukie", "Tsemenyu"),
                "Dimapur" to listOf("Nuiland")
            ),
            "Odisha" to mapOf(
                "Balasore" to listOf("Jaleswar"),
                "Dhenkanal" to listOf("Hindol"),
                "Mayurbhanja" to listOf("Karanjia"),
                "Sundergarh" to listOf("Panposh")
            ),
            "Punjab" to mapOf(
                "Amritsar" to listOf("Rayya"),
                "Hoshiarpur" to listOf("Garh Shankar", "GarhShankar (Kotfatuhi)"),
                "Jalandhar" to listOf("Shahkot"),
                "kapurthala" to listOf("Bhulath")
            ),
            "Rajasthan" to mapOf(
                "Jaipur" to listOf("Bassi")
            ),
            "Tamil Nadu" to mapOf(
                "Coimbatore" to listOf("Madathukulam"),
                "Erode" to listOf("Alangeyam")
            ),
            "Tripura" to mapOf(
                "Dhalai" to listOf("Kulai"),
                "Gomati" to listOf("Silachhari"),
                "North Tripura" to listOf("Kanchanpur", "Dasda"),
                "South District" to listOf("Barpathari")
            ),
            "Uttar Pradesh" to mapOf(
                "Aligarh" to listOf("Charra", "Visoli"),
                "Badaun" to listOf("Visoli"),
                "Bareilly" to listOf("Rampur"),
                "Basti" to listOf("Harraiya"),
                "Etah" to listOf("Patiyali"),
                "Firozabad" to listOf("Shikohabad")
            ),
            "West Bengal" to mapOf(
                "Darjeeling" to listOf("Kalimpong"),
                "Howrah" to listOf("Bagnan"),
                "Jalpaiguri" to listOf("Maynaguri"),
                "Murshidabad" to listOf("Bhagawangola")
            )
        )

        // Loop through the market data and add it to Firestore
        marketData.forEach { (state, districts) ->
            val stateRef = db.collection("states").document(state)
            val stateData = hashMapOf<String, Any>()
            districts.forEach { (district, markets) ->
                stateData[district] = markets
            }
            stateRef.set(stateData, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d("home screen", "DocumentSnapshot successfully written!")
                }
                .addOnFailureListener { e ->
                    Log.w("Home Screen", "Error writing document", e)
                }
        }
    }
    fun postCommodityData(){
        val commodities = setOf(
            "Onion",
            "Apple",
            "Bottle gourd",
            "Lemon",
            "Mahua",
            "Paddy(Dhan)(Common)",
            "Bengal Gram(Gram)(Whole)",
            "Groundnut",
            "Sesamum(Sesame,Gingelly,Til)",
            "Brinjal",
            "Spinach",
            "Arhar (Tur/Red Gram)(Whole)",
            "Banana",
            "Carrot",
            "Garlic",
            "Maize",
            "Bhindi(Ladies Finger)",
            "Water Melon",
            "Tomato",
            "Cabbage",
            "Cucumbar(Kheera)",
            "Knool Khol",
            "Round gourd",
            "Raddish",
            "Amaranthus",
            "Little gourd (Kundru)",
            "Bitter gourd",
            "Colacasia",
            "Elephant Yam (Suran)",
            "Amphophalus",
            "Drumstick",
            "Capsicum",
            "Cluster beans",
            "Ginger(Green)",
            "Pineapple",
            "Pumpkin",
            "Potato",
            "Ashgourd",
            "Chow Chow",
            "Banana - Green",
            "Beans",
            "Pointed gourd (Parval)",
            "Apple",
            "Field Pea",
            "Green Chilli",
            "Pomegranate",
            "Coconut",
            "Masur Dal",
            "Sweet Pumpkin",
            "Bajra(Pearl Millet/Cumbu)",
            "Mustard",
            "Wheat",
            "Lentil (Masur)(Whole)",
            "Peas(Dry)",
            "Green Gram Dal (Moong Dal)",
            "White Peas",
            "Corriander seed",
            "Ridgeguard(Tori)",
            "Mango (Raw-Ripe)",
            "Onion Green",
            "Cotton",
            "Long Melon(Kakri)",
            "Karbuja(Musk Melon)",
            "Raddish",
            "Papaya",
            "Beetroot",
            "French Beans (Frasbean)",
            "Galgal(Lemon)",
            "Snakeguard",
            "Cowpea(Veg)",
            "Arecanut(Betelnut/Supari)",
            "Water Melon",
            "Taramira",
            "Indian Beans (Seam)",
            "Fish",
            "Tinda",
            "Black pepper",
            "Leafy Vegetable",
            "Ginger(Dry)",
            "Papaya (Raw)",
            "Coconut Oil",
            "Barley (Jau)",
            "Cummin Seed(Jeera)",
            "Guar",
            "Mustard Oil",
            "Bottle gourd",
            "Gur(Jaggery)",
            "Mousambi(Sweet Lime)",
            "Tamarind Seed",
            "Soyabean"
        )
        // Create a map to represent the document
        val commodityData = hashMapOf(
            "commodities" to commodities.toList() // Convert set to list before storing
        )

// Add a new document with a generated ID
        db.collection("commodities")
            .add(commodityData)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }
}