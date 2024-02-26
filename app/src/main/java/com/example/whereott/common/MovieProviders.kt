package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class MovieProviders(
    @SerializedName("link") val link: String?,
    @SerializedName("flatrate") val flatrate: List<Provider>?,
    @SerializedName("rent") val rent: List<Provider>?,
    @SerializedName("buy") val buy: List<Provider>?
)