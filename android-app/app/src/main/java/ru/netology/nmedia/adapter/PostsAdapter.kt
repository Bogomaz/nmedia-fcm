package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.service.DateTimeService
import ru.netology.nmedia.service.ConvertNumberService
import ru.netology.nmedia.interfaces.PostListener

class PostsAdapter(
    private val listener: PostListener,
) : ListAdapter<Post, PostViewHolder>(
    PostDiffUtils
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val view = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(view, listener)
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int
    ) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: PostListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            avatar.setImageResource(R.drawable.avatar)
            published.text = DateTimeService.formatUnixTime(post.date)
            content.text = post.text
            if (post.videoLink != "") {
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

            content.setOnClickListener {
                listener.onViewPost(post)
            }

            likes.setOnClickListener {
                listener.onLike(post)
            }

            repost.setOnClickListener {
                listener.onRepost(post)
            }

            video.setOnClickListener {
                listener.onPlayVideo(post)
            }

            playButton.setOnClickListener {
                listener.onPlayVideo(post)
            }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                listener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                    show()
                }
            }
        }
    }
}

object PostDiffUtils : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ) = oldItem == newItem

}