package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.Post

interface PostRepository {
    val data: Flow<List<Post>>
    val hiddenCount: Flow<Int>
    suspend fun getAll(): List<Post>
    suspend fun getNewer(): Int
    suspend fun showAllHidden()
    suspend fun save(post: Post): Post
    suspend fun removeById(id: Long)
    suspend fun likeById(id: Long): Post
    suspend fun unlikeById(id: Long): Post

    suspend fun repost(parentId: Long, text: String): Post
}