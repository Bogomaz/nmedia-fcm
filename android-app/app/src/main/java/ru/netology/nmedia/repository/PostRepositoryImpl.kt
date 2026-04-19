package ru.netology.nmedia.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.netology.nmedia.dto.Post
import java.io.IOException
import java.util.concurrent.TimeUnit

class PostRepositoryImpl : PostRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()
    private val postType = object : TypeToken<List<Post>>() {}.type

    private companion object {
        private const val BASE_URL = "http://10.0.2.2:9999"
        private val jsonType = "application/json".toMediaType()
    }

    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        val request: Request = Request.Builder()
            .url("${BASE_URL}/api/posts")
            .build()

        client.newCall(request)
            .enqueue(object : Callback{
                override fun onResponse(call: Call, response: Response){
                    try{
                        val posts = response.body?.string()?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(posts, postType))
                    }catch(e: Exception){
                        callback.onError(e)
                    }
                }
                override fun onFailure(call: Call, e: IOException){
                    callback.onError(e)
                }
            })
    }

    override fun save(post: Post): Post {
        val json = gson.toJson(post)
        println("SAVE REQUEST JSON: $json")

        val request: Request = Request.Builder()
            .post(json.toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts")
            .build()
        val call = client.newCall(request)
        val response = call.execute()
        val jsonResponse = response.body?.string()

        return gson.fromJson(jsonResponse, Post::class.java)
    }

    override fun removeById(id: Long) {
        val request: Request = Request.Builder()
            .delete()
            .url("${BASE_URL}/api/posts/$id")
            .build()

        client.newCall(request)
            .execute()
            .close()
    }


    override fun likeById(id: Long): Post {
        println(">>> CLIENT: LIKE $id")
        val request: Request = Request.Builder()
            .post("".toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
                ?: throw RuntimeException("body is null")

            if (!response.isSuccessful) {
                throw RuntimeException("Error ${response.code}: $body")
            }
            println("LIKE RESPONSE JSON: $body")
            return gson.fromJson(body, Post::class.java)
        }
    }

    override fun unlikeById(id: Long): Post {
        println(">>> CLIENT: LIKE $id")
        val request: Request = Request.Builder()
            .delete("".toRequestBody(jsonType))
            .url("${BASE_URL}/api/posts/$id/likes")
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string()
                ?: throw RuntimeException("body is null")

            if (!response.isSuccessful) {
                throw RuntimeException("Error ${response.code}: $body")
            }

            return gson.fromJson(body, Post::class.java)
        }
    }

    override fun repost(parentId: Long, text: String): Post {
        val repostPost = Post(
            id = 0,
            parentId = parentId,            // Ссылка на родителя
            publishedDate = System.currentTimeMillis() / 1000,
            author = "Студент Нетологии",   // Надо вообразить, что это значение берётся из данных текущего пользователя
            text = text,
            videoLink = "",
            videoDescription = "",
            videoDate = "",
            commentsCount = 0,
            likesCount = 0,
            isLiked = false,
            viewsCount = 0,
            repostsCount = 0,
        )

        return save(repostPost)
    }

}