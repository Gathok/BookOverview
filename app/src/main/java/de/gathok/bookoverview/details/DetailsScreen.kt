@file:OptIn(ExperimentalMaterial3Api::class)

package de.gathok.bookoverview.details

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import de.gathok.bookoverview.R
import de.gathok.bookoverview.add.RatingBar
import de.gathok.bookoverview.api.BookModel
import de.gathok.bookoverview.ui.customIconBook
import de.gathok.bookoverview.ui.customIconCheckBoxOutlineBlank
import de.gathok.bookoverview.ui.customIconSelectCheckBox


@Composable
fun DetailsScreen(navController: NavController, state: DetailsState, onEvent: (DetailsEvent) -> Unit,
                  bookId: Int?) {

    LaunchedEffect (key1 = bookId) {
        onEvent(DetailsEvent.ResetState)
        if (bookId != null) {
            onEvent(DetailsEvent.ChangeBookId(bookId))
        } else {
            navController.popBackStack()
        }
    }

    LaunchedEffect (key1 = state.book) {
        if (state.book != null) {
            onEvent(DetailsEvent.TitleChanged(state.book.title))
            onEvent(DetailsEvent.AuthorChanged(state.book.author))
            onEvent(DetailsEvent.IsbnChanged(state.book.isbn))
            onEvent(DetailsEvent.PossessionStatusChanged(state.book.possessionStatus))
            onEvent(DetailsEvent.ReadStatusChanged(state.book.readStatus))
            onEvent(DetailsEvent.RatingChanged(state.book.rating ?: 0))
            onEvent(DetailsEvent.DescriptionChanged(state.book.description))

            val bookResponse = BookModel.bookService.getBook(
                isbn = "isbn:${state.book.isbn}"
            )
            try {
                val imageUrl = bookResponse.items[0].volumeInfo.imageLinks.thumbnail
                onEvent(DetailsEvent.SetCoverImage(imageUrl))
                onEvent(DetailsEvent.SetOnlineDescription(bookResponse.items[0].volumeInfo.description))
            } catch (e: Exception) {
                // No cover image found
            }
        }
    }

    var showConfirmLeaveDialog by remember { mutableStateOf(false) }
    var showNoTitleDialog by remember { mutableStateOf(false) }

    fun onDismiss() {
        if (state.isEditing &&
            (state.titleChanged || state.authorChanged || state.isbnChanged ||
                    state.possessionStatusChanged || state.readStatusChanged || state.ratingChanged
                    || state.descriptionChanged)
        ) {
            showConfirmLeaveDialog = true
        } else {
            navController.popBackStack()
        }
    }

    BackHandler(enabled = true) {
        // This block will be called when the user tries to navigate back
        onDismiss()
    }

    Scaffold(
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
                        IconButton(onClick = {
                            onEvent(DetailsEvent.SwitchEditing)
                        }) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.edit)
                            )
                        }
                    } else {
                        IconButton(onClick = {
                            if (state.title.isBlank()) {
                                showNoTitleDialog = true
                            } else {
                                if (state.titleChanged || state.authorChanged || state.isbnChanged ||
                                        state.possessionStatusChanged || state.readStatusChanged||
                                        state.ratingChanged || state.descriptionChanged
                                ) {
                                    onEvent(DetailsEvent.UpdateBook)
                                }
                                onEvent(DetailsEvent.SwitchEditing)
                            }
                        }) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = stringResource(R.string.save)
                            )
                        }
                    }
                }
            )
        }
    ) { pad ->
        var showFullTitle by remember { mutableStateOf(false) }
        var showFullAuthor by remember { mutableStateOf(false) }
        val showCover = state.coverImage.isNotBlank() // && !hideCover

        var showEditDialog by remember { mutableStateOf(false) }
        var curEditType by remember { mutableStateOf(EditType.TITLE) }

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
                                navController.popBackStack()
                            }
                        }
                    ) {
                        Text(text = stringResource(R.string.yes_save))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            navController.popBackStack()
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

        if (showEditDialog) {
            var tempValue by remember { mutableStateOf("") }

            LaunchedEffect(key1 = curEditType) {
                tempValue = when (curEditType) {
                    EditType.TITLE -> state.title
                    EditType.AUTHOR -> state.author
                    EditType.ISBN -> state.isbn
                    EditType.DESCRIPTION -> state.description
                }
            }

            AlertDialog(
                title = {
                    Text(text = stringResource(
                        id = R.string.edit_title,
                        stringResource(curEditType.getTitleStringId)
                    ))
                },
                text = {
                    OutlinedTextField(
                        value = tempValue,
                        onValueChange = { tempValue = it },
                        label = {
                            Text(
                                text = stringResource(
                                    id = R.string.new_label,
                                    stringResource(curEditType.getTitleStringId)),
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                onDismissRequest = { showEditDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            when (curEditType) {
                                EditType.TITLE -> onEvent(DetailsEvent.TitleChanged(tempValue))
                                EditType.AUTHOR -> onEvent(DetailsEvent.AuthorChanged(tempValue))
                                EditType.ISBN -> onEvent(DetailsEvent.IsbnChanged(tempValue))
                                EditType.DESCRIPTION -> onEvent(DetailsEvent.DescriptionChanged(tempValue))
                            }
                            showEditDialog = false
                        }
                    ) {
                        Text(text = stringResource(R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showEditDialog = false }
                    ) {
                        Text(text = stringResource(R.string.cancel))
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .padding(top = pad.calculateTopPadding()),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 0.dp, start = 12.dp, end = 12.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = if (showCover) {
                        Modifier.fillMaxWidth(0.7f)
                    } else {
                        Modifier.fillMaxWidth(0.85f)
                    },
                    verticalArrangement = Arrangement.Top
                ) {
                    Row {
                        Text(
                            text = state.isbn,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (state.isbnChanged) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (state.isEditing) {
                                        curEditType = EditType.ISBN
                                        showEditDialog = true
                                    }
                                },
                        )
                    }
                    Row {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (state.titleChanged) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            maxLines = if (showFullTitle) Int.MAX_VALUE else 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (state.isEditing) {
                                        curEditType = EditType.TITLE
                                        showEditDialog = true
                                    } else {
                                        showFullTitle = !showFullTitle
                                    }
                                },
                        )
                    }
                    Row {
                        Text(
                            text = stringResource(R.string.details_by, state.author),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (state.authorChanged) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                            },
                            maxLines = if (showFullAuthor) Int.MAX_VALUE else 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (state.isEditing) {
                                        curEditType = EditType.AUTHOR
                                        showEditDialog = true
                                    } else {
                                        showFullAuthor = !showFullAuthor
                                    }
                                }
                        )
                    }
                    Row(
                        modifier = if (showCover) {
                            Modifier.fillMaxWidth(0.85f)
                        } else {
                            Modifier.fillMaxWidth(0.7f)
                        }
                    ) {
                        RatingBar(
                            current = state.rating,
                            onRatingChanged = { newRating ->
                                onEvent(DetailsEvent.RatingChanged(newRating))
                            },
                            enabled = state.isEditing,
                            showText = false,
                            activeColor =
                                if (state.ratingChanged) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary,
                            inactiveColor =
                                if (state.ratingChanged) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .fillMaxWidth()
                ) {
                    if (showCover) {
                        Row {
                            AsyncImage(
                                model = state.coverImage,
                                contentDescription = stringResource(id = R.string.cover_image),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(shape = RoundedCornerShape(10.dp))
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            PossessionIcon(state, onEvent)
                            ReadIcon(state, onEvent)
                        }
                    }
                    else {
                        Row (
                            horizontalArrangement = Arrangement.End,
                        ) {
                            PossessionIcon(state, onEvent, 1f)
                        }
                        Row (
                            horizontalArrangement = Arrangement.End,
                        ) {
                            ReadIcon(state, onEvent, 1f)
                        }
                    }
                }
            }
            Spacer(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)),
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = 12.dp, end = 12.dp, bottom = 6.dp)
                    .clickable {
                        if (state.isEditing) {
                            curEditType = EditType.DESCRIPTION
                            showEditDialog = true
                        }
                    },
            ) {
                Column {
                    Text(
                        text = if (state.description.isNotBlank()) {
                            stringResource(R.string.own_description)
                        } else {
                            stringResource(R.string.online_description)
                        },
                        style = MaterialTheme.typography.titleMedium,
                        color = if (state.descriptionChanged) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                    Text(
                        text = state.description.ifBlank {
                            state.onlineDescription.ifBlank {
                                stringResource(R.string.no_description_available)
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Justify,
                    )
                }
            }
        }
    }
}

@Composable
fun PossessionIcon(state: DetailsState, onEvent: (DetailsEvent) -> Unit, fillWidth: Float = 0.4f) {

    val context = LocalContext.current
    val text = stringResource(R.string.not_editing_desc)

    Icon(
        imageVector = customIconBook(),
        contentDescription = stringResource(R.string.owned),
        modifier = Modifier
            .fillMaxWidth(fillWidth)
            .clickable {
                if (state.isEditing) {
                    onEvent(DetailsEvent.PossessionStatusChanged(!state.possessionStatus))
                } else {
                    Toast.makeText(
                        context,
                        text,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
        tint = if (state.possessionStatus) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        }
    )
}

@Composable
fun ReadIcon(state: DetailsState, onEvent: (DetailsEvent) -> Unit, fillWidth: Float = 0.5f) {

    val context = LocalContext.current
    val text = stringResource(R.string.not_editing_desc)

    Icon(
        imageVector = if (state.readStatus) {
            customIconSelectCheckBox()
        } else {
            customIconCheckBoxOutlineBlank()
        },
        contentDescription = stringResource(R.string.read),
        modifier = Modifier
            .fillMaxWidth(fillWidth)
            .clickable {
                if (state.isEditing) {
                    onEvent(DetailsEvent.ReadStatusChanged(!state.readStatus))
                } else {
                    Toast.makeText(
                        context,
                        text,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
        tint = if (state.readStatus) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        },
    )
}
