package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class Person (
    @SerializedName("id") val id : Long,
    @SerializedName("name") val name : String,
    @SerializedName("profile_path") val profilePath: String,
    @SerializedName("known_for_department") val Department: String,
)