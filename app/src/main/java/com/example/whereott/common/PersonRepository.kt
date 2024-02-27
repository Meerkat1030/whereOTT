package com.example.whereott.common

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

//    fun getPersonDetail(personId:Long, onSuccess: (personDetail: List<PersonDetail>) -> Unit, onError: () -> Unit){
//        api.getPersonDetail(personId = personId)
//            .enqueue(object : Callback<GetPersonDetailResponse> {
//                override fun onResponse(
//                    call: Call<GetPersonDetailResponse>,
//                    response: Response<GetPersonDetailResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val responseBody = response.body()
//
//                        if (responseBody != null) {
//                            onSuccess.invoke(responseBody.personDetail)
//                        } else {
//                            onError.invoke()
//                        }
//                    } else {
//                        onError.invoke()
//                    }
//                }
//
//                override fun onFailure(call: Call<GetPersonDetailResponse>, t: Throwable) {
//                    onError.invoke()
//                }
//            })
//    }


}

