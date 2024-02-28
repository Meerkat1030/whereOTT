package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class PersonDetail(
    @SerializedName("id") val id: String?,
    @SerializedName("name") val name: String,
    @SerializedName("birthday") val birthday: String,
    @SerializedName("place_of_birth") val placeOfBirth: String?,
    @SerializedName("profile_path") val profilePath: String?
)