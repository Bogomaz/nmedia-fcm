package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.model.Post

@Entity
data class PostEntity (
    @PrimaryKey(autoGenerate = true)
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
){
    fun toDto() = Post(
        id,
        parentId,
        publishedDate,
        author,
        text,
        videoLink,
        videoDescription,
        videoDate,
        commentsCount,
        likesCount,
        isLiked,
        viewsCount,
        repostsCount
    )

    companion object{
        fun fromDto(post: Post) = PostEntity(
            post.id,
            post.parentId,
            post.publishedDate,
            post.author,
            post.text,
            post.videoLink,
            post.videoDescription,
            post.videoDate,
            post.commentsCount,
            post.likesCount,
            post.isLiked,
            post.viewsCount,
            post.repostsCount
        )
    }
}
