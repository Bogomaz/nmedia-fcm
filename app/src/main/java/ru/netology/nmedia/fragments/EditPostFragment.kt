package ru.netology.nmedia.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentEditPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.utils.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel

class EditPostFragment : Fragment() {

    private var _binding: FragmentEditPostBinding? = null // Nullable-ссылка на binding
    private val binding get() = _binding!! // кастомный геттер, который точно возвращает не null
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        Здесь получение данных происходит через viewModel
//        val postsText = viewModel.edited.value?.text.orEmpty()
//        if (postsText.isNotBlank()) {
//            binding.newText.setText(postsText)
//            binding.topAppBar.title = getString(R.string.edited_post_title)
//            AndroidUtils.showKeyboard(binding.newText)
//        } else {
//            binding.topAppBar.title = getString(R.string.created_post_title)
//        }

        val args = EditPostFragmentArgs.fromBundle(requireArguments())
        val postText = args.postText

        if (postText.isNotBlank()) {
            binding.newText.setText(postText)
            binding.topAppBar.title = getString(R.string.edited_post_title)
            AndroidUtils.showKeyboard(binding.newText)
        } else {
            binding.topAppBar.title = getString(R.string.created_post_title)
        }


        binding.savePost.setOnClickListener {
            val text = binding.newText.text?.toString().orEmpty()
            if (text.isNotBlank()) {
                viewModel.save(text)
                findNavController().navigateUp()
            }
        }

        binding.cancelEdit.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // очистить binding в конце жизни фрагмента
    }

    companion object {
        private const val ARG_POST_TEXT = "postText"

        fun newInstance(
            postText: String
        ): EditPostFragment {

            return EditPostFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_POST_TEXT, postText)
                }
            }
        }
    }
}