package com.example.gamingthoughts.data

import com.example.gamingthoughts.BuildConfig
import kotlinx.coroutines.flow.Flow

/** Combines Room (offline storage) with the RAWG API (auto-fill). */
class GameRepository(
    private val dao: GameEntryDao,
    private val api: GameApiService = GameApiService.instance
) {
    fun observe(query: String, status: String?): Flow<List<GameEntry>> = dao.observe(query, status)
    suspend fun get(id: Int) = dao.getById(id)
    suspend fun save(entry: GameEntry) = if (entry.id == 0) dao.insert(entry).let { } else dao.update(entry)
    suspend fun delete(entry: GameEntry) = dao.delete(entry)
    suspend fun restore(entry: GameEntry) { dao.insert(entry) }   // used by Undo

    /** Returns null if nothing found or the network fails (app keeps working offline). */
    suspend fun lookup(title: String): RawgGame? = try {
        api.search(title, apiKey = BuildConfig.RAWG_API_KEY).results.firstOrNull()
    } catch (e: Exception) {
        null
    }
}
