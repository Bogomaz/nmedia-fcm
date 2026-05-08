package ru.netology.nmedia_server.dto

data class Comment(
    val id: Long,
    val postId: Long,
    val author: String,
    val authorAvatar: String? = null,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
)
