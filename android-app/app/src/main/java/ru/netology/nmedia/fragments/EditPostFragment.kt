package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.model.EditMode
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.utils.clearDraft
import ru.netology.nmedia.utils.editMode
import ru.netology.nmedia.utils.getDraft
import ru.netology.nmedia.utils.postId
import ru.netology.nmedia.utils.saveDraft
import ru.netology.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null // Nullable-ссылка на binding
    private val binding get() = _binding!! // кастомный геттер, который точно возвращает не null
    private var currentPost: Post? = null
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Теперь мы работаем только с идентификатором поста и режимом редактирования.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val postId = requireArguments().postId
        val editMode = requireArguments().editMode?.let { EditMode.valueOf(it) } ?: EditMode.CREATE

        binding.apply {
            when (editMode) {
                EditMode.CREATE -> {
                    topAppBar.title = getString(R.string.created_post_title)

                    // Загрузить черновик, если он есть
                    val draft = requireContext().getDraft()
                    if (!draft.isNullOrEmpty()) {
                        newText.setText(draft)
                        //Поставить курсор в конец
                        newText.setSelection(draft.length)
                    }
                }

                EditMode.EDIT, EditMode.REPOST -> {
                    viewModel.data.observe(viewLifecycleOwner) { posts ->
                        val post = posts.find { it.id == postId } ?: return@observe
                        currentPost = post
                        newText.setText(post.text)
                        when (editMode) {
                            EditMode.EDIT -> topAppBar.title = getString(R.string.edited_post_title)
                            EditMode.REPOST -> topAppBar.title =
                                getString(R.string.reposted_post_title)
                        }
                    }
                }
            }
        }

        AndroidUtils.showKeyboard(binding.newText)

        binding.savePost.setOnClickListener {
            val text = binding.newText.text?.toString().orEmpty()
            if (text.isBlank()) return@setOnClickListener

            when (editMode) {
                EditMode.CREATE, EditMode.EDIT -> {
                    viewModel.save(text)
                    // Очистить черновик после сохранения.
                    requireContext().clearDraft()
                    findNavController().popBackStack()
                }

                EditMode.REPOST -> {
                    currentPost?.let { post ->
                        viewModel.repost(parentId = post.id, text = text)
                        findNavController().popBackStack(R.id.feedFragment, false)
                    }
                }
            }
        }

        binding.cancelEdit.setOnClickListener {
            if (editMode == EditMode.CREATE) {
                requireContext().saveDraft(binding.newText.text.toString())
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // очистить binding в конце жизни фрагмента
    }

}