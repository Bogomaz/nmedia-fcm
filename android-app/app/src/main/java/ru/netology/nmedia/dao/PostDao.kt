package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    fun getById(id: Int): PostEntity?

    @Insert
    fun insert(post: PostEntity)

    @Query("UPDATE PostEntity SET text = :text WHERE id = :id")
    fun updateContentById(id: Int, text: String)
    fun save(post: PostEntity) =
        if (post.id == 0) insert(post) else updateContentById(post.id, post.text)

    @Query(
        """
           UPDATE PostEntity SET
               likesCount = likesCount + CASE WHEN isLiked THEN -1 ELSE 1 END,
               isLiked = CASE WHEN isLiked THEN 0 ELSE 1 END
               WHERE id = :id;
        """
    )
    fun likeById(id: Int)

    @Query("DELETE FROM PostEntity WHERE id = :id")
    fun removeById(id: Int)

    @Query(
        """
        UPDATE PostEntity
        SET repostsCount = repostsCount + 1
        WHERE id = :id
        """
    )
    fun incrementRepostsCount(id: Int)}