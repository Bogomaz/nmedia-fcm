package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.dto.Post

interface PostRepository  {
    fun getAllAsync(callback: GetAllCallback)

    interface GetAllCallback{
        fun onSuccess(posts: List<Post>)
        fun onError(e: Exception)
    }
//    fun getAll(): List<Post>
    fun save(post:Post): Post

    fun removeById(id: Long)

    fun removeById(callBack: GetAllCallback)

    fun likeById(id: Long): Post
    fun unlikeById(id: Long): Post

    fun repost(parentId: Long, text: String): Post

}