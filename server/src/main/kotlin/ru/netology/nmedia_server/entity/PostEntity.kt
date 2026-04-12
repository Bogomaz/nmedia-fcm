package ru.netology.nmedia_server.entity

import ru.netology.nmedia_server.dto.Post
import jakarta.persistence.Column
import jakarta.persistence.Id
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType

@Entity
data class PostEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,
    var parentId: Long? = null,
    var publishedDate: Long,
    var author: String = "",
    @Column(columnDefinition = "TEXT")
    var content: String,
    var videoLink: String = "",
    var videoDescription: String = "",
    var videoDate: String = "",
    var commentsCount: Int = 0,
    var likesCount: Int = 0,
    var isLiked: Boolean = false,
    var viewsCount: Int = 0,
    var repostsCount: Int = 0,
){
    fun toDto() = Post(
        id,
        parentId,
        publishedDate,
        author,
        content,
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

//@Entity
//data class PostEntity(
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long,
//    var author: String,
//    @Column(columnDefinition = "TEXT")
//    var content: String,
//    var published: Long,
//    var likedByMe: Boolean,
//    var likes: Int = 0,
//) {
//    fun toDto() = Post(id, author, content, published, likedByMe, likes)
//
//    companion object {
//        fun fromDto(dto: Post) = PostEntity(dto.id, dto.author, dto.content, dto.published, dto.likedByMe, dto.likes)
//    }
//}