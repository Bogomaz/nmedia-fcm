package ru.netology.nmedia.model

data class Comment (
    val id: Int = 0, //Идентификатор комментария
    val parentId: Int = 0, // Идентификатор родительской записи: поста или заметки
    val parentType: ParentType = ParentType.POST, //Тип родительской записи: пост, заметка.
    val isDeleted: Boolean = false,
    val fromId: Int = 0, //Идентификатор автора комментария
    val date: Int = 0, //Дата создания комментария в формате Unixtime
    val text: String = "", //Текст комментария
    val replyToUser: Int = 0, //Идентификатор пользователя или сообщества, в ответ которому оставлен текущий комментарий (если применимо).
    val replyToComment: Int = 0, // Идентификатор комментария, в ответ на который оставлен текущий (если применимо).
    val attachments: List<Attachment>? = null // Медиавложения комментария (фотографии, ссылки и т.п.). Описание массива attachments находится на отдельной странице.
)
enum class ParentType {
    POST,
    NOTE
}