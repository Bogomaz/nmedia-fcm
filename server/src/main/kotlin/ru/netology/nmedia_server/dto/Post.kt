package ru.netology.nmedia_server.dto

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
    val isLiked: Boolean = false,
    val viewsCount: Int = 0,
    val repostsCount: Int = 0,

//    val id: Long,
//    val author: String,
//    val content: String,
//    val published: Long,
//    val likedByMe: Boolean,
//    val likes: Int = 0,
)