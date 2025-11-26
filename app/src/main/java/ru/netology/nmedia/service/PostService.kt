package ru.netology.nmedia.service

import ru.netology.nmedia.R
import ru.netology.nmedia.model.Comment
import ru.netology.nmedia.model.Likes
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.Reposts
import kotlin.math.round

object PostService {
    var posts = emptyArray<Post>()
    private var currentPostId = 1

    private var comments = emptyArray<Comment>()
    private var currentCommentId = 1

    //Принимает объект Post
    //Добавляет пост
    //Возвращает только что добавленный пост
    fun add(post: Post): Post {
        val newPost = post.copy(id = currentPostId++)
        posts += newPost
        return posts.last();
    }

    //Принимает id поста
    //Возвращает запрошенный пост, или генерит исключение
    fun getById(postId: Int): Post {
        val post = posts.firstOrNull() { it.id == postId }
        if (post == null) {
            throw RuntimeException("The post $postId doesn't exist")
        }
        return post;
    }

    //Принимает объект Post
    //Находит в массиве запись с тем же id, что и у post и обновляет все свойства;
    //Если пост с таким id не найден, то ничего не происходит и возвращается false, в противном случае – возвращается true.
    fun update(post: Post): Boolean {
        for ((index, currentPost) in posts.withIndex()) {
            if (currentPost.id == post.id) {
                posts[index] = post.copy(id = currentPost.id)
                println(posts[index])
                return true
            }
        }
        return false
    }

    fun clear() {
        posts = emptyArray()
        currentPostId = 1
    }

    // Принимает id пользователя и id поста.
    // Cтавит отметку о том, что данный пользователь поставил/снял лайк
    // Возвращает новое количество лайков
    fun likeHandler(userId: Int, postId: Int): Post{
        val post = getById(postId)
        val updatedLikes = post.likes?.let{
            Likes(
                userLikes = !it.userLikes,
                count = if (it.userLikes) --it.count else ++it.count
            )
        }
        val updatedPost = post.copy(likes = updatedLikes)
        update(updatedPost)

        return updatedPost
    }

    // Принимает id пользователя и id поста.
    // Cтавит отметку о том, что данный пользователь поставил/снял лайк
    // Возвращает новое количество лайков
    fun repostHandler(postId: Int): Post {
        val post = getById(postId)
        val updatedReposts = post.reposts?.let{
            Reposts(
                count = ++it.count
            )
        }
        val updatedPost = post.copy(reposts = updatedReposts)
        return updatedPost
    }

    //Принимает число с количеством взаимодействий с постом
    //Возвращает строку, пригодную для вывода.
    fun convertNumberIntoText(number: Int): String {
        return when {
            number >= 1_000_000 -> formatNumberWithSuffix(number / 1_000_000.0, "M")
            number >= 10_000 -> "${number / 1_000} K"
            number >= 1_000 -> formatNumberWithSuffix(number / 1_000.0, "K")
            else -> number.toString()
        }
    }

    // Принимает число и суффикс, указывающий на размерность числа
    // Возвращает строку с нужным числом знаков после запятой и заданным суффиксом
    private fun formatNumberWithSuffix(value: Double, suffix: String): String {
        // Отрезать лишние цифры без округления
        val truncated = (value * 10).toInt() / 10.0
        val formatted = "%.1f".format(truncated).replace(",", ".")

        // Убрать .0, если число целое
        return if (formatted.endsWith(".0")) {
            formatted.dropLast(2) + " $suffix"
        } else {
            "$formatted $suffix"
        }
    }
}