package ru.netology.nmedia_server

import com.google.firebase.messaging.Message

// Сообщение о новой реакции на пост
val likeMessage = Message.builder()
    .putData("action", "LIKE")
    .putData(
        "content", """{
          "userId": 1,
          "userName": "Vasiliy",
          "postId": 2,
          "postAuthor": "Netology"
        }""".trimIndent()
    )
    .setToken(token)
    .build()