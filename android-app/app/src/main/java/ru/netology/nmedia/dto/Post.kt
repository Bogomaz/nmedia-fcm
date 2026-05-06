package ru.netology.nmedia.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlin.Int

@Parcelize
data class Attachment(
    val url: String,
    val description: String,
    val type: String,
): Parcelable

/**
 * Структура поста на клиенте
 */
@Parcelize
data class Post(
    val id: Long = 0L, // локальный id для Room
    val serverId: Long? = null, // серверный id

    val authorId: Long = 0L,
    val author: String = "",
    val authorAvatar: String? = null,

    // Поля, которые приходят с сервера
    @SerializedName("content")
    val text: String = "",
    @SerializedName("published")
    val publishedDate: Long = 0,
    @SerializedName("likedByMe")
    val isLiked: Boolean = false,
    @SerializedName("likes")
    val likesCount: Int = 0,
    val attachment: Attachment? = null,

    // Поля, которые используются на клиенте, но пока отсутствуют на сервер
    val parentId: Long? = null,
    val commentsCount: Int = 0,
    val viewsCount: Int = 0,
    val repostsCount: Int = 0,
) : Parcelable



