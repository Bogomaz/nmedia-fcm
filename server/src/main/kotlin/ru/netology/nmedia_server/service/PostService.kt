package ru.netology.nmedia_server.service

import jakarta.annotation.PostConstruct
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.data.repository.findByIdOrNull
import ru.netology.nmedia_server.dto.Attachment
import ru.netology.nmedia_server.dto.Post
import ru.netology.nmedia_server.entity.PostEntity
import ru.netology.nmedia_server.exception.NotFoundException
import ru.netology.nmedia_server.repository.PostRepository
import java.time.OffsetDateTime

@Service
@Transactional
class PostService(private val repository: PostRepository) {

    @PostConstruct
    fun init() {
        if (repository.count() > 0) return

        val now = OffsetDateTime.now().toEpochSecond()

        // пост с вложением от Сбера
        repository.save(
            PostEntity(
                publishedDate = now,
                author = "Сбер",
                content = "Привет, это новый Сбер!",
                authorAvatar = "sber.jpg",
                attachment = ru.netology.nmedia_server.entity.AttachmentEmbeddable.fromDto(
                    Attachment(
                        url = "sbercard.jpg",
                        description = "Новая карта от Сбера",
                        type = "IMAGE"
                    )
                )
            )
        )

        // пост с вложением от Нетологии
        repository.save(
            PostEntity(
                publishedDate = now - 3600,
                author = "Нетология",
                content = "Привет, это новая Нетология!",
                authorAvatar = "netology.jpg",
                attachment = ru.netology.nmedia_server.entity.AttachmentEmbeddable.fromDto(
                    Attachment(
                        url = "podcast.jpg",
                        description = "Как запустить свой подкаст: подборка статей",
                        type = "IMAGE"
                    )
                )
            )
        )

        // пост без вложения
        repository.save(
            PostEntity(
                publishedDate = now - 7200,
                author = "Тинькофф",
                content = "Нам и так норм!",
                authorAvatar = "tcs.jpg",
            )
        )
    }


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