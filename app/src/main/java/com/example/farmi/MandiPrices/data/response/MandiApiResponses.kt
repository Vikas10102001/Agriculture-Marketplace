package com.example.farmi.MandiPrices.data.response

import com.google.gson.annotations.SerializedName

data class MandiApiResponse(
    val created: Long?,
    val updated: Long?,
    @SerializedName("created_date")
    val createdDate: String?,
    @SerializedName("updated_date")
    val updatedDate: String?,
    val active: String?,
    @SerializedName("index_name")
    val indexName: String?,
    val org: List<String>?,
    @SerializedName("org_type")
    val orgType: String?,
    val source: String?,
    val title: String?,
    @SerializedName("external_ws_url")
    val externalWsUrl: String?,
    val visualizable: String?,
    val field: List<Field>?,
    @SerializedName("external_ws")
    val externalWs: Int?,
    @SerializedName("catalog_uuid")
    val catalogUuid: String?,
    val sector: List<String>?,
    @SerializedName("target_bucket")
    val targetBucket: TargetBucket?,
    val desc: String?,
    @SerializedName("field_exposed")
    val fieldExposed: List<FieldExposed>?,
    val message: String?,
    val version: String?,
    val status: String?,
    val total: Int?,
    val count: Int?,
    val limit: String?,
    val offset: String?,
    val records: List<Record>?
)

data class Field(
    val name: String,
    val id: String,
    val type: String
)

data class TargetBucket(
    val field: String,
    val index: String,
    val type: String
)

data class FieldExposed(
    val name: String,
    val id: String,
    val type: String
)

data class Record(
    val state: String?,
    val district: String?,
    val market: String?,
    val commodity: String?,
    val variety: String?,
    val grade: String?,
    @SerializedName("arrival_date")
    val arrivalDate: String?,
    @SerializedName("min_price")
    val min_price: Double?,
    @SerializedName("max_price")
    val max_price: Double?,
    @SerializedName("modal_price")
    val modal_price: Double?
)
data class MarketPriceRequest(

    val offset: Int = 0,
    val limit: Int = 10,
    val filters: Map<String, String?>
)


