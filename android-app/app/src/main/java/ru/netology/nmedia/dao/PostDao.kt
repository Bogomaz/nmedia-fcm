package ru.netology.nmedia.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.entity.PostEntity
import ru.netology.nmedia.entity.toEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity WHERE isVisible = 1 ORDER BY localId DESC")
    fun getAll(): Flow<List<PostEntity>>

    @Query("SELECT COUNT(*) == 0 FROM PostEntity")
    suspend fun isEmpty(): Boolean

    // количество "новых" постов (visible=0)
    @Query("SELECT COUNT(*) FROM PostEntity WHERE isVisible = 0")
    fun getHiddenCount(): Flow<Int>

    // пометить все скрытые как видимые
    @Query("UPDATE PostEntity SET isVisible = 1 WHERE isVisible = 0")
    suspend fun showAllHidden()

    @Query("SELECT MAX(serverId) FROM PostEntity")
    suspend fun getMaxServerId(): Long?

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