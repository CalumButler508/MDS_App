package com.example.gamingthoughts.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.gamingthoughts.R
import com.example.gamingthoughts.data.GameStatus
import com.example.gamingthoughts.viewmodel.GameViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: GameViewModel, onAdd: () -> Unit, onOpen: (Int) -> Unit) {
    val games by vm.games.collectAsStateWithLifecycle()
    val query by vm.query.collectAsStateWithLifecycle()
    val filter by vm.statusFilter.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val deletedMsg = stringResource(R.string.deleted)
    val undoLabel = stringResource(R.string.undo)

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.app_name)) }) },
        snackbarHost = { SnackbarHost(snackbar) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_thought))
            }
        },
        bottomBar = {
            // Filter chips: All + each status
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = filter == null, onClick = { vm.statusFilter.value = null },
                    label = { Text(stringResource(R.string.status_all)) })
                GameStatus.entries.forEach { s ->
                    FilterChip(selected = filter == s, onClick = { vm.statusFilter.value = s },
                        label = { Text(stringResource(s.label)) })
                }
            }
        }
    ) { pad ->
        Column(Modifier.padding(pad)) {
            OutlinedTextField(
                value = query, onValueChange = { vm.query.value = it },
                label = { Text(stringResource(R.string.search)) }, singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            )
            if (games.isEmpty()) {
                Column(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                    Icon(painterResource(R.drawable.ic_controller), contentDescription = null, modifier = Modifier.size(64.dp))
                    Text(stringResource(R.string.empty_state))
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(games, key = { it.id }) { game ->
                        // Extra UI feature: swipe-to-delete with undo snackbar
                        val dismiss = rememberSwipeToDismissBoxState(confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                vm.delete(game)
                                scope.launch {
                                    val r = snackbar.showSnackbar(deletedMsg, undoLabel, duration = SnackbarDuration.Short)
                                    if (r == SnackbarResult.ActionPerformed) vm.restore(game)
                                }
                                true
                            } else false
                        })
                        SwipeToDismissBox(dismiss, enableDismissFromStartToEnd = false, backgroundContent = {}) {
                            ElevatedCard(Modifier.fillMaxWidth().clickable { onOpen(game.id) }) {
                                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                    if (!game.coverUrl.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = game.coverUrl,
                                            contentDescription = game.title,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp))
                                        )
                                        Spacer(Modifier.width(12.dp))
                                    }
                                    Column(Modifier.weight(1f)) {
                                        Text(game.title, style = MaterialTheme.typography.titleMedium)
                                        Text(stringResource(GameStatus.valueOf(game.status).label),
                                            style = MaterialTheme.typography.bodySmall)
                                    }
                                    Icon(painterResource(R.drawable.ic_star), contentDescription = null)
                                    Text("${game.rating}/5")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
