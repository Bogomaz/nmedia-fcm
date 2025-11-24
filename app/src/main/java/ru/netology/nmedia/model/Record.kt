package ru.netology.nmedia.model

//Общий класс, от которого будут наследоваться и Post, и Note.
abstract class Record (
    open val id: Int, // Идентификатор записи
    open val fromId: Int, // Идентификатор автора, от чьего имени опубликована запись
    open val ownerId: Int, // Идентификатор владельца записи
    open val date: Int, //Время публикации записи в формате unixtime.
    open var text: String, //Текст записи
    open var viewPrivacy: Privacy,
    open var comments: Comments?, //комментарии
    open var likes: Likes?, // реакции
    open var views: Views?, // просмотры
    open var reposts: Reposts?, // репосты
    open var attachments: List<Attachment>?
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
    var count: Int = 0, //Количество комментариев к записи
    var readCommentsCount: Int = 0, //Количество прочитанных комментариев.
    var commentPrivacy: Privacy = Privacy.EVERYONE, //Уровень доступа к комментированию заметки.
    var canClose: Boolean = false, // может ли текущий пользователь закрыть комментарии к записи;
    var canOpen: Boolean = false // может ли текущий пользователь открыть комментарии к записи.
)

data class Likes(
    var count: Int = 0, // число пользователей, которым понравилась запись;
    var userLikes: Boolean = true, // наличие отметки «Мне нравится» от текущего пользователя (1 — есть, 0 — нет);
    var canLike: Boolean = true, // информация о том, может ли текущий пользователь поставить отметку «Мне нравится» (1 — может, 0 — не может);
)

data class Views(
    var count: Int // число просмотров записи.
)

data class Reposts(
    var count: Int = 0, // число пользователей, скопировавших запись;
    var canPublish: Boolean = true // информация о том, может ли текущий пользователь сделать репост записи (1 — может, 0 — не может).
)