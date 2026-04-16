package ru.netology.nmedia.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.FeedModel
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.repository.PostRepository
import ru.netology.nmedia.repository.PostRepositoryImpl
import ru.netology.nmedia.utils.SingleLiveEvent
import kotlin.concurrent.thread

val emptyPost = Post(
    publishedDate = (System.currentTimeMillis() / 1000),
    author = "Студент Нетологии",
    text = "",
    commentsCount = 0,
    likesCount = 0,
    viewsCount = 0,
    repostsCount = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository = PostRepositoryImpl()
    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel>
        get() = _data
    val edited = MutableLiveData(emptyPost)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit>
        get() = _postCreated

    init {
        load()
    }

    fun load() {
        thread {
            _data.postValue(FeedModel(loading = true))

            val result = try {
                val posts = repository.getAll()
                FeedModel(posts = posts, empty = posts.isEmpty())
            } catch (_: Exception) {
                FeedModel(error = true)
            }

            _data.postValue(result)
        }
    }

    fun likeById(id: Long) {
        repository.likeById(id)
    }

    fun repost(parentId: Long, text: String) {
        repository.repost(parentId, text)
    }

    fun save(newText: String) {
        thread {
            val trimmedText = newText.trim()
            if(trimmedText.isBlank()) return@thread

            val current = edited.value ?: emptyPost

            val toSave = current.copy(text = trimmedText)
            try{
                repository.save(toSave)
                val posts = repository.getAll()
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))

                edited.postValue(emptyPost)
                _postCreated.postValue(Unit)
            } catch(_:Exception){
                _data.postValue(FeedModel(error = true))
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {
        thread{
            try{
                repository.removeById(id)
                val posts = repository.getAll()
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }catch(e:Exception){
                _data.postValue(FeedModel(error = true))
            }
        }
    }
}