package ru.netology.nmedia.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.Api
import ru.netology.nmedia.db.AppDb
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl

private val emptyPost = Post(
    date = (System.currentTimeMillis() / 1000),
    author = "Студент Нетологии",
    text = "",
    commentsCount = 0,
    likesCount = 0,
    viewsCount = 0,
    repostsCount = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl(
        AppDb.getInstance(application).postDao,
        Api.service
    )

    val data = repository.getAll()
    val edited = MutableLiveData(emptyPost)

    fun refresh() = viewModelScope.launch {
        try{
            repository.refresh()
        }catch(e: Exception){

        }
    }
    fun likeById(id: Long) = viewModelScope.launch {
        repository.likeById(id)
    }

    fun repost(parentId: Long, text: String)  = viewModelScope.launch{
        repository.repost(parentId, text)
    }

    fun save(newText: String) = viewModelScope.launch{
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

    fun removeById(id: Long) = viewModelScope.launch {
        repository.removeById(id)
    }
}