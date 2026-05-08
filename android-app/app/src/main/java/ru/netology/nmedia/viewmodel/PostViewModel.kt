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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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

    private val _data = MutableStateFlow(FeedModel())
    val data: StateFlow<FeedModel> get() = _data
    val edited = MutableLiveData(emptyPost)

    private val _postCreated = SingleLiveEvent<Unit>()
    val postCreated: LiveData<Unit> get() = _postCreated
    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: LiveData<String> get() = _errorEvent

    init {
        // поток кешированных постов из Room
        viewModelScope.launch {
            repository.data.collect { posts ->
                _data.update { cur ->
                    cur.copy(
                        posts = posts,
                        empty = posts.isEmpty(),
                        loading = false,
                        error = false,
                    )
                }
            }
        }
        // поток количества скрытых постов (для плашки)
        viewModelScope.launch {
            repository.hiddenCount.collect { count ->
                _data.update { cur ->
                    cur.copy(newCount = count)
                }
            }
        }
        loadPosts()
        // периодически проверять новые посты
        viewModelScope.launch {
            while (true) {
                delay(60_000L)
                checkForNewer()
            }
        }
    }

    fun loadPosts() = viewModelScope.launch {
        try {
            _data.update { it.copy(loading = true, error = false) }
            repository.getAll()
            _data.update { it.copy(loading = false) }
        } catch (e: Exception) {
            _data.update { it.copy(loading = false, error = true) }
        }
    }

    fun checkForNewer() = viewModelScope.launch {
        try {
            repository.getNewer()
            // новые посты попадут в Room как isVisible = false,
            // hiddenCount обновится через Flow и newCount изменится
        } catch (e: Exception) {
            _errorEvent.value = "Ошибка при загрузке новых постов"
        }
    }

    fun showNewPosts() = viewModelScope.launch {
        try {
            repository.showAllHidden()
            // hiddenCount станет 0, newCount обновится сам
        } catch (e: Exception) {
            _errorEvent.value = "Не удалось показать новые посты"
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
                // запись появится в Room и UI обновится сам
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