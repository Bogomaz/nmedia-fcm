package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository {
    fun getAllAsync(callback: GetAllCallback)
    interface GetAllCallback {
        fun onSuccess(posts: List<Post>)
        fun onError(e: Throwable)
    }

    fun saveAsync(post: Post, callback: SaveCallback)
    interface SaveCallback {
        fun onSuccess(post: Post)
        fun onError(e: Throwable)
    }

    fun removeByIdAsync(id: Long, callBack: RemoveCallback)
    interface RemoveCallback {
        fun onSuccess(id: Long)
        fun onError(e: Exception)
    }

    fun likeByIdAsync(id: Long, callback: LikeCallback)
    interface LikeCallback{
        fun onSuccess(id: Long)
        fun onError(e:Exception)
    }

    fun unlikeByIdAsync(id: Long, callback: UnlikeCallback)
    interface UnlikeCallback{
        fun onSuccess(id: Long)
        fun onError(e: Exception)
    }

    fun repostAsync(parentId: Long, text: String, callback: SaveCallback)
}