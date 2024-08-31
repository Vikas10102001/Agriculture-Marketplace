package com.example.farmi.MandiPrices.data.api

import com.example.farmi.MandiPrices.data.response.MandiApiResponse

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.Url

interface MandiApi {
    @GET
    suspend fun getMarketPrice(
        @Url url:String ="https://api.data.gov.in/resource/<your resource key>",
        @Query("api-key") apiKey: String = "your api key",
        @Query("format") format: String = "json",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 10,
        @QueryMap filters: Map<String, String?>
    ): MandiApiResponse
}