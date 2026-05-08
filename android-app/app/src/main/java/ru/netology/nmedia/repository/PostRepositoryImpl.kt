package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.netology.nmedia.api.PostApi
import ru.netology.nmedia.dao.PostDao
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toDto



class PostRepositoryImpl(private val dao: PostDao) : PostRepository {
    override val data: Flow<List<Post>> = dao.getAll().map { entities -> entities.toDto() }
    override val hiddenCount: Flow<Int> = dao.getHiddenCount()

    private val api = PostApi.service

    override suspend fun getAll(): List<Post> {
        val postsFromServer = api.getAll()

        postsFromServer.forEach {
            android.util.Log.d(
                "POSTS",
                "id=${it.id}, author=${it.author}, avatar=${it.authorAvatar}"
            )
        }

        val enriched = postsFromServer.map { postFromServer ->
            postFromServer.copy(
                id = 0L,
                serverId = postFromServer.id,
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


    override suspend fun showAllHidden() {
        dao.showAllHidden()
    }

    override suspend fun getNewer(): Int {
        // Берём максимальный serverId из локальной БД
        val maxServerId = dao.getMaxServerId() ?: 0L
        if (maxServerId == 0L) return 0

        // Просим у сервера посты новее этого id
        val newerFromServer = api.getNewer(maxServerId)
        if (newerFromServer.isEmpty()) return 0

        // Подготавливаем Post:
        //    - id локальный пока 0
        //    - serverId = id с сервера
        val prepared = newerFromServer.map { postFromServer ->
            postFromServer.copy(
                id = 0L,
                serverId = postFromServer.id,
            )
        }

        // Сохраняем в Room как "скрытые" (isVisible = false), если записи ещё нет;
        // если есть — обновляем данные, но не трогаем видимость
        for (post in prepared) {
            val serverId = post.serverId ?: continue
            val existing = dao.getByServerId(serverId)

            val entity = if (existing != null) {
                // обновляем существующую запись, сохраняя её localId и isVisible
                PostEntity.fromDto(
                    post.copy(id = existing.localId)
                ).copy(isVisible = existing.isVisible)
            } else {
                // новый пост: по умолчанию скрытый
                PostEntity.fromDto(post).copy(isVisible = false)
            }

            if (existing != null) {
                dao.update(entity)
            } else {
                dao.insert(entity)
            }
        }

        // Возвращаем количество новых записей с сервера
        return prepared.size
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
        val entity = dao.getByLocalId(id)
            ?: throw IllegalStateException("Пост с localId=$id не найден")
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
        val entity = dao.getByLocalId(id)
            ?: throw IllegalStateException("Пост с localId=$id не найден")
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
