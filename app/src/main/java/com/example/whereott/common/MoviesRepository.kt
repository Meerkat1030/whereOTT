package com.example.whereott.common

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MoviesRepository {
    private val api: Api //인터페이스 구현

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    fun getPopularMovies(page: Int = 1,
                         onSuccess: (movies: List<Movie>) -> Unit,
                         onError: () -> Unit ) {
        api.getPopularMovies(page = page)
            .enqueue(object : Callback<GetMoviesResponse> {
                override fun onResponse(
                    call: Call<GetMoviesResponse>,
                    response: Response<GetMoviesResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.movies)
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<GetMoviesResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getMovieCast(movieId: Long, onSuccess: (cast: List<Cast>) -> Unit, onError: () -> Unit) {
        api.getMovieCredits(movieId = movieId)
            .enqueue(object : Callback<GetMovieCreditsResponse> {
                override fun onResponse(
                    call: Call<GetMovieCreditsResponse>,
                    response: Response<GetMovieCreditsResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            onSuccess.invoke(it.cast)
                        } ?: onError.invoke()
                    } else {
                        onError.invoke()
                    }

                    Log.d("jkshin","Fail to getMovieCast !!")
                }

                override fun onFailure(call: Call<GetMovieCreditsResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getMovieProviders(movieId: Long, onSuccess: (providers: List<Provider>) -> Unit, onError: () -> Unit) {
        api.getMovieProviders(movieId = movieId)
            .enqueue(object : Callback<GetMovieProvidersResponse> {
                override fun onResponse(
                    call: Call<GetMovieProvidersResponse>,
                    response: Response<GetMovieProvidersResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            // KR 정보만 사용
//                            val krProviders = it.results["KR"]
//                            krProviders?.let { kr ->
//                                val providers = listOfNotNull(kr.flatrate, kr.rent, kr.buy).flatten()

                            val krProviders = it.results["KR"]
                            krProviders?.let { kr ->
                                val providers = mutableListOf<Provider>()

                                kr.flatrate?.let { flatrate ->
                                    for (provider in flatrate) {
                                        provider.type = "Stream"
                                    }
                                    providers.addAll(flatrate)
                                }

                                kr.rent?.let { rent ->
                                    for (provider in rent) {
                                        provider.type = "Rent"
                                    }
                                    providers.addAll(rent)
                                }

                                kr.buy?.let { buy ->
                                    for (provider in buy) {
                                        provider.type = "Buy"
                                    }
                                    providers.addAll(buy)
                                }
                                onSuccess.invoke(providers)
                            } ?: onError.invoke()
                        } ?: onError.invoke()
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<GetMovieProvidersResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }


}

