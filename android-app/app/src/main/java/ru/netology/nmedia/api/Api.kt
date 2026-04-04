package ru.netology.nmedia.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    private const val LOCAL_BASE_URL = "http://10.0.2.2:9999/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(LOCAL_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: PostApiService = retrofit.create(PostApiService::class.java)
}