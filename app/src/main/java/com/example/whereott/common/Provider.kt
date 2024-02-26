package com.example.whereott.common

import com.google.gson.annotations.SerializedName

data class Provider(
    @SerializedName("logo_path") val logoPath: String?,
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("provider_name") val providerName: String,
    @SerializedName("display_priority") val displayPriority: Int,
    @SerializedName("type") var type: String?
)