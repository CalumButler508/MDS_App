package com.example.gamingthoughts.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GameEntryDao {
    // SELECT (list + search + filter). status == null means "All".
    @Query(
        """SELECT * FROM game_entry
           WHERE (:status IS NULL OR status = :status)
             AND title LIKE '%' || :query || '%'
           ORDER BY dateAdded DESC"""
    )
    fun observe(query: String, status: String?): Flow<List<GameEntry>>

    // SELECT (single)
    @Query("SELECT * FROM game_entry WHERE id = :id")
    suspend fun getById(id: Int): GameEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: GameEntry): Long

    @Update
    suspend fun update(entry: GameEntry)

    @Delete
    suspend fun delete(entry: GameEntry)
}
