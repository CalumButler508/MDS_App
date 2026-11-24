package com.example.gamingthoughts.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gamingthoughts.data.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = GameRepository(AppDatabase.get(app).gameEntryDao())

    val query = MutableStateFlow("")
    val statusFilter = MutableStateFlow<GameStatus?>(null)   // null = All

    val games: StateFlow<List<GameEntry>> =
        combine(query, statusFilter) { q, s -> q to s }
            .flatMapLatest { (q, s) -> repo.observe(q, s?.name) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    suspend fun get(id: Int) = repo.get(id)
    fun save(entry: GameEntry, onDone: () -> Unit = {}) = viewModelScope.launch { repo.save(entry); onDone() }
    fun delete(entry: GameEntry, onDone: () -> Unit = {}) = viewModelScope.launch { repo.delete(entry); onDone() }
    fun restore(entry: GameEntry) = viewModelScope.launch { repo.restore(entry) }
    suspend fun lookup(title: String) = repo.lookup(title)
}
