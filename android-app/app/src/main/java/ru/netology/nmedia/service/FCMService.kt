package ru.netology.nmedia.service

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import ru.netology.nmedia.R
import kotlin.random.Random


class FCMService : FirebaseMessagingService() {

    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val actionString = message.data[action]
        val action = actionString?.toAction() ?: Action.UNKNOWN

        when (action) {
            Action.LIKE -> {
                val likeJson = message.data[content]  // content = "content"
                if (likeJson == null) {
                    Log.w("FCM", "LIKE action without content")
                    return
                }
                val like = gson.fromJson(likeJson, Like::class.java)
                handleLike(like)
            }

            Action.NEW_POST -> {
                val newPostJson = message.data[content]
                if (newPostJson == null) {
                    Log.w("FCM", "NEW_POST action without content")
                    return
                }
                val newPost = gson.fromJson(newPostJson, NewPost::class.java)
                handleNewPost(newPost)
            }

            Action.UNKNOWN -> {
                Log.w("FCM", "Unknown action in notificcation: $actionString")
            }
        }

        Log.d("FCM", "message: ${Gson().toJson(message)}")
    }

    override fun onNewToken(token: String) {
        println(token)
    }

    // Обработка контента сообщения о новом лайке.
    private fun handleLike(content: Like) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_liked,
                    content.userName,
                    content.postAuthor,
                )
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notify(notification)
    }

    // Обработка контента сообщения о новом посте.
    private fun handleNewPost(content: NewPost) {
        val shortText = content.postTitle

        val longText = buildString {
            appendLine(content.postTitle)
            appendLine()
            appendLine(content.postContent)
        }

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(
                    R.string.notification_user_new_post,
                    content.userName
                )
            )
            .setContentText(shortText) // в свёрнутом виде
            .setStyle(
                NotificationCompat.BigTextStyle()  // стиль «разворачиваемого» уведомления
                    .bigText(longText)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notify(notification)
    }

    // Вывод уведомления
    private fun notify(notification: Notification) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(Random.nextInt(100_000), notification)
        }
    }
}

// Список типов экшенов, которые могут приходить от сервера
enum class Action {
    LIKE,
    NEW_POST,
    UNKNOWN;
}

// Сравнение входящего экшена со списком валидных.
// Преобразование неизвестных типов к типу UNKNOWN
fun String.toAction(): Action =
    try {
        Action.valueOf(this)
    } catch (_: IllegalArgumentException) {
        Action.UNKNOWN
    }

data class Like(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
)

data class NewPost(
    val userId: Long,
    val userName: String,
    val postId: Long,
    val postAuthor: String,
    val postTitle: String,
    val postContent: String,
)