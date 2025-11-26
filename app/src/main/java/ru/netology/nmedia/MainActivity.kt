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
import ru.netology.nmedia.viewmodel.PostViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.Int
import androidx.activity.viewModels

class MainActivity : AppCompatActivity() {
    private val viewModel: PostViewModel by viewModels()
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

//        val testPost = PostService.add(post);

        val binding = ActivityMainBinding.inflate(layoutInflater)

        viewModel.data.observe(this) { testPost ->
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
            }
        }
        binding.likes.setOnClickListener {
            viewModel.like()
        }

        binding.shares.setOnClickListener {
            viewModel.repost()
        }
       setContentView(binding.root)
    }

    // Конвертирует и форматирует UnixTime в строку с датой и временем публикации
    @RequiresApi(Build.VERSION_CODES.O)
    fun formatUnixTime(unixDateTime: Int): String {
        val instant = Instant.ofEpochSecond(unixDateTime.toLong())
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy 'в' HH:mm", Locale("ru"))
            .withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    // Определяет, какая иконка будет выводиться.
    fun selectImageResource(isLiked: Boolean): Int {
        return when (isLiked) {
            true -> R.drawable.cards_heart
            false -> R.drawable.heart_outline_24
        }
    }
}
