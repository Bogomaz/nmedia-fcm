package ru.netology.nmedia

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.model.Comments
import ru.netology.nmedia.model.Likes
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.PostType
import ru.netology.nmedia.model.Privacy
import ru.netology.nmedia.model.Reposts
import ru.netology.nmedia.model.User
import ru.netology.nmedia.model.Views
import ru.netology.nmedia.service.PostService
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.Int

class MainActivity : AppCompatActivity() {
    val post = Post(
        fromId = 1,
        ownerId = 1,
        date = 1747841760, //21 мая в 18:36
        text = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. " +
                "Затем появились курсы по дизайну, разработке, аналитике и управлению. " +
                "Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. " +
                "Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. " +
                "Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        viewPrivacy = Privacy.EVERYONE,
        comments = Comments(
            count = 155, //Количество комментариев к записи
            readCommentsCount = 0, //Количество прочитанных комментариев.
            commentPrivacy = Privacy.EVERYONE, //Уровень доступа к комментированию заметки.
            canClose = false, // может ли текущий пользователь закрыть комментарии к записи;
            canOpen = false // может ли текущий пользователь открыть комментарии к записи.

        ),
        likes = Likes(
            count = 996, // число пользователей, которым понравилась запись;
            userLikes = false, // наличие отметки «Мне нравится» от текущего пользователя (1 — есть, 0 — нет);
            canLike = true, // информация о том, может ли текущий пользователь поставить отметку «Мне нравится» (1 — может, 0 — не может);
        ),
        views = Views(
            count = 1_495 // число просмотров записи.
        ),
        reposts = Reposts(
            count = 1_998, // число пользователей, скопировавших запись;
            canPublish = true // информация о том, может ли текущий пользователь сделать репост записи (1 — может, 0 — не может).
        ),
        attachments = null,
        postType = PostType.POST,
        replyOwnerId = 0,
        replyPostId = 0,
        isLiked = false,
    )

    val testAuthor = User(
        id = 1,
        firstName = "Нетология. Университет интернет-профессий будущего",
        lastName = "",
        middleName = null,
        login = "netology",
        isDeleted = false
    )

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val testPost = PostService.add(post);

        val binding = ActivityMainBinding.inflate(layoutInflater)

        with(binding) {
            author.text = testAuthor.firstName
            avatar.setImageResource(R.drawable.avatar)
            published.text = formatUnixTime(testPost.date)
            content.text = testPost.text
            likesCount.text = PostService.convertNumberIntoText(testPost.likes!!.count)
            viewsCount.text = PostService.convertNumberIntoText(testPost.views!!.count)
            commentsCount.text = PostService.convertNumberIntoText(testPost.comments!!.count)
            sharesCount.text = PostService.convertNumberIntoText(testPost.reposts!!.count)
            likes.setImageResource(
                selectImageResource(testPost.likes!!.userLikes)
            )
            // Клик по сердечку
            likes.setOnClickListener {
                PostService.likeHandler(testAuthor.id, testPost.id)
                likes.setImageResource(
                    selectImageResource(testPost.likes!!.userLikes)
                )
                likesCount.text = PostService.convertNumberIntoText(testPost.likes!!.count)
                Toast.makeText(this@MainActivity, "Лайк изменён.", Toast.LENGTH_SHORT).show()
            }
            // Клик по репосту
            shares.setOnClickListener {
                PostService.sharesHandler(testPost.id)
                sharesCount.text = PostService.convertNumberIntoText(testPost.reposts!!.count)
                Toast.makeText(this@MainActivity, "Вы успешно поделились постом. Стало ${testPost.reposts!!.count}", Toast.LENGTH_SHORT).show()
            }
        }
        setContentView(binding.root)
    }

    // Конвертируем и форматируем UnixTime в строку с датой и временем публикации
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatUnixTime(unixDateTime: Int): String {
        val instant = Instant.ofEpochSecond(unixDateTime.toLong())
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'в' HH:mm", Locale("ru"))
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    // Определяем, какая иконка будет выводиться.
    fun selectImageResource(isLiked: Boolean): Int {
        return when (isLiked) {
            true -> R.drawable.cards_heart
            false -> R.drawable.heart_outline_24
        }
    }
}
