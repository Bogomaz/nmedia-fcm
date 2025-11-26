package ru.netology.nmedia.model

data class Post(
    override val id: Int = 0,
    override val fromId: Int = 0,
    override val ownerId: Int = 0,
    override val date: Int = 0,
    override var text: String = "",
    override var viewPrivacy: Privacy = Privacy.EVERYONE,
    override var comments: Comments? = null,
    override var likes: Likes? = Likes(
        count = 129,
        userLikes = false,
        canLike = true,
    ),
    override var views: Views? = null, // просмотры
    override var reposts: Reposts? = null, // просмотры
    override var attachments: List<Attachment>? = null, // медаивложения
    val postType: PostType = PostType.POST, //Тип записи, может принимать следующие значения: post, repost, comment, reply, postpone, suggest.
    val replyOwnerId: Int = 0, //id владельца записи, в ответ на которую была оставлена текущая.
    val replyPostId: Int = 0, //id записи, в ответ на которую была оставлена текущая.
    var isLiked: Boolean = false // true, если объект добавлен в закладки у текущего пользователя

) : Record(
    id,
    fromId,
    ownerId,
    date,
    text,
    viewPrivacy,
    comments,
    likes,
    views,
    reposts,
    attachments
)


// Опциональный enum для post_type
enum class PostType {
    POST,
    REPOST,
    REPLY,
    POSTPONE,
    SUGGEST
}

