package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class CombinedCast(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("character") val character: String,
    @SerializedName("poster_path") val poster_path: String
)