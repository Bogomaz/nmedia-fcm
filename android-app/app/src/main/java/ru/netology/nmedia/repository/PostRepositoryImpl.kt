package ru.netology.nmedia.repository

import okhttp3.Call
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import retrofit2.Callback
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dto.Post
import java.io.IOException
import kotlin.collections.orEmpty

class PostRepositoryImpl : PostRepository {
    override fun getAllAsync(callback: PostRepository.GetAllCallback) {
        PostApi.service.getAll()
            .enqueue(object : Callback<List<Post>> {
                override fun onResponse(
                    call: retrofit2.Call<List<Post>?>,
                    response: retrofit2.Response<List<Post>?>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess(response.body().orEmpty())
                    } else {
                        callback.onError(
                            java.lang.RuntimeException(response.errorBody()?.string().orEmpty())
                        )
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<List<Post>?>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }
            })
    }

    override fun saveAsync(post: Post, callback: PostRepository.SaveCallback) {
        PostApi.service.savePost(post)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post?>,
                    response: retrofit2.Response<Post?>
                ) {
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            callback.onSuccess(body)
                        } else {
                            callback.onError(RuntimeException("Body is null"))
                        }
                    } else {
                        callback.onError(
                            RuntimeException(response.errorBody()?.string().orEmpty())
                        )
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<Post?>,
                    t: Throwable
                ) {
                    callback.onError(t)
                }
            })
    }

    override fun removeByIdAsync(id: Long, callback: PostRepository.RemoveCallback) {
        PostApi.service.removeById(id)
            .enqueue(object : Callback<Unit> {

                override fun onResponse(
                    call: retrofit2.Call<Unit?>,
                    response: retrofit2.Response<Unit?>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess(id)
                    } else {
                        callback.onError(
                            RuntimeException(response.errorBody()?.string().orEmpty())
                        )
                    }
                }

                override fun onFailure(call: retrofit2.Call<Unit?>, t: Throwable) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun likeByIdAsync(id: Long, callback: PostRepository.LikeCallback) {
        PostApi.service.likeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post?>,
                    response: retrofit2.Response<Post?>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess(id)
                    } else {
                        callback.onError(
                            RuntimeException(response.errorBody()?.string().orEmpty())
                        )
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<Post?>,
                    t: Throwable
                ) {
                    callback.onError(Exception(t))
                }
            })
    }

    override fun unlikeByIdAsync(id: Long, callback: PostRepository.UnlikeCallback) {
        PostApi.service.unlikeById(id)
            .enqueue(object : Callback<Post> {
                override fun onResponse(
                    call: retrofit2.Call<Post?>,
                    response: retrofit2.Response<Post?>
                ) {
                    if (response.isSuccessful) {
                        callback.onSuccess(id)
                    } else {
                        callback.onError(
                            RuntimeException(response.errorBody()?.string().orEmpty())
                        )
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<Post?>,
                    t: Throwable
                ) {
                    callback.onError(Exception(t))
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
                callback.onSuccess(post)
            }

            override fun onError(e: Throwable) {
                callback.onError(e)
            }
        })
    }
}
