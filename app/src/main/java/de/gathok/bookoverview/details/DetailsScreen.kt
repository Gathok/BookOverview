@file:OptIn(ExperimentalMaterial3Api::class)

package de.gathok.bookoverview.details

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.add.RatingBar
import de.gathok.bookoverview.util.Screen


@Composable
fun DetailsScreen(navController: NavController, state: DetailsState, onEvent: (DetailsEvent) -> Unit,
                  bookId: Int?) {

    if (bookId == null || state.bookId == 0) {
        AlertDialog(
            title = { Text(stringResource(R.string.error)) },
            text = { Text(stringResource(R.string.error_message)) },
            onDismissRequest = { navController.navigate(Screen.Overview.route) },
            confirmButton = {
                TextButton(
                    onClick = { navController.navigate(Screen.Overview.route) }
                ) {
                    Text("OK")
                }
            }
        )
    } else {
        LaunchedEffect(key1 = bookId) {
            onEvent(DetailsEvent.FetchBook(bookId))
        }
        DetailsScreenContent(navController, state, onEvent)
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailsScreenContent(navController: NavController, state: DetailsState, onEvent: (DetailsEvent) -> Unit) {

    var showConfirmLeaveDialog by remember { mutableStateOf(false) }
    var showNoTitleDialog by remember { mutableStateOf(false) }

    fun onDismiss() {
        if (state.isEditing &&
            (state.titleChanged || state.authorChanged || state.isbnChanged ||
                    state.possessionStatusChanged || state.readStatusChanged || state.ratingChanged)) {
            showConfirmLeaveDialog = true
        } else {
            navController.navigate(Screen.Overview.route)
            onEvent(DetailsEvent.ResetState)
        }
    }

    BackHandler(enabled = true) {
        // This block will be called when the user tries to navigate back
        onDismiss()
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = {
                        onDismiss()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Text(text = stringResource(id = R.string.book_details))
                },
                actions = {
                    if (!state.isEditing) {
                        IconButton(onClick = { onEvent(DetailsEvent.SwitchEditing) }) {
                            Icon(Icons.Filled.Edit, contentDescription = stringResource(R.string.edit))
                        }
                    } else {
                        IconButton(onClick = {
                            if (state.title.isBlank()) {
                                showNoTitleDialog = true
                            } else {
                                onEvent(DetailsEvent.UpdateBook)
                                onEvent(DetailsEvent.SwitchEditing)
                            }
                        }) {
                            Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.save))
                        }
                    }
                }
            )
        }
    ) {  pad ->
        val scrollState = rememberScrollState()

        if (showConfirmLeaveDialog) {
            AlertDialog(
                title = { Text(stringResource(R.string.error_save_changes)) },
                text = { Text(stringResource(R.string.error_msg_save_changes)) },
                onDismissRequest = { showConfirmLeaveDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (state.title.isBlank()) {
                                showConfirmLeaveDialog = false
                                showNoTitleDialog = true
                            } else {
                                onEvent(DetailsEvent.UpdateBook)
                                onEvent(DetailsEvent.SwitchEditing)
                                navController.navigate(Screen.Overview.route)
                                onEvent(DetailsEvent.ResetState)
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.yes_save))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            navController.navigate(Screen.Overview.route)
                            onEvent(DetailsEvent.ResetState)
                        }
                    ) {
                        Text(
                            text = stringResource(R.string.no),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }

        if (showNoTitleDialog) {
            AlertDialog(
                title = { Text(text = stringResource(R.string.error)) },
                text = { Text(text = stringResource(R.string.error_msg_no_title)) },
                onDismissRequest = { showNoTitleDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = { showNoTitleDialog = false }
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(12.dp, pad.calculateTopPadding(), 12.dp, 12.dp)
                .verticalScroll(scrollState)
                .clip(shape = RoundedCornerShape(10.dp))
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = {
                    onEvent(DetailsEvent.TitleChanged(it))
                },
                label = {
                    Text(stringResource(R.string.title)
                                + if(state.titleChanged) "*" else "")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state.isEditing
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.author,
                onValueChange = {
                    onEvent(DetailsEvent.AuthorChanged(it))
                },
                label = {
                    Text(stringResource(R.string.author)
                                + if(state.authorChanged) "*" else "")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state.isEditing
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (state.isDoubleIsbn) {
                Row (
                    Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = stringResource(R.string.is_double_isbn),
                        modifier = Modifier.padding(end = 8.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = stringResource(R.string.is_double_isbn),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            OutlinedTextField(
                value = state.isbn,
                onValueChange = {
                    onEvent(DetailsEvent.IsbnChanged(it))
                },
                label = {
                    Text(stringResource(R.string.isbn)
                                + if(state.isbnChanged) "*" else "")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = state.isEditing
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Checkbox(
                        checked = state.possessionStatus,
                        onCheckedChange = {
                            onEvent(DetailsEvent.PossessionStatusChanged(it))
                        },
                        enabled = state.isEditing
                    )
                    Text(stringResource(id = R.string.owned)
                            + if (state.possessionStatusChanged) "*" else "")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Checkbox(
                        checked = state.readStatus,
                        onCheckedChange = {
                            onEvent(DetailsEvent.ReadStatusChanged(it))
                        },
                        enabled = state.isEditing
                    )
                    Text(stringResource(id = R.string.read)
                            + if (state.readStatusChanged) "*" else "")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column{
                    // Rating with clickable stars
                    RatingBar(
                        current = state.rating,
                        onRatingChanged = { newRating ->
                            onEvent(DetailsEvent.RatingChanged(newRating))
                        },
                        enabled = state.isEditing,
                        changed = state.ratingChanged
                    )
                }
            }
        }
    }
}