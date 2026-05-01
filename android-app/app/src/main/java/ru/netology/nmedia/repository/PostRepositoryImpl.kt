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
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.viewmodel.emptyPost
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
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        val posts =
                            response.body?.string() ?: throw RuntimeException("body is null")
                        callback.onSuccess(gson.fromJson(posts, postType))
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.SaveCallback) {
        val request: Request = Request.Builder()
            .post(gson.toJson(post).toRequestBody(jsonType))
            .url(Api.POSTS_URL)
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    try {
                        response.use { res ->
                            val body = res.body?.string()
                                ?: throw RuntimeException("body is null")

                            if (!res.isSuccessful) {
                                throw RuntimeException("Error ${res.code}: $body")
                            }

                            val savedPost = gson.fromJson(body, Post::class.java)
                            callback.onSuccess(savedPost)
                        }
                    } catch (e: Exception) {
                        callback.onError(e)
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.RemoveCallback) {
        val request: Request = Request.Builder()
            .delete()
            .url(Api.postByIdUrl(id))
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            callback.onSuccess(id)
                        } else {
                            callback.onError(
                                java.lang.RuntimeException("Error ${it.code}: ${it.message}")
                            )
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
        val request: Request = Request.Builder()
            .post("".toRequestBody(jsonType))
            .url(Api.likesUrl(id))
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            callback.onSuccess(id)
                        } else {
                            callback.onError(
                                java.lang.RuntimeException("Error ${it.code}: ${it.message}")
                            )
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.UnlikeCallback) {
        val request: Request = Request.Builder()
            .delete("".toRequestBody(jsonType))
            .url(Api.likesUrl(id))
            .build()

        client.newCall(request)
            .enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (it.isSuccessful) {
                            callback.onSuccess(id)
                        } else {
                            callback.onError(
                                java.lang.RuntimeException("Error ${it.code}: ${it.message}")
                            )
                        }
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    callback.onError(e)
                }
            })
    }

    override fun repostAsync(
        parentId: Long,
        text: String,
        callback: PostRepository.SaveCallback
    ) {
        val repostPost = Post(
            id = 0,
            parentId = parentId,
            publishedDate = System.currentTimeMillis() / 1000,
            author = "Студент Нетологии",
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

        saveAsync(repostPost, object : PostRepository.SaveCallback {
            override fun onSuccess(post: Post) {
                // пробрасываем дальше наверх
                callback.onSuccess(post)
            }

            override fun onError(e: Exception) {
                callback.onError(e)
            }
        })
    }
}