package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class GetPersonCombinedCreditsResponse(
    @SerializedName("cast") val cast: List<CombinedCast>
)
