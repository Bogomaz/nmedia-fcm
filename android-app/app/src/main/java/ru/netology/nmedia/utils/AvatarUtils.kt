package ru.netology.nmedia.utils

import ru.netology.nmedia.dto.Post

object AvatarUtils {
    fun resolveAvatarFileName(post: Post): String {
        // если сервер дал конкретный файл — используем его
        post.authorAvatar?.takeIf { it.isNotBlank() }?.let { return it }

        return when (post.author) {
            "Сбер" -> "sber.jpg"
            "Netology", "Нетология" -> "netology.jpg"
            "Тинькофф" -> "tcs.jpg"
            else -> "noname.png"
        }
    }
}