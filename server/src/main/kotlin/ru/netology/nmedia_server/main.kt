package ru.netology.nmedia_server

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class NMediaApplication

fun main(args: Array<String>) {
    runApplication<NMediaApplication>(*args)
}

//fun main() {
//    val options = FirebaseOptions.builder()
//        .setCredentials(GoogleCredentials.fromStream(FileInputStream("fcm.json")))
//        .build()
//
//    FirebaseApp.initializeApp(options)
//
//    FirebaseMessaging.getInstance().send(newPostMessage)
//
//    // Неизвестное уведомление.
//    val anyMessage = Message.builder()
//        .putData("action", "HELLO")
//        .putData(
//            "content", """{I am a message}"""
//        )
//        .setToken(token)
//        .build()
//    FirebaseMessaging.getInstance().send(anyMessage)
//
//    FirebaseMessaging.getInstance().send(likeMessage)
//}


