package ru.netology.nmedia.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentReadPostBinding
import ru.netology.nmedia.dto.EditMode
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.service.DateTimeService.formatUnixTime
import ru.netology.nmedia.service.ConvertNumberService
import ru.netology.nmedia.utils.editMode
import ru.netology.nmedia.viewmodel.PostViewModel
import kotlin.getValue
import ru.netology.nmedia.utils.postId
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.api.avatarUrl
import ru.netology.nmedia.api.imageUrl

class ReadPostFragment : Fragment() {

    private var postId: Long = 0
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

            topAppBar.setNavigationIcon(R.drawable.ic_close_editing)
            topAppBar.setTitle(R.string.reading_post_title)
            topAppBar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }

            likes.setOnClickListener {
                viewModel.likeById(postId)
            }

            repost.setOnClickListener {
                currentPost?.let { post ->
                    viewModel.edit(post)
                    findNavController().navigate(
                        R.id.action_readPostFragment_to_EditPostFragment,
                        Bundle().apply {
                            this.postId = this@ReadPostFragment.postId
                            editMode = EditMode.REPOST.name
                        }
                    )
                }
            }
        }

        // Обновление данных при изменении
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.data.collectLatest { state ->
                val post = state.posts.find { it.id == postId } ?: return@collectLatest
                currentPost = post

                binding.apply {
                    author.text = post.author

                    val avatarName = post.authorAvatar ?: "noname.png"
                    Glide.with(avatar)
                        .load(avatarUrl(avatarName))
                        .timeout(15_000)
                        .transform(CircleCrop())
                        .into(avatar)

                    published.text = formatUnixTime(post.publishedDate)
                    content.text = post.text

                    val att = post.attachment
                    if (att != null && att.type == "IMAGE") {
                        attachmentImage.visibility = View.VISIBLE
                        Glide.with(attachmentImage)
                            .load(imageUrl(att.url))
                            .timeout(15_000)
                            .placeholder(R.drawable.mock)
                            .error(R.drawable.mock)
                            .into(attachmentImage)
                    } else {
                        attachmentImage.visibility = View.GONE
                    }

                    likes.isChecked = post.isLiked
                    likes.text = ConvertNumberService.convertNumberIntoText(post.likesCount)
                    repost.text = ConvertNumberService.convertNumberIntoText(post.repostsCount)
                    comments.text = ConvertNumberService.convertNumberIntoText(post.commentsCount)
                    views.text = ConvertNumberService.convertNumberIntoText(post.viewsCount)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        currentPost = null
    }
}
