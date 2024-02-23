package com.example.whereott.common

import com.example.whereott.common.GetMoviesResponse
import com.example.whereott.common.GetTVResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("movie/popular")
    fun getPopularMovies(
        @Query("api_key") apiKey: String = "beb95ad63fd33a8568136afbb01979a1",
        @Query("page") page : Int,
        @Query("language") language : String = "ko-KR,en-US"
    ): Call<GetMoviesResponse>

    @GET("tv/popular")
    fun getPopularTV(
        @Query("api_key") apiKey: String = "beb95ad63fd33a8568136afbb01979a1",
        @Query("page") page : Int,
        @Query("language") language : String = "ko-KR,en-US"
    ): Call<GetTVResponse>
}