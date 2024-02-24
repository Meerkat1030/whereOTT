package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class GetMovieProvidersResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("results") val results: Map<String, MovieProviders>
)