package ru.netology.nmedia.model

//Общий класс, от которого будут наследоваться и Post, и Note.
abstract class Record (
    open val id: Int, // Идентификатор записи
    open val fromId: Int, // Идентификатор автора, от чьего имени опубликована запись
    open val ownerId: Int, // Идентификатор владельца записи
    open val date: Int, //Время публикации записи в формате unixtime.
    open val text: String, //Текст записи
    open val viewPrivacy: Privacy,
    open val comments: Comments?, //комментарии
    open val likes: Likes?, // реакции
    open val views: Views?, // просмотры
    open val reposts: Reposts?, // репосты
    open val attachments: List<Attachment>?
){

}

enum class Privacy{
    EVERYONE, //все пользователи и сообщества,
    HUMANS_ONLY, // Только пользователи, но не сообщества
    FRIENDS_ONLY, //только друзья
    FRIENDS_OF_FRIENDS, //друзья и друзья друзей,
    USER_ONLY //только пользователь, от чьего имени опубликована запись.
}

data class Comments(
    val count: Int = 0, //Количество комментариев к записи
    val readCommentsCount: Int = 0, //Количество прочитанных комментариев.
    val commentPrivacy: Privacy = Privacy.EVERYONE, //Уровень доступа к комментированию заметки.
    val canClose: Boolean = false, // может ли текущий пользователь закрыть комментарии к записи;
    val canOpen: Boolean = false // может ли текущий пользователь открыть комментарии к записи.
)

data class Likes(
    val count: Int = 0, // число пользователей, которым понравилась запись;
    val userLikes: Boolean = true, // наличие отметки «Мне нравится» от текущего пользователя (1 — есть, 0 — нет);
    val canLike: Boolean = true, // информация о том, может ли текущий пользователь поставить отметку «Мне нравится» (1 — может, 0 — не может);
)

data class Views(
    val count: Int // число просмотров записи.
)

data class Reposts(
    val count: Int = 0, // число пользователей, скопировавших запись;
    val canPublish: Boolean = true // информация о том, может ли текущий пользователь сделать репост записи (1 — может, 0 — не может).
)