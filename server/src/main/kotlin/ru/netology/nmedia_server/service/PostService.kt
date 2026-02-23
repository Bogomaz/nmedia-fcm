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

    fun save(dto: Post): Post = repository
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
        .let {
            if (it.id == 0L) repository.save(it) else it.content = dto.text
            it
        }.toDto()

    fun removeById(id: Long) {
        repository.findByIdOrNull(id)
            ?.also(repository::delete)
    }

    fun likeById(id: Long): Post = repository
        .findById(id)
        .orElseThrow(::NotFoundException)
        .apply {
            likesCount += 1
            isLiked = true
        }
        .toDto()

    fun unlikeById(id: Long): Post = repository
        .findById(id)
        .orElseThrow(::NotFoundException)
        .apply {
            likesCount -= 1
            isLiked = false
        }
        .toDto()
}