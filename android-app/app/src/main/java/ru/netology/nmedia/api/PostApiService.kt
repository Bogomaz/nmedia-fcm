package ru.netology.nmedia.api

import retrofit2.Response
import retrofit2.http.*
import ru.netology.nmedia.model.NetworkPost

interface PostApiService {
    // Получить список всех постов
    @GET("api/posts")
    suspend fun getAll(): Response<List<NetworkPost>>

    // Поставить лайк
    @POST("api/posts/{id}/likes")
    suspend fun likedById(@Path("id") id: Long): Response<NetworkPost>

    // Снять лайк
    @DELETE("api/posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Response<NetworkPost>
}