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
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.service.DateTimeService
import ru.netology.nmedia.service.ConvertNumberService
import ru.netology.nmedia.interfaces.PostListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.nmedia.api.avatarUrl
import ru.netology.nmedia.api.imageUrl
import ru.netology.nmedia.utils.AvatarUtils

class PostsAdapter(
    private val listener: PostListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffUtils) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val binding =
            CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, listener)
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

    fun bind(post: Post) = with(binding) {
        // Автор и аватар
        author.text = post.author

        val avatarName = post.authorAvatar ?: "noname.png"
        val url = avatarUrl(avatarName)
        android.util.Log.d("AVATAR", "postId=${post.id}, url=$url")
        Glide.with(avatar)
            .load(avatarUrl(avatarName))
            .timeout(15_000)
            .transform(CircleCrop())
            .into(avatar)

        // Дата и текст
        published.text = DateTimeService.formatUnixTime(post.publishedDate)
        content.text = post.text

        // Вложение (картинка)
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

        // Лайки/репосты/прочее
        likes.isChecked = post.isLiked
        likes.text = ConvertNumberService.convertNumberIntoText(post.likesCount)
        repost.text = ConvertNumberService.convertNumberIntoText(post.repostsCount)
        comments.text = ConvertNumberService.convertNumberIntoText(post.commentsCount)
        views.text = ConvertNumberService.convertNumberIntoText(post.viewsCount)

        // Обработчики
        content.setOnClickListener {
            listener.onViewPost(post)
        }

        likes.setOnClickListener {
            listener.onLike(post)
        }

        repost.setOnClickListener {
            listener.onRepost(post)
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
