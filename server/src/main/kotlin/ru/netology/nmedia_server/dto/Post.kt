package ru.netology.nmedia_server.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class Post(
    val id: Long = 0L,
    val parentId: Long? = null,
    val publishedDate: Long,
    val author: String = "",
    val text: String = "",
    val videoLink: String = "",
    val videoDescription: String = "",
    val videoDate: String = "",
    val commentsCount: Int = 0,
    val likesCount: Int = 0,
    @JsonProperty("liked")
    val isLiked: Boolean = false,
    val viewsCount: Int = 0,
    val repostsCount: Int = 0,
)