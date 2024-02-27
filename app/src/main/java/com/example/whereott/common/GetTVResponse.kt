package com.example.whereott.common

import com.example.whereott.common.TV
import com.google.gson.annotations.SerializedName

data class GetTVResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val tvlist: List<TV>, // results 필드의 타입을 List<TV>로 수정
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)