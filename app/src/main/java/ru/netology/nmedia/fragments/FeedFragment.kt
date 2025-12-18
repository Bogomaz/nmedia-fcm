package ru.netology.nmedia.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import ru.netology.nmedia.interfaces.PostListener


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostsAdapter(object : PostListener {

            // Этот метод передаёт данные через bundle
            override fun onViewPost(post: Post) {
                val bundle = Bundle().apply {
                    putParcelable("post", post) // ← ключ "post", сам объект!
                }
                findNavController().navigate(
                    R.id.action_feedFragment_to_readPostFragment,
                    bundle
                )
            }


            // Этот метод передаёт данные на форму редактирования через safe args
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                val action = FeedFragmentDirections
                    .actionFeedFragmentToEditPostFragment()
                    .setPostText(post.text)
                findNavController().navigate(action)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPlayVideo(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, post.videoLink.toUri())
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
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
                        requireContext(),
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

        viewModel.data.observe(viewLifecycleOwner)
        { posts ->
            adapter.submitList(posts)
        }

        //Обработчик кнопки "Создать пост"
        binding.addNewPost.setOnClickListener()
        {
            val action = FeedFragmentDirections.actionFeedFragmentToEditPostFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}