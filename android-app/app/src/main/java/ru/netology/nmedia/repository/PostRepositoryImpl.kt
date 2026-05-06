package ru.netology.nmedia.repository

import androidx.lifecycle.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.dto.Author
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto
import ru.netology.nmedia.entity.toEntity


class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    //override val data = dao.getAll().map(List<PostEntity>::toDto)
    override val data = dao.getAll().map { entities -> entities.toDto() }
    private val api = PostApi.service

    override suspend fun getAll(): List<Post> {
        val postsFromServer = api.getAll()

        // Добавляем авторов и сразу раскладываем:
        //    serverId = id с сервера, локальный id пока 0.
        val enriched = postsFromServer.map { postFromServer ->
            val withAuthor = try {
                val author: Author = api.getAuthorById(postFromServer.authorId)
                postFromServer.copy(
                    author = author.name,
                    authorAvatar = author.avatar,
                )
            } catch (e: Exception) {
                if (postFromServer.authorId == 0L) {
                    postFromServer.copy(
                        author = "Студент",
                        authorAvatar = "noname.png",
                    )
                } else {
                    postFromServer.copy(
                        author = "Noname",
                        authorAvatar = null,
                    )
                }
            }
            // id с сервера кладём в serverId, локальный пока 0
            withAuthor.copy(
                id = 0L,
                serverId = withAuthor.id,
            )
        }

        // Для каждого поста с сервера:
        //    - если уже есть запись с таким serverId → обновляем её, сохраняя localId;
        //    - если нет → вставляем новую.
        for (post in enriched) {
            val serverId = post.serverId ?: continue

            val existing = dao.getByServerId(serverId)
            val entity = if (existing != null) {
                // сохраняем localId, обновляем остальные поля
                PostEntity.fromDto(
                    post.copy(id = existing.localId)
                )
            } else {
                PostEntity.fromDto(post)
            }

            if (existing != null) {
                dao.update(entity)
            } else {
                dao.insert(entity)
            }
        }

        return enriched
    }

    override suspend fun save(post: Post): Post {
        // Сохраняем только локально без serverId и получаем localId
        val localEntity = PostEntity.fromDto(
            post.copy(
                id = 0L,    // Room сгенерит localId
                serverId = post.serverId, // для новых постов будет null
            )
        )
        val localId = dao.insert(localEntity)

        // Готовим пост для отправки на сервер
        val forServer = post.copy(
            id = post.serverId ?: 0L, // либо id, который ранее получили с сервера, либо ноль.
            serverId = null, // это серверу не показываем
        )
        val savedFromServer = api.savePost(forServer)

        // Добавляем автора
        val withAuthor = try {
            val author = api.getAuthorById(savedFromServer.authorId)
            savedFromServer.copy(
                author = author.name,
                authorAvatar = author.avatar
            )
        } catch (e: Exception) {
            if (savedFromServer.authorId == 0L) {
                savedFromServer.copy(author = "Студент", authorAvatar = "noname.png")
            } else {
                savedFromServer.copy(author = "Noname", authorAvatar = null)
            }
        }

        // Обновляем запись в Room
        val final = withAuthor.copy(
            id = localId,
            serverId = savedFromServer.id,
        )
        dao.update(PostEntity.fromDto(final))
        return final
    }

    override suspend fun removeById(id: Long) {
        val entity = dao.getByLocalId(id) ?: return
        val serverId = entity.serverId

        // Сначала пост будет удалён на сервере, потом из локального кеша.
        if (serverId != null) {
            api.removeById(serverId)
        }
        dao.removeByLocalId(id)
    }

    override suspend fun likeById(id: Long): Post {
        val entity = dao.getByLocalId(id) ?: return data.value?.first { it.id == id }
            ?: throw IllegalStateException()
        val serverId = entity.serverId ?: return entity.toDto()

        val updatedFromServer = api.likeById(serverId)

        val old = entity.toDto()
        val final = old.copy(
            // локальный и серверный id сохраняем
            id = id,
            serverId = serverId,

            // обновляем то, что реально поменял сервер
            isLiked = updatedFromServer.isLiked,
            likesCount = updatedFromServer.likesCount,
        )
        dao.update(PostEntity.fromDto(final))
        return final
    }

    override suspend fun unlikeById(id: Long): Post {
        val entity = dao.getByLocalId(id) ?: return data.value?.first { it.id == id }
            ?: throw IllegalStateException()
        val serverId = entity.serverId ?: return entity.toDto()

        val updatedFromServer = api.unlikeById(serverId)

        val old = entity.toDto()
        val final = old.copy(
            id = id,
            serverId = serverId,
            isLiked = updatedFromServer.isLiked,
            likesCount = updatedFromServer.likesCount,
        )
        dao.update(PostEntity.fromDto(final))
        return final
    }

    override suspend fun repost(parentId: Long, text: String): Post {
        // создаём локальную запись-черновик
        val localDraft = PostEntity.fromDto(
            Post(
                id = 0L,
                serverId = null,
                authorId = 4L,      // репост делается всегда от имени хардкодного студента
                text = text,
                isLiked = false,
                likesCount = 0,
            )
        )
        val localId = dao.insert(localDraft)

        // Готовим пост для сервера
        val forServer = Post(
            id = 0L,
            authorId = 4L,
            text = text,
            isLiked = false,
            likesCount = 0,
        )

        val savedFromServer = api.savePost(forServer)

        val withAuthor = try {
            val author = api.getAuthorById(savedFromServer.authorId)
            savedFromServer.copy(
                author = author.name,
                authorAvatar = author.avatar,
            )
        } catch (e: Exception) {
            if (savedFromServer.authorId == 0L) {
                savedFromServer.copy(author = "Студент", authorAvatar = "noname.png")
            } else {
                savedFromServer.copy(author = "Noname", authorAvatar = null)
            }
        }

        val final = withAuthor.copy(
            id = localId,
            serverId = savedFromServer.id,
        )
        dao.update(PostEntity.fromDto(final))

        return final
    }
}
