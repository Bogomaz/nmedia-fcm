package ru.netology.nmedia.dto

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlin.Int

/**
 * Структура поста на клиенте
 */
@Parcelize
data class Post(
    val id: Long = 0L,
    val parentId: Long? = null,
    val publishedDate: Long = 0,
    val author: String = "",
    val text: String = "",
    val videoLink: String = "",
    val videoDescription: String = "",
    val videoDate: String = "",
    val commentsCount: Int = 0,
    val likesCount: Int = 0,
    @SerializedName("liked")
    val isLiked: Boolean = false,
    val viewsCount: Int = 0,
    val repostsCount: Int = 0,
) : Parcelable

