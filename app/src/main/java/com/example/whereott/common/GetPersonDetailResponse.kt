package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class GetPersonDetailResponse(
    @SerializedName("id") val id: Long,

) {}