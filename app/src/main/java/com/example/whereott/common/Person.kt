package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class Person (
    @SerializedName("id") val id : Long,
    @SerializedName("name") val name : String,
    @SerializedName("profile_path") val profilePath: String,
    @SerializedName("birthday") val birthday: String?

)