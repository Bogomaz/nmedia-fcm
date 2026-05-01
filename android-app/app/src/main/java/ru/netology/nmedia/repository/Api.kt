package ru.netology.nmedia.repository

object Api {
    const val BASE_URL = "http://10.0.2.2:9999"

    // REST API
    const val POSTS_URL = "$BASE_URL/api/posts"
    fun postByIdUrl(id: Long) = "$POSTS_URL/$id"
    fun likesUrl(id: Long) = "$POSTS_URL/$id/likes"

    // Медиа
    fun avatarUrl(name: String) = "$BASE_URL/avatars/$name"
    fun imageUrl(name: String) = "$BASE_URL/images/$name"
}