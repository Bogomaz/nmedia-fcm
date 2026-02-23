package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryRoomImpl

private val emptyPost = Post(
    publishedDate = (System.currentTimeMillis() / 1000),
    author = "Студент Нетологии",
    text = "",
    commentsCount = 0,
    likesCount = 0,
    viewsCount = 0,
    repostsCount = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryRoomImpl(
        AppDb.getInstance(application).postDao
    )

    val data = repository.getAll()
    val edited = MutableLiveData(emptyPost)
    fun likeById(id: Long) {
        repository.likeById(id)
    }

    fun repost(parentId: Long, text: String) {
        repository.repost(parentId, text)
    }

    fun save(newText: String) {
        edited.value?.let { post ->
            val trimmedText = newText.trim()
            if (trimmedText != post.text) {
                repository.save(
                    post.copy(text = trimmedText)
                )
                edited.value = emptyPost
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {
        repository.removeById(id)
    }
}