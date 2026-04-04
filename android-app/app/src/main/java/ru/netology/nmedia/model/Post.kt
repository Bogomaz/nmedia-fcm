package ru.netology.nmedia.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import kotlin.Int

/**
 * Структура поста на клиенте
 */
@Parcelize
data class Post(
    val id: Long = 0,
    val parentId: Long? = null,
    val date: Long = 0,
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
) : Parcelable

/**
 * Структура поста на сервере
 */
data class NetworkPost(
    val id: Long,
    val author: String,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
)

fun Post.clientToNetworkPostMapping(clientPost: Post): NetworkPost{
    return NetworkPost(
        id = clientPost.id,
        author = clientPost.author,
        content = clientPost.text,
        published = clientPost.date.toLong(),
        likedByMe = clientPost.isLiked,
        likes = clientPost.likesCount,
    )
}

fun NetworkPost.networkToClientPostMapping(networkPost: NetworkPost): Post{
    return Post(
        id = networkPost.id,
        date = networkPost.published,
        author = networkPost.author,
        text = networkPost.content,
        likesCount = networkPost.likes,
        isLiked = networkPost.likedByMe,
    )
}
