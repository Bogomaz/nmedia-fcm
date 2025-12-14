package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.nmedia.initPosts
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.service.PostService
import java.lang.reflect.Type
import kotlin.inc


class PostRepositoryInMemory(
    private val context: Context
) : PostRepository {


    //Загружаем в список данные из shared pref.
    //Сеттер обновляет shared pref и live data
    private var posts: List<Post> = getOrInitPosts()
        set(value) {
            field = value
            sync() // обновляем shared pref
            postsLive.value = value // обновляем live data
        }

    // Загружаем в live data список постов
    private val postsLive = MutableLiveData(posts)

    override fun getAll(): LiveData<List<Post>> = postsLive

    // Создаёт новый пост, добавляет его в live data, вызывает обновление shared pref.
    override fun save(post: Post) {
        posts = if (post.id == 0) {
//            val currentId = posts.maxOfOrNull { it.id } ?: 0
            val newPost = PostService.createPost(post, getCurrentId())
            listOf(newPost) + posts
        } else {
            posts.map {
                if (it.id == post.id) post else it
            }
        }
    }

    // Принимает идентификатор поста. Берёт свежий список постов из LiveData
    // Обращается к PostService.likeHandler за новым состоянием лайков
    // Изменяет LiveData
    override fun likeById(id: Int) {
        posts = posts.map{
            if(it.id != id) it
            else
                PostService.likeHandler(it)
        }
    }

    // Принимает идентификатор поста.  Берёт свежий список постов из LiveData
    // Обращается к PostService.repostHandler за новым состоянием репостов
    // Изменяет LiveData.
    override fun repostById(id: Int) {
        posts = posts.map {
            if (it.id != id) it
            else
                PostService.repostHandler(it)
        }
    }

    // Удаляет пост из live data и вызывает обновление shared pref.
    override fun removeById(id: Int) {
        posts = posts.filter { it.id != id }
    }

    // Чтение данных из файла
    private fun getOrInitPosts(): List<Post>{
        val file = context.filesDir.resolve(FILE_NAME)
        return if(!file.exists()||file.length() == 0L){
            file.outputStream().bufferedWriter().use{
                it.write(gson.toJson(initPosts))
            }
            initPosts
        }else{
            file.inputStream()
                .bufferedReader()
                .use {
                    gson.fromJson(it, postsType)
                }
        }
    }

    // запись в файл изменённого списка постов
    private fun sync() {
        context.filesDir.resolve(FILE_NAME)
            .outputStream()
            .bufferedWriter()
            .use{
                it.write(gson.toJson(posts))
            }
    }

    private fun getCurrentId() = posts.maxOfOrNull { it.id } ?: 0

    // Константы
    private companion object {
        const val FILE_NAME = "posts.json"
        val gson = Gson() // Gson-объект
        val postsType: Type =
            object : TypeToken<List<Post>>() {}.type // тип объекта - список постов
    }
}
