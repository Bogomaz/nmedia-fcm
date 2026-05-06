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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.netology.nmedia.db.AppDb

val emptyPost = Post(
    publishedDate = (System.currentTimeMillis() / 1000),
    author = "Студент",
    text = "",
    commentsCount = 0,
    likesCount = 0,
    viewsCount = 0,
    repostsCount = 0
)

class PostViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PostRepository =
        PostRepositoryImpl(AppDb.getInstance(application).postDao)

    private val _data = MutableLiveData(FeedModel())
    val data: LiveData<FeedModel> get() = _data
    val edited = MutableLiveData(emptyPost)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> get() = _postCreated
    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: LiveData<String> get() = _errorEvent

    private val dbSource = repository.data

    init {
        _data.value = FeedModel(loading = true)
        dbSource.observeForever {posts ->
            val current = _data.value ?: FeedModel()
            _data.value = current.copy(
                posts = posts,
                empty = posts.isEmpty(),
                loading = false,
                error = false,
            )
        }
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            try {
                _data.value = _data.value?.copy(loading = true, error = false)
                repository.getAll() // результат попадёт в Room и вернётся через подписку.
                _data.value = _data.value?.copy(
                    loading = false,
                    empty = _data.value?.posts?.isEmpty() ?: true,
                )
            } catch (e: Exception) {
                _data.value = _data.value?.copy(loading = false, error = true)
            }
        }
    }

    fun likeById(id: Long) {
        viewModelScope.launch {
            try {
                val currentPost = _data.value?.posts?.find { it.id == id } ?: return@launch
                if (currentPost.isLiked) {
                    repository.unlikeById(id)
                } else {
                    repository.likeById(id)
                }
            } catch (e: Exception) {
                _errorEvent.value = "Ошибка соединения с сервером. Попробуйте ещё раз."
            }
        }
    }

    fun repost(parentId: Long, newText: String) {
        val trimmed = newText.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            try {
                repository.repost(parentId, trimmed)
                // запись появится в Room → UI обновится сам
            } catch (e: Exception) {
                _errorEvent.value = "Ошибка соединения с сервером. Попробуйте ещё раз."
            }
        }
    }

    fun save(newText: String) {
        val trimmedText = newText.trim()
        if (trimmedText.isBlank()) return

        viewModelScope.launch {
            try {
                val current = edited.value ?: emptyPost
                val toSave = current.copy(text = trimmedText)

                repository.save(toSave)
                // после успешного ответа репозиторий обновит Room,
                // и новый/обновлённый пост сам попадёт в ленту

                edited.value = emptyPost
                _postCreated.value = Unit
            } catch (e: Exception) {
                _errorEvent.value = "Ошибка соединения с сервером. Попробуйте ещё раз."
            }
        }
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {
        viewModelScope.launch {
            try {
                repository.removeById(id)
                // Room удалит пост, и он исчезнет из UI
            } catch (e: Exception) {
                _errorEvent.value = "Ошибка соединения с сервером. Попробуйте ещё раз."
            }
        }
    }
}