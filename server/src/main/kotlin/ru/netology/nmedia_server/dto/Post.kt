package ru.netology.nmedia_server.dto

import com.fasterxml.jackson.annotation.JsonProperty
import ru.netology.nmedia_server.enumeration.AttachmentType

data class Post(
    val id: Long,
    val authorId: Long,
    val content: String,
    val published: Long,
    val likedByMe: Boolean,
    val likes: Int = 0,
    var attachment: Attachment? = null,
)

data class Attachment(
    val url: String,
    val description: String,
    val type: AttachmentType,
)