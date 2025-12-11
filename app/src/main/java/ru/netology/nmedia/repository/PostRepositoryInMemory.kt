package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.service.PostService


class PostRepositoryInMemory : PostRepository {
    // пользователь-заглушка
    val userId = 1

    // набор постов-заглушек
    private val posts = PostService.addPostList(
        listOf(
            Post(
                date = 1758622320, //"23 сентября в 10:12"
                author = "Нетология. Университет интернет-профессий будущего",
                text = "Освоение новой профессии — это не только открывающиеся возможности и перспективы, но и настоящий вызов самому себе. Приходится выходить из зоны комфорта и перестраивать привычный образ жизни: менять распорядок дня, искать время для занятий, быть готовым к возможным неудачам в начале пути. В блоге рассказали, как избежать стресса на курсах профпереподготовки → http://netolo.gy/fPD",
                video = "https://rutube.ru/video/8ec3bce92ea95b3340af7511e533cbc0/?r=plwd",
                commentsCount = 94,
                likesCount = 90,
                viewsCount = 362,
                repostsCount = 1
            ),
            Post(
                date = 1758552328, //"22 сентября в 14:45",
                author = "Нетология. Университет интернет-профессий будущего",
                text = "Делиться впечатлениями о любимых фильмах легко, а что если рассказать так, чтобы все заскучали",
                commentsCount = 944,
                likesCount = 80,
            ),
            Post(
                date = 1758535920, // "22 сентября в 10:12",
                author = "Нетология. Университет интернет-профессий будущего",
                text = "Таймбоксинг — отличный способ навести порядок в своём календаре и разобраться с делами, которые долго откладывали на потом. Его главный принцип — на каждое дело заранее выделяется определённый отрезок времени. В это время вы работаете только над одной задачей, не переключаясь на другие. Собрали советы, которые помогут внедрить таймбоксинг \uD83D\uDC47\uD83C\uDFFB",
                video = "https://rutube.ru/video/8ec3bce92ea95b3340af7511e533cbc0/?r=plwd",
                commentsCount = 944,
                likesCount = 70,
                viewsCount = 52100,
                repostsCount = 54
            ),
            Post(
                date = 1758449520, //"21 сентября в 10:12"
                author = "Нетология. Университет интернет-профессий будущего",
                text = "24 сентября стартует новый поток бесплатного курса «Диджитал-старт: первый шаг к востребованной профессии» — за две недели вы попробуете себя в разных профессиях и определите, что подходит именно вам → http://netolo.gy/fQ",
                video = "https://rutube.ru/video/8ec3bce92ea95b3340af7511e533cbc0/?r=plwd",
                commentsCount = 41,
                likesCount = 60,
                viewsCount = 365,
                repostsCount = 540
            ),
            Post(
                date = 1758363240, // 20 сентября в 10:14,
                author = "Нетология. Университет интернет-профессий будущего",
                text = "Диджитал давно стал частью нашей жизни: мы общаемся в социальных сетях и мессенджерах, заказываем еду, такси и оплачиваем счета через приложения.",
                commentsCount = 25,
                likesCount = 50,
                viewsCount = 996,
                repostsCount = 54
            ),
            Post(
                date = 1758291120, // 19 сентября в 14:12,
                author = "Нетология. Университет интернет-профессий будущего",
                text = "Большая афиша мероприятий осени: конференции, выставки и хакатоны для жителей Москвы, Ульяновска и Новосибирска",
                commentsCount = 28,
                likesCount = 40,
                viewsCount = 3102000,
                repostsCount = 2500
            ),
            Post(
                date = 1758277440, // 19 сентября в 10:24",
                author = "Нетология. Университет интернет-профессий будущего",
                text = "Языков программирования много, и выбрать какой-то один бывает нелегко. Собрали подборку статей, которая поможет вам начать, если вы остановили свой выбор на JavaScript.",
                commentsCount = 4,
                likesCount = 30,
                viewsCount = 1102,
                repostsCount = 800
            ),
            Post(
                date = 1758190320, // 18 сентября в 10:12",
                author = "Нетология. Университет интернет-профессий будущего",
                text = "Знаний хватит на всех: на следующей неделе разбираемся с разработкой мобильных приложений, учимся рассказывать истории и составлять PR-стратегию прямо на бесплатных занятиях",
                video = "https://rutube.ru/video/8ec3bce92ea95b3340af7511e533cbc0/?r=plwd",
                commentsCount = 4,
                likesCount = 20,
                viewsCount = 41,
                repostsCount = 6
            ),
            Post(
                date = 1747841760, //"21 мая в 18:36"
                author = "Нетология. Университет интернет-профессий будущего",
                text = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
                video = "invalid_link",
                commentsCount = 12,
                likesCount = 10,
                viewsCount = 30,
                repostsCount = 1
            ),
        )
    )

    // Готовим данные для LiveData (пока они берутся из заглушки)
    private val data = MutableLiveData(posts)
    override fun getAll(): LiveData<List<Post>> = data

    // Принимает идентификатор лайкнутого поста.
    // Берёт свежий список постов из LiveData
    // Обращается к PostService.likeHandler за новым состоянием лайков
    // Изменяет LiveData
    override fun likeById(id: Int) {
        var currentPostsSet = data.value ?: return
        currentPostsSet = currentPostsSet.map {
            if (it.id != id) it
            else
                PostService.likeHandler(it.id)
        }
        data.value = currentPostsSet
    }

    // Получает список постов из LiveData
    // Обращается к PostService.repostHandler за новым состоянием репостов
    // Изменяет LiveData.
    override fun repostById(id: Int) {
        var currentPostsSet = data.value ?: return
        currentPostsSet = currentPostsSet.map {
            if (it.id != id) it
            else
                PostService.repostHandler(it.id)
        }
        data.value = currentPostsSet
    }

    // Получает список постов из LiveData
    // Передаёт в PostService введённый post, чтобы его добавили в хранилище постов
    // Обновляет LiveData так, чтобы добавленный пост выводился первым в сппике.
    override fun save(post: Post) {
        data.value = if (post.id == 0) {
            listOf(PostService.addOnePost(post)) + (data.value ?: emptyList())
        } else {
            (data.value ?: emptyList()).map {
                if (it.id == post.id) PostService.update(post) else it
            }
        }
    }

    // Получает список постов из LiveData
    // Обращается к PostService.removeById, чтобы удалить пост из коллекции
    // Обновляет LiveData
    override fun removeById(id: Int) {
        var currentPostsSet = data.value ?: return
        PostService.removeById(id)
        data.value = currentPostsSet.filter { it.id != id }
    }
}
