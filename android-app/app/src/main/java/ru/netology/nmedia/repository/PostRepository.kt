package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import ru.netology.nmedia.model.Post

interface PostRepository  {
    fun getAll(): LiveData<List<Post>>
    fun save(post:Post)

    fun removeById(id: Int)

    fun likeById(id: Int)

    fun repost(parentId: Int, text: String)

}