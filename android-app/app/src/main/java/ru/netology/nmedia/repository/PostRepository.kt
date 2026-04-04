package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.Post

interface PostRepository  {
    fun getAll(): LiveData<List<Post>>
    suspend fun refresh()
    suspend fun save(post:Post)

    suspend fun removeById(id: Long)

    suspend fun likeById(id: Long)

    suspend fun repost(parentId: Long, text: String)

}