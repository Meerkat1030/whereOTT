package com.example.whereott.common

import com.example.whereott.common.TV
import com.google.gson.annotations.SerializedName

data class GetTVResponse (
    @SerializedName("page") val page: Int,
    @SerializedName("results") val tvlist: List<TV>,
    @SerializedName("totla_pages") val pages: Int,
    @SerializedName("totla_results") val results: Int
) {}