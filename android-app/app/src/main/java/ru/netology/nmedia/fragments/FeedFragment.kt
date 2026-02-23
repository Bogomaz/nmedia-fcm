package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.viewmodel.PostViewModel
import androidx.fragment.app.activityViewModels
import ru.netology.nmedia.interfaces.PostListener
import ru.netology.nmedia.model.EditMode
import ru.netology.nmedia.utils.editMode
import ru.netology.nmedia.utils.openVideo

import ru.netology.nmedia.utils.postId


class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PostViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PostsAdapter(object : PostListener {

            // Этот метод передаёт данные через bundle с помощью делегата
            override fun onViewPost(post: Post) {
                val bundle = Bundle().apply { postId = post.id }

                findNavController().navigate(
                    R.id.action_feedFragment_to_readPostFragment,
                    bundle
                )
            }


            // Этот метод передаёт данные на форму редактирования через bundle
            override fun onEdit(post: Post) {
                viewModel.edit(post)
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().apply {
                        postId = post.id
                        editMode = EditMode.EDIT.name
                    }
                )
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onPlayVideo(post: Post) {
                requireContext().openVideo(post.videoLink)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRepost(post: Post) {

                // Когда нужно поделиться данными с другими приложениями через intent
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().apply {
                        postId = post.id
                        editMode = EditMode.REPOST.name
                    }
                )
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
            findNavController().navigate(
                R.id.action_feedFragment_to_editPostFragment,
                Bundle().apply {
                    postId = 0
                    editMode = EditMode.CREATE.name
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}