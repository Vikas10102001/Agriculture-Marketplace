package com.example.farmi.MandiPrices.repo

import com.example.farmi.MandiPrices.data.api.MandiApi
import com.example.farmi.MandiPrices.data.response.MandiApiResponse
import com.example.farmi.MandiPrices.data.response.MarketPriceRequest
import javax.inject.Inject

class MarketPriceRepo @Inject constructor(
    private val api:MandiApi) {
    suspend fun fetchMarketPrices(request: MarketPriceRequest): MandiApiResponse {
        val filteredFilters = mutableMapOf<String, String?>()
        for ((key, value) in request.filters) {
            if (value != null && value.isNotBlank()) {
                filteredFilters[key] = value
            }
        }
        return api.getMarketPrice(
            offset = request.offset,
            limit = request.limit,
            filters = filteredFilters
        )
    }
}