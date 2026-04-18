package ru.netology.nmedia_server.config

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.netology.nmedia_server.entity.PostEntity
import ru.netology.nmedia_server.repository.PostRepository
import java.time.OffsetDateTime

@Configuration
class InitPostsConfig{
    @Bean
fun initPosts(postRepository: PostRepository) = CommandLineRunner {
        if (postRepository.count() > 0) return@CommandLineRunner

        val now = OffsetDateTime.now().toEpochSecond()

        val posts = listOf(
            PostEntity(
                id = 0L,
                parentId = null,
                publishedDate = now,
                author = "Нетология",
                content = "Добро пожаловать в NMedia!",
                videoLink = "",
                videoDescription = "",
                videoDate = "",
                commentsCount = 0,
                likesCount = 0,
                isLiked = false,
                viewsCount = 0,
                repostsCount = 0,
            ),
            PostEntity(
                id = 0L,
                parentId = null,
                publishedDate = now - 3600,
                author = "Студент Нетологии",
                content = "Это первый пост, созданный на сервере.",
                videoLink = "",
                videoDescription = "",
                videoDate = "",
                commentsCount = 2,
                likesCount = 1,
                isLiked = false,
                viewsCount = 10,
                repostsCount = 0,
            ),
            PostEntity(
                id = 0L,
                parentId = null,
                publishedDate = now - 7200,
                author = "Студент Нетологии",
                content = "Пост с примером количества лайков и репостов.",
                videoLink = "",
                videoDescription = "",
                videoDate = "",
                commentsCount = 5,
                likesCount = 3,
                isLiked = false,
                viewsCount = 42,
                repostsCount = 1,
            ),
        )

        postRepository.saveAll(posts)
    }
}