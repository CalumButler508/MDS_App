package com.example.gamingthoughts.data

import androidx.annotation.StringRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.gamingthoughts.R

/** One row = one game + the user's thoughts on it (matches the ER diagram). */
@Entity(tableName = "game_entry")
data class GameEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,   // Game_ID
    val title: String,
    val thoughts: String = "",
    val rating: Int = 3,                                 // 1..5
    val status: String = GameStatus.PLAYING.name,        // stored as enum name, shown via strings.xml
    val dateAdded: Long = System.currentTimeMillis(),
    val releaseYear: Int? = null,
    val coverUrl: String? = null
)

/** Stored by name so the DB stays language-independent; label comes from strings.xml (EN/GA). */
enum class GameStatus(@StringRes val label: Int) {
    PLAYING(R.string.status_playing),
    COMPLETED(R.string.status_completed),
    DROPPED(R.string.status_dropped),
    WISHLIST(R.string.status_wishlist)
}
