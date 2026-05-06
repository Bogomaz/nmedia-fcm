package ru.netology.nmedia.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import ru.netology.nmedia.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY localId DESC")
    fun getAll(): LiveData<List<PostEntity>>

    @Query("SELECT * FROM PostEntity WHERE localId = :localId")
    suspend fun getByLocalId(localId: Long): PostEntity?

    @Query("SELECT * FROM PostEntity WHERE serverId = :serverId LIMIT 1")
    suspend fun getByServerId(serverId: Long): PostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<PostEntity>)

    @Update
    suspend fun update(post: PostEntity)
    suspend fun save(post: PostEntity): Long =
        if(post.localId == 0L){
            insert(post)
        }else{
            update(post)
            post.localId
        }

//    suspend fun updateContentById(id: Long, text: String)
//    suspend fun save(post: PostEntity) =
//        if (post.id == 0L) insert(post) else updateContentById(post.id, post.text)

    @Query("""
        UPDATE PostEntity SET 
            likesCount = likesCount + CASE WHEN isLiked THEN -1 ELSE 1 END,
            isLiked = CASE WHEN isLiked THEN 0 ELSE 1 END
        WHERE localId = :localId""")
    suspend fun likeByLocalId(localId: Long)

    @Query("DELETE FROM PostEntity WHERE localId = :localId")
    suspend fun removeByLocalId(localId: Long)

    @Query(
        """
        UPDATE PostEntity
        SET repostsCount = repostsCount + 1
        WHERE localId = :localId
        """
    )
    suspend fun incrementRepostsCount(localId: Long)}