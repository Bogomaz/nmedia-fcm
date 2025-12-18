@file:JvmName("EditPostFragmentKt")

package ru.netology.nmedia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentReadPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.service.DateTimeService
import ru.netology.nmedia.service.PostService
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.getValue

class ReadPostFragment() : Fragment() {

    private var postId: Int = 0
    private var currentPost: Post? = null
    private var _binding: FragmentReadPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = requireArguments().getParcelable<Post>(ARG_POST)?.id
            ?: error("Произошла непредвиденная ситуация.")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReadPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // меню и обработчики событий
        binding.apply {
            //Кебаб-меню
            topAppBar.inflateMenu(R.menu.post_menu)
            topAppBar.setOnMenuItemClickListener { menuItem ->
                currentPost?.let { post ->
                    when (menuItem.itemId) {
                        R.id.remove -> {
                            viewModel.removeById(postId)
                            findNavController().navigateUp()
                            true
                        }
                        R.id.edit -> {
                            viewModel.edit(post)
                              findNavController().navigate(
                                R.id.action_readPostFragment_to_EditPostFragment,
                                Bundle().apply {
                                    putString("postText", post.text)
                                }
                            )
                            true
                        }

                        else -> false
                    }
                } ?: false
            }

            // Кнопка Назад
            topAppBar.setNavigationIcon(R.drawable.ic_close_editing)
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            likes.setOnClickListener {
                viewModel.likeById(postId)
            }
            shares.setOnClickListener {
                viewModel.repostById(postId)
            }
        }

        // Обновление данных
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId } ?: return@observe
            currentPost = post
            binding.apply {
                author.text = post.author
                avatar.setImageResource(R.drawable.avatar)
                published.text = DateTimeService.formatUnixTime(post.date)
                content.text = post.text
                if (post.videoLink.isNotEmpty()) {
                    video.visibility = View.VISIBLE
                    videoDescription.text = post.videoDescription
                    videoDate.text = post.videoDate
                } else {
                    video.visibility = View.GONE
                }
                likes.isChecked = post.isLiked
                likes.text = PostService.convertNumberIntoText(post.likesCount)
                shares.text = PostService.convertNumberIntoText(post.repostsCount)
                comments.text = PostService.convertNumberIntoText(post.commentsCount)
                views.text = PostService.convertNumberIntoText(post.viewsCount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // очистить binding в конце жизни фрагмента
        currentPost = null
    }

    companion object {
        private const val ARG_POST = "post"
        fun newInstance(
            post: Post
        ): ReadPostFragment {

            return ReadPostFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_POST, post)
                }
            }
        }
    }
}