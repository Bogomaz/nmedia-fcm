package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.Comments
import ru.netology.nmedia.model.Likes
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.PostType
import ru.netology.nmedia.model.Privacy
import ru.netology.nmedia.model.Reposts
import ru.netology.nmedia.model.Views
import ru.netology.nmedia.service.PostService


class PostRepositoryInMemoy : PostRepository {
    val userId = 1
    val post = PostService.add(
        Post(
            fromId = 1,
            ownerId = 1,
            date = 1747841760, //21 мая в 18:36
            text = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. " +
                    "Затем появились курсы по дизайну, разработке, аналитике и управлению. " +
                    "Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. " +
                    "Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. " +
                    "Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            viewPrivacy = Privacy.EVERYONE,
            comments = Comments(
                count = 155, //Количество комментариев к записи
                readCommentsCount = 0, //Количество прочитанных комментариев.
                commentPrivacy = Privacy.EVERYONE, //Уровень доступа к комментированию заметки.
                canClose = false, // может ли текущий пользователь закрыть комментарии к записи;
                canOpen = false // может ли текущий пользователь открыть комментарии к записи.

            ),
            likes = Likes(
                count = 996, // число пользователей, которым понравилась запись;
                userLikes = false, // наличие отметки «Мне нравится» от текущего пользователя (1 — есть, 0 — нет);
                canLike = true, // информация о том, может ли текущий пользователь поставить отметку «Мне нравится» (1 — может, 0 — не может);
            ),
            views = Views(
                count = 1_495 // число просмотров записи.
            ),
            reposts = Reposts(
                count = 998, // число пользователей, скопировавших запись;
                canPublish = true // информация о том, может ли текущий пользователь сделать репост записи (1 — может, 0 — не может).
            ),
            attachments = null,
            postType = PostType.POST,
            replyOwnerId = 0,
            replyPostId = 0,
            isLiked = false,
        )
    )

    private val data = MutableLiveData<Post>(post)

    override fun get(): LiveData<Post> = data

    override fun like() {
        val currentPost = data.value ?: return
        data.value = PostService.likeHandler(userId, currentPost.id)
    }

    override fun repost() {
        val currentPost = data.value ?: return
        data.value = PostService.repostHandler(currentPost.id)
    }
}
