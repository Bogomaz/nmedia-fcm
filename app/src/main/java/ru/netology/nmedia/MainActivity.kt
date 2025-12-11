package ru.netology.nmedia

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.launch
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.adapter.PostListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.utils.AndroidUtils

class MainActivity : AppCompatActivity() {

    val viewModel: PostViewModel by viewModels()

    val postLauncher = registerForActivityResult(PostActivityContract()) { result ->
        val editingPost = viewModel.edited.value
        if(editingPost != null && result != null) {
            val updatedPost = editingPost.copy(text = result)
            viewModel.save(updatedPost.text)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Создаём адаптер, передаём ему объект PostListener
        //Переопределяем все методы  так, чтобы в них вызывались функции их вьюшки
        val adapter = PostsAdapter(object : PostListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                postLauncher.launch(post.text)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPlayVideo(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                try{
                    startActivity(intent)
                }catch(e:Exception){
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.invalid_link),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRepost(post: Post) {
                viewModel.repostById(post.id)

                // Когда нужно поделиться данными с другими приложениями через intent
                val intent = Intent()
                    .putExtra(Intent.EXTRA_TEXT, post.text)
                    .setAction(Intent.ACTION_SEND)
                    .setType("text/plain")
                try {
                    startActivity(Intent.createChooser(intent, null))
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Apps not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        binding.postList.adapter = adapter // созданный адаптер помещаем в Recycler View с постами

        // Переводит ленту вверх, чтобы пользователь сразу
        // видел добавленный пост
        adapter.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {
                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    if (positionStart == 0) {
                        binding.postList.smoothScrollToPosition(0)
                    }
                }
            }
        )

        viewModel.data.observe(this)
        { posts ->
            adapter.submitList(posts)
        }

        //Обработчик кнопки Сохранить
        binding.addNewPost.setOnClickListener()
        {
            postLauncher.launch(null)
        }
    }
}

