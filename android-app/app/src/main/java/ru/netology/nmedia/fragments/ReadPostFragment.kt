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
import ru.netology.nmedia.model.EditMode
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.service.DateTimeService.formatUnixTime
import ru.netology.nmedia.service.ConvertNumberService
import ru.netology.nmedia.utils.editMode
import ru.netology.nmedia.utils.openVideo
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.getValue
import ru.netology.nmedia.utils.postId

class ReadPostFragment() : Fragment() {

    private var postId: Int = 0
    private var currentPost: Post? = null
    private var _binding: FragmentReadPostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postId = requireArguments().postId
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

        // шапка, меню и обработчики кнопок
        binding.apply {
            //Кебаб-меню
            topAppBar.inflateMenu(R.menu.post_menu)
            topAppBar.setOnMenuItemClickListener { menuItem ->
                currentPost?.let { post ->
                    when (menuItem.itemId) {
                        R.id.remove -> {
                            viewModel.removeById(postId)    // удалить выбранный пост
                            findNavController().navigateUp() // вернуться на тот фрагмент, с которого пришли.
                            true
                        }

                        R.id.edit -> {
                            viewModel.edit(post)
                            findNavController().navigate(
                                R.id.action_readPostFragment_to_EditPostFragment,
                                Bundle().apply {
                                    postId = post.id
                                    editMode = EditMode.EDIT.name
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
            topAppBar.setTitle(R.string.reading_post_title)
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            likes.setOnClickListener {
                viewModel.likeById(postId)
            }

            repost.setOnClickListener {
                findNavController().navigate(
                    R.id.action_readPostFragment_to_EditPostFragment,
                    Bundle().apply {
                        this.postId = this@ReadPostFragment.postId
                        editMode = EditMode.REPOST.name
                    }
                )

                ///TODO: Для шаринга сделать отдельную кнопку. Репост внутри приложения и шаринг - это разные вещи
//                val intent = Intent(Intent.ACTION_SEND).apply {
//                    putExtra(Intent.EXTRA_TEXT, currentPost?.text)
//                    type = "text/plain"
//                }
//
//                try {
//                    startActivity(Intent.createChooser(intent, null))
//                } catch (e: Exception) {
//                    Toast.makeText(
//                        requireContext(),
//                        "Apps not found",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
            }

            playButton.setOnClickListener {
                requireContext().openVideo(currentPost?.videoLink)
            }

            video.setOnClickListener {
                requireContext().openVideo(currentPost?.videoLink)
            }
        }

        // Обновление данных при изменении данных поста.
        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.find { it.id == postId } ?: return@observe
            currentPost = post
            binding.apply {
                author.text = post.author
                avatar.setImageResource(R.drawable.avatar)
                published.text = formatUnixTime(post.date)

                content.text = post.text
                if (post.videoLink.isNotEmpty()) {
                    video.visibility = View.VISIBLE
                    videoDescription.text = post.videoDescription
                    videoDate.text = post.videoDate
                } else {
                    video.visibility = View.GONE
                }
                likes.isChecked = post.isLiked
                likes.text = ConvertNumberService.convertNumberIntoText(post.likesCount)
                repost.text = ConvertNumberService.convertNumberIntoText(post.repostsCount)
                comments.text = ConvertNumberService.convertNumberIntoText(post.commentsCount)
                views.text = ConvertNumberService.convertNumberIntoText(post.viewsCount)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // очистить binding в конце жизни фрагмента
        currentPost = null
    }
}