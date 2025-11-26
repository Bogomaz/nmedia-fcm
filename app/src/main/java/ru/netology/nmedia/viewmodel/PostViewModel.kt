package ru.netology.nmedia.viewmodel

import androidx.lifecycle.ViewModel
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryInMemoy

class PostViewModel: ViewModel() {
    private val repository: PostRepository = PostRepositoryInMemoy()

    val data = repository.get()

    fun like(){
        repository.like()
    }

    fun repost(){
        repository.repost()
    }
}