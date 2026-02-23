package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.model.Post

class PostRepositoryRoomImpl(
    private val dao: PostDao
) : PostRepository {

    override fun getAll() =
        dao.getAll().map { list ->
            list.map { it.toDto() }
        }

    override fun save(post: Post) = dao.save(PostEntity.fromDto(post))

    override fun likeById(id: Int) = dao.likeById(id)

    override fun repost(parentId: Int, text: String) {
        val parent = dao.getById(parentId) ?: return
        val repostEntity = parent.copy(
            id = 0,
            parentId = parentId,
            text = text.ifBlank { parent.text },
            date = (System.currentTimeMillis() / 1000).toInt(),
            likesCount = 0,
            isLiked = false,
            commentsCount = 0,
            viewsCount = 0,
            repostsCount = 0
        )

        dao.insert(repostEntity)

        dao.incrementRepostsCount(parentId)
    }
    override fun removeById(id: Int) = dao.removeById(id)
}