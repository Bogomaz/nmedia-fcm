package ru.netology.nmedia.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
    val localId: Long = 0L, // Это будет локальный id, он же - ключ в Room
    val serverId: Long? = null, // Это будет серверный id, который может быть null до синхронизации

    // Серверный id автора
    val authorId: Long = 0L,
    // Легаси-поля используются для кеширования данных для UI
    val author: String = "",
    val authorAvatar: String? = null,

    // Поля поста с сервера
    @ColumnInfo(name = "content")
    val text: String = "",
    val publishedDate: Long,
    val isLiked: Boolean = false,
    val likesCount: Int = 0,

    //Остальные поля
    val parentId: Long? = null,
    val commentsCount: Int = 0,
    val viewsCount: Int = 0,
    val repostsCount: Int = 0,
){
    fun toDto() = Post(
        id = localId,
        serverId = serverId,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        text = text,
        publishedDate = publishedDate,
        isLiked = isLiked,
        likesCount = likesCount,
        parentId = parentId,
        commentsCount = commentsCount,
        viewsCount = viewsCount,
        repostsCount = repostsCount,
        attachment = null,
    )

    companion object{
        fun fromDto(post: Post) = PostEntity(
            localId = post.id,
            serverId = post.serverId, // ?: post.id,
            authorId = post.authorId,
            author = post.author,
            authorAvatar = post.authorAvatar,
            text = post.text,
            publishedDate = post.publishedDate,
            isLiked = post.isLiked,
            likesCount = post.likesCount,
            parentId = post.parentId,
            commentsCount = post.commentsCount,
            viewsCount = post.viewsCount,
            repostsCount = post.repostsCount,
        )
    }
}
fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(): List<PostEntity> = map(PostEntity::fromDto)
