package ru.netology.nmedia_server.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.netology.nmedia_server.dto.Author
import ru.netology.nmedia_server.entity.AuthorEntity
import ru.netology.nmedia_server.exception.NotFoundException
import ru.netology.nmedia_server.repository.AuthorRepository

@Service
@Transactional
class AuthorService(private val repository: AuthorRepository) {
    fun getById(id: Long): Author = repository
        .findById(id)
        .map { it.toDto() }
        .orElseThrow(::NotFoundException)

    fun save(dto: Author): Author = repository
        .findById(dto.id)
        .orElse(AuthorEntity.fromDto(dto))
        .let {
            if (it.id == 0L) repository.save(it)
            it
        }.toDto()
}