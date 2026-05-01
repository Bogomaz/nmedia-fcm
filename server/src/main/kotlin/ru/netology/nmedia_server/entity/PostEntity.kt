package ru.netology.nmedia_server.entity

import ru.netology.nmedia_server.dto.Post
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.Id
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import ru.netology.nmedia_server.dto.Attachment

@Embeddable
data class AttachmentEmbeddable(
    var url: String? = null,
    var description: String? = null,
    var type: String? = null,
) {
    fun toDto() = url?.let { urlNonNull ->
        Attachment(
            url = urlNonNull,
            description = description ?: "",
            type = type ?: "IMAGE",
        )
    }

    companion object {
        fun fromDto(dto: Attachment?) = dto?.let {
            AttachmentEmbeddable(
                url = it.url,
                description = it.description,
                type = it.type,
            )
        }
    }
}

@Entity
data class PostEntity(
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

    var authorAvatar: String? = null,
    @Embedded
    var attachment: AttachmentEmbeddable? = null,
) {
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
        repostsCount,
        authorAvatar = authorAvatar,
        attachment = attachment?.toDto(),
    )

    companion object {
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
            post.repostsCount,
            authorAvatar = post.authorAvatar,
            attachment = AttachmentEmbeddable.fromDto(post.attachment),
        )
    }
}
