package ru.netology.nmedia.interfaces

import ru.netology.nmedia.model.Post

// Интерфейс PostListener содержит все методы, которые позволяют манипулировать постом в ленте
interface PostListener {
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onLike(post: Post)
    fun onRepost(post: Post)
    fun onPlayVideo(post: Post)
    fun onViewPost(post: Post)
}