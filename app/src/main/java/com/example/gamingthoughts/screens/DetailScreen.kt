package com.example.gamingthoughts.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.gamingthoughts.R
import com.example.gamingthoughts.data.GameEntry
import com.example.gamingthoughts.data.GameStatus
import com.example.gamingthoughts.viewmodel.GameViewModel
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(vm: GameViewModel, id: Int, onBack: () -> Unit, onEdit: () -> Unit) {
    var game by remember { mutableStateOf<GameEntry?>(null) }
    var confirm by remember { mutableStateOf(false) }
    LaunchedEffect(id) { game = vm.get(id) }
    val g = game ?: return

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(g.title) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                }
            }
        )
    }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AsyncImage(
                model = g.coverUrl, contentDescription = stringResource(R.string.cover_art, g.title),
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(200.dp)
            )
            Text(g.title, style = MaterialTheme.typography.headlineSmall)
            g.releaseYear?.let { Text(stringResource(R.string.released, it)) }
            Text(stringResource(R.string.rating_value, g.rating))
            Text(stringResource(GameStatus.valueOf(g.status).label))
            Text(stringResource(R.string.added_on, DateFormat.getDateInstance().format(Date(g.dateAdded))))
            Text(g.thoughts, style = MaterialTheme.typography.bodyLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEdit, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.edit)) }
                Button(onClick = { confirm = true }, modifier = Modifier.heightIn(min = 48.dp)) { Text(stringResource(R.string.delete)) }
            }
        }
    }

    if (confirm) AlertDialog(
        onDismissRequest = { confirm = false },
        title = { Text(stringResource(R.string.delete_confirm_title)) },
        text = { Text(stringResource(R.string.delete_confirm_body, g.title)) },
        confirmButton = { TextButton(onClick = { vm.delete(g, onBack) }) { Text(stringResource(R.string.delete)) } },
        dismissButton = { TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.cancel)) } }
    )
}
