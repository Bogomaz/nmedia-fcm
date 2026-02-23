package ru.netology.pusher

import com.google.firebase.messaging.Message
import com.google.gson.Gson

data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
    val postTitle: String,
    val postContent: String,
)

val postTitle = "Как устроена BPM/ECM‑платформа и почему ваш знакомый айтишник её не осилит"

val postContent = """
В статье разбираем, из чего состоит BPM/ECM‑платформа, как всё это стыкуется с корпоративным ландшафтом и почему попытка «сделать то же самое, только быстрее и дешевле» почти всегда заканчивается болью и переписываниями.
""".trimIndent()


val newPost = NewPost(
    userId = 1,
    userName = "Sergey",
    postId = 2,
    postAuthor = "Netology",
    postTitle = postTitle,
    postContent = postContent,
)


val gson = Gson()
val contentJson = gson.toJson(newPost)

val newPostMessage = Message.builder()
    .putData("action", "NEW_POST")
    .putData("content", contentJson)
    .setToken(token)
    .build()