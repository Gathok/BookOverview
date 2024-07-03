@file:OptIn(ExperimentalMaterial3Api::class)

package de.gathok.bookoverview.settings.trash

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.settings.SettingsEvent
import de.gathok.bookoverview.settings.SettingsState
import de.gathok.bookoverview.ui.customIconDeleteForever
import de.gathok.bookoverview.ui.customIconRestoreFromTrash

@Composable
fun TrashScreen(navController: NavController, state: SettingsState, onEvent: (SettingsEvent) -> Unit) {

    var currentBook: Book? = null
    var showDialog by remember { mutableStateOf(false) }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.trash))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
    ) { pad ->

        if (showDialog && currentBook != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEvent(SettingsEvent.OnTrashRestoreClicked(currentBook!!))
                            showDialog = false
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.restore),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            onEvent(SettingsEvent.OnTrashDeleteClicked(currentBook!!))
                            showDialog = false
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.delete),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                title = {
                    Text(stringResource(id = R.string.restore_or_delete))
                },
                text = {
                    Text(stringResource(id = R.string.restore_or_delete_desc, currentBook!!.title))
                },
            )
        }

        Column (
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            LazyColumn {
                items(state.trashedBooks) { book ->
                    Column {
                        Row {
                            TrashedBookItem(
                                book = book,
                                onClick = {
                                    currentBook = book
                                    showDialog = true
                                },
                                onRestore = { onEvent(SettingsEvent.OnTrashRestoreClicked(book)) },
                                onDelete = { onEvent(SettingsEvent.OnTrashDeleteClicked(book)) },
                            )
                        }
                        Row {
                            Spacer(modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrashedBookItem(book: Book, onClick: () -> Unit, onRestore: () -> Unit, onDelete: () -> Unit) {
    Row (
        modifier = Modifier
            .padding(12.dp, 0.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(1f)
        ) {
            Text(
                text = book.title,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = book.author,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            )
            Text(
                text = "${stringResource(id = R.string.deleted)}: ${book.getDeletedSinceString()}",
                modifier = Modifier
                    .padding(end = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        }
        Column (
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = customIconRestoreFromTrash(),
                contentDescription = stringResource(id = R.string.restore),
                modifier = Modifier
                    .clickable { onRestore() }
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = customIconDeleteForever(),
                contentDescription = stringResource(id = R.string.delete),
                modifier = Modifier
                    .clickable { onDelete() }
                    .size(32.dp),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
