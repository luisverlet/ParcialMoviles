package com.example.parcialmoviles.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {


    // Emulador: private const val BASE_URL = "http://10.0.2.2:8080/api/"
    private const val BASE_URL = "https://todo-api-test.up.railway.app/api/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }



    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}