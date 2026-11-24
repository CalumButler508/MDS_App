package com.example.gamingthoughts.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.gamingthoughts.R
import com.example.gamingthoughts.data.GameEntry
import com.example.gamingthoughts.data.GameStatus
import com.example.gamingthoughts.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditScreen(vm: GameViewModel, id: Int, onDone: () -> Unit) {
    var existing by remember { mutableStateOf<GameEntry?>(null) }
    var title by remember { mutableStateOf("") }
    var thoughts by remember { mutableStateOf("") }
    var rating by remember { mutableFloatStateOf(3f) }
    var status by remember { mutableStateOf(GameStatus.PLAYING) }
    var year by remember { mutableStateOf<Int?>(null) }
    var cover by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf(false) }
    var menuOpen by remember { mutableStateOf(false) }
    var searching by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val notFound = stringResource(R.string.lookup_failed)

    // Load when editing (id == -1 means "new")
    LaunchedEffect(id) {
        if (id != -1) vm.get(id)?.let {
            existing = it; title = it.title; thoughts = it.thoughts; rating = it.rating.toFloat()
            status = GameStatus.valueOf(it.status); year = it.releaseYear; cover = it.coverUrl
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(if (id == -1) R.string.add_thought else R.string.edit_thought)) }) },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = title, onValueChange = { title = it; titleError = false },
                label = { Text(stringResource(R.string.title)) }, singleLine = true, isError = titleError,
                supportingText = { if (titleError) Text(stringResource(R.string.title_required)) },
                trailingIcon = {
                    IconButton(enabled = title.isNotBlank() && !searching, onClick = {
                        searching = true
                        scope.launch {
                            val game = vm.lookup(title)           // Retrofit -> RAWG
                            if (game != null) {
                                title = game.name; year = game.released?.take(4)?.toIntOrNull(); cover = game.backgroundImage
                            } else snackbar.showSnackbar(notFound)
                            searching = false
                        }
                    }) { Icon(Icons.Default.Search, contentDescription = stringResource(R.string.search_online)) }
                },
                modifier = Modifier.fillMaxWidth()
            )
            year?.let { Text(stringResource(R.string.released, it)) }

            OutlinedTextField(
                value = thoughts, onValueChange = { thoughts = it },
                label = { Text(stringResource(R.string.thoughts)) }, minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Text(stringResource(R.string.rating_value, rating.toInt()))
            Slider(value = rating, onValueChange = { rating = it }, valueRange = 1f..5f, steps = 3)

            ExposedDropdownMenuBox(expanded = menuOpen, onExpandedChange = { menuOpen = it }) {
                OutlinedTextField(
                    value = stringResource(status.label), onValueChange = {}, readOnly = true,
                    label = { Text(stringResource(R.string.status)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(menuOpen) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }) {
                    GameStatus.entries.forEach { s ->
                        DropdownMenuItem(text = { Text(stringResource(s.label)) }, onClick = { status = s; menuOpen = false })
                    }
                }
            }

            Button(
                onClick = {
                    if (title.isBlank()) { titleError = true; return@Button }   // validation
                    val entry = (existing ?: GameEntry(title = title)).copy(
                        title = title.trim(), thoughts = thoughts, rating = rating.toInt(),
                        status = status.name, releaseYear = year, coverUrl = cover
                    )
                    vm.save(entry, onDone)
                },
                modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
            ) { Text(stringResource(R.string.save)) }
        }
    }
}
