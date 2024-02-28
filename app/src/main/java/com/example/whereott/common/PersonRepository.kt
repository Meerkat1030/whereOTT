package com.example.whereott.common

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PersonRepository {
    private val api: Api //인터페이스 구현

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(Api::class.java)
    }

    fun getPopularPerson(page: Int = 1,
                         onSuccess: (persons: List<Person>) -> Unit,
                         onError: () -> Unit ) {
        api.getPopularPerson(page = page)
            .enqueue(object : Callback<GetPersonResponse> {
                override fun onResponse(
                    call: Call<GetPersonResponse>,
                    response: Response<GetPersonResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            onSuccess.invoke(responseBody.persons)
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<GetPersonResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getPersonDetail(personId: Long, onSuccess: (personDetail: PersonDetail) -> Unit, onError: () -> Unit) {
        api.getPersonDetail(personId = personId)
            .enqueue(object : Callback<GetPersonDetailResponse> {
                override fun onResponse(
                    call: Call<GetPersonDetailResponse>,
                    response: Response<GetPersonDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null) {
                            val personDetail = PersonDetail(
                                id = responseBody.id,
                                name = responseBody.name,
                                biography = responseBody.biography?:"",
                                birthday = responseBody.birthday?:"",
                                deathday = responseBody.deathday?:"",
                                placeOfBirth = responseBody.placeOfBirth?:"",
                                profilePath = responseBody.profilePath?:""
                            )
                            onSuccess.invoke(personDetail) // PersonDetail 객체를 onSuccess 콜백에 전달
                        } else {
                            onError.invoke()
                        }
                    } else {
                        onError.invoke()
                    }
                }

                override fun onFailure(call: Call<GetPersonDetailResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }

    fun getPersonCombinedCredits(personId: Long, onSuccess: (cast: List<CombinedCast>) -> Unit, onError: () -> Unit) {
        api.getCombined_Credits(personId = personId)
            .enqueue(object : Callback<GetPersonCombinedCreditsResponse> {
                override fun onResponse(
                    call: Call<GetPersonCombinedCreditsResponse>,
                    response: Response<GetPersonCombinedCreditsResponse>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        responseBody?.let {
                            onSuccess.invoke(it.cast)
                        } ?: onError.invoke()
                    } else {
                        onError.invoke()
                        Log.d("jkshin","Fail to getMovieCast !!")
                    }
                }

                override fun onFailure(call: Call<GetPersonCombinedCreditsResponse>, t: Throwable) {
                    onError.invoke()
                }
            })
    }


}

