package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApiService
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.NetworkPost
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.networkToClientPostMapping

class PostRepositoryImpl(
    private val dao: PostDao,
    private val api: PostApiService,
) : PostRepository {

    // Получить все посты
    override fun getAll(): LiveData<List<Post>> =
        dao.getAll().map { list ->
            list.map { it.toDto() }
        }

    // Обновить список постов с сервера
    override suspend fun refresh() {
        val response = api.getAll()
        if(!response.isSuccessful){
            throw RuntimeException("Код ответа: ${response.code()}")
        }

        val body: List<NetworkPost> =
            response.body() ?: throw RuntimeException("Нет постов")

        val clientPosts = body.map{ it.networkToClientPostMapping(it)}

        val entities = clientPosts.map(PostEntity::fromDto)

        dao.clearAndInsert(entities)
    }

    // Сохранение пока локальное.
    override suspend fun save(post: Post){
        dao.save(PostEntity.fromDto(post))
    }

    // Удаление пока локальное
    override suspend fun removeById(id: Long) {
        // пока тоже локально, серверную часть можно добавить потом
        dao.removeById(id)
    }

    // Репост пока выполняется локально
    override suspend fun repost(parentId: Long, text: String) {
        val parent = dao.getById(parentId) ?: return
        val repostEntity = parent.copy(
            id = 0L,
            parentId = parentId,
            text = text.ifBlank { parent.text },
            date = (System.currentTimeMillis() / 1000),
            likesCount = 0,
            isLiked = false,
            commentsCount = 0,
            viewsCount = 0,
            repostsCount = 0
        )
        dao.insert(repostEntity)
        dao.incrementRepostsCount(parentId)
    }

    override suspend fun likeById(id: Long) {
        // Выбрать пост
        val entity = dao.getById(id) ?: return
        val post = entity.toDto()

        // Определить, ставить лайк или снимать
        val response = if(!post.isLiked){
            println("LIKE: id=$id")
            api.likedById(id)
        } else{
            println("UNLIKE: id=$id")
            api.unlikeById(id)
        }

        if (!response.isSuccessful) {
            throw RuntimeException("Код ответа: ${response.code()}")
        }

        val networkPost = response.body()
            ?: throw RuntimeException("Пустое тело ответа при like/unlike")

        // Конвертировать пост с сервера в локальный
        val updatedClientPost = networkPost.networkToClientPostMapping(networkPost)

        // Сохранить локальный в Room
        dao.insert(PostEntity.fromDto(updatedClientPost))

    }
}