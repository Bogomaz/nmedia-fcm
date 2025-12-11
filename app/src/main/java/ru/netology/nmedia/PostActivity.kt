package ru.netology.nmedia

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.utils.AndroidUtils

class PostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Добавить крестик на панель управления.
        binding.topAppBar.setNavigationIcon(R.drawable.ic_close_editing)
        binding.topAppBar.setNavigationOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        // Получаем из интента текст поста.
        val postText = intent.getStringExtra(Intent.EXTRA_TEXT)
        // Если текст есть, то это режим редактирования поста
        // Ставим соотв. заголовок и заполняем поле текстом поста.
        if (postText != null) {
            binding.topAppBar.title = getString(R.string.edited_post_title)
            binding.newText.setText(postText)
        }
        // Если текста нет, то это режим создания поста.
        // Ставим соотв. заголовок и поле ввода текста оставляем пустым
        else {
            binding.topAppBar.title = getString(R.string.created_post_title)
        }

        AndroidUtils.showKeyboard(binding.newText)

        //Обработчик сохранения
        binding.savePost.setOnClickListener {
            // Если пытаются сохранить пустой текст,
            // то возвращается RESULT_CANCELED
            if (binding.newText.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED)
            }
            // Если текст есть - он помещается в intent и передаётся в результате.
            else {
                val intent = Intent()
                val content = binding.newText.text.toString()
                intent.putExtra(Intent.EXTRA_TEXT, content)
                setResult(RESULT_OK, intent)
            }
            finish()
        }
    }
}

