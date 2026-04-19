package ru.netology.nmedia_server.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.repository.findByIdOrNull
import ru.netology.nmedia_server.dto.Post
import ru.netology.nmedia_server.entity.PostEntity
import ru.netology.nmedia_server.exception.NotFoundException
import ru.netology.nmedia_server.repository.PostRepository
import java.time.OffsetDateTime

@Service
@Transactional
class PostService(private val repository: PostRepository) {
    fun getAll(): List<Post> = repository
        .findAll(Sort.by(Sort.Direction.DESC, "id"))
        .map { it.toDto() }

    fun getById(id: Long): Post = repository
        .findById(id)
        .map { it.toDto() }
        .orElseThrow(::NotFoundException)

    fun save(dto: Post): Post {
        // создаём или находим пост для сохранения
        val entity = repository
            .findById(dto.id)
            .orElse(
                PostEntity.fromDto(
                    dto.copy(
                        likesCount = 0,
                        isLiked = false,
                        publishedDate = OffsetDateTime.now().toEpochSecond()
                    )
                )
            )

        val saved = if (entity.id == 0L) {
            // новый пост (create)
            val newPost = repository.save(entity)

            // если это репост (есть parentId) – увеличиваем счётчик у родителя
            dto.parentId?.let { parentId ->
                repository.findById(parentId).ifPresent { parent ->
                    parent.repostsCount += 1
                    repository.save(parent)
                }
            }

            newPost
        } else {
            // редактирование существующего поста
            entity.content = dto.text
            repository.save(entity)
        }

        return saved.toDto()
    }

    fun removeById(id: Long) {
        repository.findByIdOrNull(id)
            ?.also(repository::delete)
    }

    fun likeById(id: Long): Post {
        println(">>> SERVER: likeById($id)")
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                likesCount += 1
                isLiked = true
            }
            .let(repository::save)
            .toDto()
    }


    fun unlikeById(id: Long): Post {
        println(">>> SERVER: unlikeById($id)")
        return repository
            .findById(id)
            .orElseThrow(::NotFoundException)
            .apply {
                likesCount -= 1
                isLiked = false
            }
            .let(repository::save)
            .toDto()
    }
}