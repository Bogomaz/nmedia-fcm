package ru.netology.nmedia_server.repository

import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import ru.netology.nmedia_server.entity.CommentEntity

interface CommentRepository : JpaRepository<CommentEntity, Long> {
    fun findAllByPostId(postId: Long, sort: Sort): List<CommentEntity>
    @Modifying
    fun removeAllByPostId(postId: Long)
}