package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class TV(
    @SerializedName("id") val id : Long,
    @SerializedName("name") val name : String,
    @SerializedName("overview") val overview : String,
    @SerializedName("poster_path") val poster_path: String,
    @SerializedName("backdrop_path") val backdrop_path: String,
    @SerializedName("vote_average") val rating: Float,
    @SerializedName("vote_count") val vcount: Long,
    @SerializedName("first_air_date") val first_air_date: String,
    @SerializedName("popularity") val prating: Float,

)
