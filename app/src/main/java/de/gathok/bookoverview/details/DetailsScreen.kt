@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package de.gathok.bookoverview.details

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import de.gathok.bookoverview.R
import de.gathok.bookoverview.add.RatingBar
import de.gathok.bookoverview.api.BookModel
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.data.BookSeries
import de.gathok.bookoverview.ui.Dropdown
import de.gathok.bookoverview.ui.customIconBook
import de.gathok.bookoverview.ui.customIconRead
import de.gathok.bookoverview.ui.theme.BookOverviewTheme
import de.gathok.bookoverview.util.NavOverviewScreen


@Composable
fun DetailsScreen(navController: NavController, state: DetailsState, onEvent: (DetailsEvent) -> Unit,
                  bookId: Int?, testBook: Book? = null) {

    val context = LocalContext.current

    LaunchedEffect (key1 = bookId) {
        onEvent(DetailsEvent.ResetState)
        if (bookId != null) {
            onEvent(DetailsEvent.ChangeBookId(bookId))
        } else if (testBook == null) {
            navController.popBackStack()
        }
    }

    LaunchedEffect (key1 = state.book) {
        if (state.book != null) {
            onEvent(DetailsEvent.ChangeBook(state.book))

            if (state.book.isbn.length == 13 && state.book.isbn.startsWith("978")) {
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
    }

    val EDIT_ENABLED = stringResource(R.string.edit_enabled)

    LaunchedEffect(state.isEditing) {
        if (state.isEditing) {
            Toast.makeText(context, EDIT_ENABLED, Toast.LENGTH_SHORT).show()
        }
    }

    var showConfirmLeaveDialog by remember { mutableStateOf(false) }
    var showNoTitleDialog by remember { mutableStateOf(false) }

    fun onDismiss() {
        if (state.isEditing && state.somethingChanged) {
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
                                if (state.somethingChanged) {
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

        val NO_SERIES = stringResource(R.string.no_series)

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
            var tempString by remember { mutableStateOf("") }
            var tempSeries by remember { mutableStateOf(state.series) }

            LaunchedEffect(key1 = curEditType) {
                tempString = when (curEditType) {
                    EditType.TITLE -> state.title
                    EditType.AUTHOR -> state.author
                    EditType.ISBN -> state.isbn
                    EditType.DESCRIPTION -> state.description
                    EditType.BOOK_SERIES -> state.series?.title ?: NO_SERIES
                    EditType.READING_TIME -> ""
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
                    when (curEditType) {
                        EditType.BOOK_SERIES -> {
//                            val options: Map<Any?, String> = state.bookSeriesList.associateBy({ it as BookSeries? }, { it.title })
//                                .toMutableMap().apply { put(null, stringResource(R.string.no_series)) }.toMap()

                            Dropdown(
                                selectedOption = Pair(tempSeries, tempSeries?.title ?: NO_SERIES),
                                options = state.bookSeriesList.associateBy({ it }, { it.title }),
                                onValueChanged = { newSeries ->
                                    tempSeries = newSeries as BookSeries
                                },
                                label = stringResource(R.string.new_series),
                            )
                        }
                        EditType.READING_TIME -> {
                            OutlinedTextField(
                                value = tempString,
                                onValueChange = { tempString = it },
                                label = {
                                    Text(
                                        text = stringResource(
                                            id = R.string.add_minutes,
                                            stringResource(curEditType.getTitleStringId),
                                        ),
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            )
                        }
                        else -> {
                            OutlinedTextField(
                                value = tempString,
                                onValueChange = { tempString = it },
                                label = {
                                    Text(
                                        text = stringResource(
                                            id = R.string.new_label,
                                            stringResource(curEditType.getTitleStringId)),
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                },
                onDismissRequest = { showEditDialog = false },
                confirmButton = {
                    Row {
                        if (curEditType == EditType.READING_TIME) {
                            TextButton(
                                onClick = {
                                    onEvent(DetailsEvent.ReadingTimeChanged(
                                        (state.readingTime ?: 0) - (tempString.toIntOrNull() ?: 0)
                                    ))
                                    showEditDialog = false
                                }
                            ) {
                                Icon (
                                    imageVector = Icons.Default.RemoveCircle,
                                    contentDescription = "Subtract Time"
                                )
                            }
                        }
                        TextButton (
                            onClick = {
                                when (curEditType) {
                                    EditType.TITLE -> onEvent(DetailsEvent.TitleChanged(tempString))
                                    EditType.AUTHOR -> onEvent(DetailsEvent.AuthorChanged(tempString))
                                    EditType.ISBN -> onEvent(DetailsEvent.IsbnChanged(tempString))
                                    EditType.DESCRIPTION -> onEvent(DetailsEvent.DescriptionChanged(tempString))
                                    EditType.BOOK_SERIES -> onEvent(DetailsEvent.SeriesChanged(tempSeries))
                                    EditType.READING_TIME -> onEvent(DetailsEvent.ReadingTimeChanged(
                                        (state.readingTime ?: 0) + (tempString.toIntOrNull() ?: 0)
                                    ))
                                }
                                showEditDialog = false
                            }
                        ) {
                            if (curEditType != EditType.READING_TIME) {
                                Text(text = stringResource(R.string.ok))
                            } else {
                                Icon (
                                    imageVector = Icons.Default.AddCircle,
                                    contentDescription = "Add Time"
                                )
                            }
                        }
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
                                .combinedClickable(
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.ISBN
                                            showEditDialog = true
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                                        curEditType = EditType.ISBN
                                        showEditDialog = true
                                    }
                                ),
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
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.TITLE
                                            showEditDialog = true
                                        } else {
                                            showFullTitle = !showFullTitle
                                        }
                                    },
                                    onLongClick = {
                                        if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                                        curEditType = EditType.TITLE
                                        showEditDialog = true
                                    }
                                ),
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
                                .combinedClickable (
                                    onClick = {
                                        if (state.isEditing) {
                                            curEditType = EditType.AUTHOR
                                            showEditDialog = true
                                        } else {
                                            showFullAuthor = !showFullAuthor
                                        }
                                    },
                                    onLongClick = {
                                        navController.navigate(NavOverviewScreen(authorToSearch = state.author))
                                    }
                                )
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
                    .combinedClickable (
                        onClick = {
                            if (state.isEditing) {
                                curEditType = EditType.READING_TIME
                                showEditDialog = true
                            }
                        },
                        onLongClick = {
                            if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                            curEditType = EditType.READING_TIME
                            showEditDialog = true
                        }
                    ),
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.reading_time),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (state.readingTimeChanged) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                    Text(
                        text = if (state.readingTime != null) {
                            formatReadingTime(state.readingTime) + " " + stringResource(R.string.hours)
                        } else {
                            stringResource(R.string.no_reading_time)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = 12.dp, end = 12.dp, bottom = 6.dp)
                    .combinedClickable (
                        onClick = {
                            if (state.isEditing) {
                                curEditType = EditType.BOOK_SERIES
                                showEditDialog = true
                            }
                        },
                        onLongClick = {
                            if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                            curEditType = EditType.BOOK_SERIES
                            showEditDialog = true
                        }
                    )
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.book_series),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (state.seriesChanged) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                    Text(
                        text = state.series?.title ?: stringResource(R.string.no_series_assigned),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, start = 12.dp, end = 12.dp, bottom = 6.dp)
                    .combinedClickable (
                        onClick = {
                            if (state.isEditing) {
                                curEditType = EditType.DESCRIPTION
                                showEditDialog = true
                            }
                        },
                        onLongClick = {
                            if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                            curEditType = EditType.DESCRIPTION
                            showEditDialog = true
                        }
                    )
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
    val description = if (state.possessionStatus) {
        stringResource(R.string.owned)
    } else {
        stringResource(R.string.not_owned)
    }

    Icon(
        imageVector = customIconBook(),
        contentDescription = stringResource(R.string.owned),
        modifier = Modifier
            .fillMaxWidth(fillWidth)
            .combinedClickable(
                onClick = {
                    if (state.isEditing) {
                        onEvent(DetailsEvent.PossessionStatusChanged(!state.possessionStatus))
                    } else {
                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
                    }
                },
                onLongClick = {
                    if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                    onEvent(DetailsEvent.PossessionStatusChanged(!state.possessionStatus))
                }
            ),
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
    val description = if (state.readStatus) {
        stringResource(R.string.read)
    } else {
        stringResource(R.string.not_read)
    }

    Icon(
        imageVector = customIconRead(),
        contentDescription = stringResource(R.string.read),
        modifier = Modifier
            .fillMaxWidth(fillWidth)
            .combinedClickable(
                onClick = {
                    if (state.isEditing) {
                        onEvent(DetailsEvent.ReadStatusChanged(!state.readStatus))
                    } else {
                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
                    }
                },
                onLongClick = {
                    if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                    onEvent(DetailsEvent.ReadStatusChanged(!state.readStatus))
                }
            )
            .padding(top = 6.dp, bottom = 6.dp),
        tint = if (state.readStatus) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        },
    )
}

fun formatReadingTime(readingTime: Int?): String {
    if (readingTime == null) {
        return ""
    }
    val minutes: Int = readingTime % 60
    val hours: Int = (readingTime - minutes) / 60
    return if (minutes < 10) {
        "$hours:0$minutes"
    } else {
        "$hours:$minutes"
    }
}


@Preview
@Composable
private fun PreviewDetailsScreen() {
    BookOverviewTheme (
        darkTheme = true
    ) {
        DetailsScreen(
            navController = NavController(LocalContext.current),
            state = DetailsState(),
            onEvent = {},
            bookId = null,
            testBook = Book(
                title = "Test Title",
                author = "Test Author",
                isbn = "1234567890123",
                possessionStatus = true,
                readStatus = false,
                rating = 3,
                description = "Test Description",
                id = 1,
                bookSeriesId = 1
            )
        )
    }
}
