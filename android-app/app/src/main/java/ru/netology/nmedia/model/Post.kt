package ru.netology.nmedia.model

import kotlinx.parcelize.Parcelize
import android.os.Parcelable
@Parcelize
data class Post(
    val id: Int = 0,
    val parentId: Int? = null,
    val date: Int = 0,
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
