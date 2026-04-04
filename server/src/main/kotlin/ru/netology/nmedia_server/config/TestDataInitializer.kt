package ru.netology.nmedia_server.config


import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import ru.netology.nmedia_server.dto.Post
import ru.netology.nmedia_server.service.PostService
import kotlin.collections.forEach
import kotlin.collections.isNotEmpty

@Component
class TestDataInitializer(
    private val service: PostService,
) {
    @PostConstruct
    fun init() {
        // Если уже есть посты — ничего не делаем
        if (service.getAll().isNotEmpty()) return

        val posts = listOf(
            Post(
                id = 0,
                author = "Нетология. Университет интернет-профессий будущего",
                content = "Освоение новой профессии — это не только открывающиеся возможности и перспективы, но и настоящий вызов самому себе. Приходится выходить из зоны комфорта и перестраивать привычный образ жизни: менять распорядок дня, искать время для занятий, быть готовым к возможным неудачам в начале пути. В блоге рассказали, как избежать стресса на курсах профпереподготовки → http://netolo.gy/fPD",
                published = 1758622320, //"23 сентября в 10:12"
                likedByMe = false,
                likes = 0,
            ),
            Post(
                id = 0,
                author = "Нетология. Университет интернет-профессий будущего",
                "Делиться впечатлениями о любимых фильмах легко, а что если рассказать так, чтобы все заскучали",
                published = 1758552328, //"22 сентября в 14:45",
                likedByMe = false,
                likes = 3,
            ),
            Post(
                id = 0,
                author = "Нетология. Университет интернет-профессий будущего",
                content = "Таймбоксинг — отличный способ навести порядок в своём календаре и разобраться с делами, которые долго откладывали на потом. Его главный принцип — на каждое дело заранее выделяется определённый отрезок времени. В это время вы работаете только над одной задачей, не переключаясь на другие. Собрали советы, которые помогут внедрить таймбоксинг \uD83D\uDC47\uD83C\uDFFB",
                published = 1758535920, // "22 сентября в 10:12",
                likedByMe = false,
                likes = 10,
            ),
            Post(
                id = 0,
                author = "Светлана",
                content = "Привет всем!",
                published = 0, // "22 сентября в 10:12",
                likedByMe = false,
                likes = 10,
            )
        )

        posts.forEach(service::save)
    }
}