package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class GetMovieCreditsResponse(
    @SerializedName("cast") val cast: List<Cast>
)