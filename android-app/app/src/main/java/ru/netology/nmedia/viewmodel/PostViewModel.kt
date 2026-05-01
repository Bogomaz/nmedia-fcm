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
    private val _errorEvent = SingleLiveEvent<String>()
    val errorEvent: LiveData<String>
        get() = _errorEvent

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
                _data.postValue(
                    FeedModel(
                        posts = posts,
                        empty = posts.isEmpty(),
                        loading = false,
                        error = false
                    )
                )
            }

            override fun onError(e: Throwable) {
                _data.postValue(FeedModel(error = true, loading = false))
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
                    _errorEvent.postValue("Ошибка соединения с сервером. Попробуйте ещё раз.")
                }
            })
        } else {
            repository.likeByIdAsync(id, object : PostRepository.LikeCallback {
                override fun onSuccess(id: Long) {
                    loadPosts()
                }

                override fun onError(e: Exception) {
                    _errorEvent.postValue("Ошибка соединения с сервером. Попробуйте ещё раз.")
                }
            })
        }
    }

    fun repost(parentId: Long, newText: String) {
        val trimmed = newText.trim()
        if (trimmed.isBlank()) return

        repository.repostAsync(parentId, trimmed, object : PostRepository.SaveCallback {
            override fun onSuccess(post: Post) {
                loadPosts()
            }

            override fun onError(e: Throwable) {
                val currentState = _data.value ?: FeedModel()
                _errorEvent.postValue("Ошибка соединения с сервером. Попробуйте ещё раз.")
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
                val currentState = _data.value ?: FeedModel()
                val posts = currentState.posts

                val newPosts = if (current.id == 0L) {
                    listOf(post) + posts       // создание
                } else {
                    posts.map { if (it.id == post.id) post else it }  // редактирование
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

            override fun onError(e: Throwable) {
                val currentState = _data.value ?: FeedModel()
                _errorEvent.postValue("Ошибка соединения с сервером. Попробуйте ещё раз.")
            }
        })
    }

    fun edit(post: Post) {
        edited.value = post
    }

    fun removeById(id: Long) {
        val currentState = _data.value ?: return
        val currentPosts = currentState.posts

        // оптимистично убираем из UI
        val newPosts = currentPosts.filter { it.id != id }
        _data.value = currentState.copy(posts = newPosts, empty = newPosts.isEmpty())

        repository.removeByIdAsync(id, object : PostRepository.RemoveCallback {
            override fun onSuccess(id: Long) {
                loadPosts()
            }

            override fun onError(e: Exception) {
                _errorEvent.postValue("Ошибка соединения с сервером. Попробуйте ещё раз.")
            }
        })
    }
}