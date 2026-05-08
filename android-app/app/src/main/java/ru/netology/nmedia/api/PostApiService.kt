package ru.netology.nmedia.api

import com.google.firebase.datatransport.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.dto.Comment
import ru.netology.nmedia.dto.Post
import java.util.concurrent.TimeUnit

private const val BASE_URL = "http://10.0.2.2:9999"

// Утилиты для URL картинок
fun avatarUrl(name: String) = "${BASE_URL}/avatars/$name"
fun imageUrl(name: String) = "${BASE_URL}/images/$name"

private val client = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    })
    .connectTimeout(30, TimeUnit.SECONDS)
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("$BASE_URL/")
    .addConverterFactory(GsonConverterFactory.create())
    .client(client)
    .build()

private val logging = HttpLoggingInterceptor().apply {
    if (BuildConfig.DEBUG) {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

interface PostApiService {
    @GET("api/posts")
    suspend fun getAll(): List<Post>

    @GET("api/posts/{id}/newer")
    suspend fun getNewer(@Path("id") id: Long): List<Post>

    @POST("api/posts")
    suspend fun savePost(@Body post: Post): Post

    @DELETE("api/posts/{id}")
    suspend fun removeById(@Path("id") id: Long)

    @POST("api/posts/{id}/likes")
    suspend fun likeById(@Path("id") id: Long): Post

    @DELETE("api/posts/{id}/likes")
    suspend fun unlikeById(@Path("id") id: Long): Post

    @GET("api/authors/{id}")
    suspend fun getAuthorById(@Path("id") id: Long): Author

    @GET("api/posts/{postId}/comments")
    suspend fun getCommentsByPostId(@Path("postId") postId: Long): List<Comment>

}

object PostApi{
    val service by lazy{
        retrofit.create(PostApiService::class.java)
    }
}