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
        loadPosts()
    }

    fun loadPosts() {
        _data.postValue(FeedModel(loading = true))
        repository.getAllAsync(object : PostRepository.GetAllCallback {
            override fun onSuccess(posts: List<Post>) {
                _data.postValue(FeedModel(posts = posts, empty = posts.isEmpty()))
            }

            override fun onError(e: Exception) {
                _data.postValue(FeedModel(error = true))
            }
        })
    }

    fun likeById(id: Long) {
        val currentState = _data.value ?: return
        val currentPosts = currentState.posts

        val post = currentPosts.find { it.id == id } ?: return

        if (post.isLiked) {
            repository.unlikeByIdAsync(id, object : PostRepository.UnlikeCallback {
                override fun onSuccess(id: Long) {
                    loadPosts()
                }

                override fun onError(e: Exception) {
                    val state = _data.value ?: FeedModel()
                    _data.postValue(FeedModel(error = true))
                }
            })
        } else {
            repository.likeByIdAsync(id, object : PostRepository.LikeCallback {
                override fun onSuccess(id: Long) {
                    loadPosts()
                }

                override fun onError(e: Exception) {
                    val state = _data.value ?: FeedModel()
                    _data.postValue(state.copy(error = true))
                }
            })
        }
    }

    fun repost(parentId: Long, newText: String) {
        val trimmed = newText.trim()
        if (trimmed.isBlank()) return

        repository.repostAsync(parentId, trimmed, object : PostRepository.SaveCallback {
            override fun onSuccess(post: Post) {
                // простой вариант – перезагрузить ленту
                loadPosts()
            }

            override fun onError(e: Exception) {
                val currentState = _data.value ?: FeedModel()
                _data.postValue(currentState.copy(error = true))
            }
        })
    }

    fun save(newText: String) {
        val trimmedText = newText.trim()
        if (trimmedText.isBlank()) return

        val current = edited.value ?: emptyPost
        val toSave = current.copy(text = trimmedText)

        repository.saveAsync(toSave, object : PostRepository.SaveCallback {
            override fun onSuccess(post: Post) {
                // Точечное обновление:
                val currentState = _data.value ?: FeedModel()
                val posts = currentState.posts

                val newPosts = if (current.id == 0L) {
                    // Создание нового поста и добавление его в начало
                    listOf(post) + posts
                } else {
                    // Редактирование. Замена по id
                    posts.map { if (it.id == post.id) post else it }
                }

                _data.postValue(
                    currentState.copy(
                        posts = newPosts,
                        empty = newPosts.isEmpty(),
                        error = false,
                        loading = false,
                    )
                )

                edited.postValue(emptyPost)
                _postCreated.postValue(Unit)
            }

            override fun onError(e: Exception) {
                val currentState = _data.value ?: FeedModel()
                _data.postValue(currentState.copy(error = true, loading = false))
            }
        })
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {
        // Берём текущее состояние ленты
        val currentState = _data.value ?: return
        val currentPosts = currentState.posts

        // Можно оптимистично сразу убрать пост из UI:
        val newPosts = currentPosts.filter { it.id != id }
        _data.value = currentState.copy(posts = newPosts, empty = newPosts.isEmpty())

        // Запускаем удаление на сервере
        repository.removeByIdAsync(id, object : PostRepository.RemoveCallback {
            override fun onSuccess(id: Long) {
                loadPosts()
            }

            override fun onError(e: Exception) {
                _data.postValue(currentState.copy(error = true))
            }
        })
    }
}