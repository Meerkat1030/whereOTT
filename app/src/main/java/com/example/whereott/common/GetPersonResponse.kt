package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class GetPersonResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val persons: List<Person>,
    @SerializedName("totla_pages") val pages: Int,
    @SerializedName("totla_results") val results: Int
) {}