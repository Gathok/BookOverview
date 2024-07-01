@file:OptIn(ExperimentalPermissionsApi::class)

package de.gathok.bookoverview.add

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.gathok.bookoverview.R
import de.gathok.bookoverview.add.api.BookModel
import de.gathok.bookoverview.ui.theme.ratingStars
import de.gathok.bookoverview.util.Screen
import de.gathok.bookoverview.util.customIconBarcodeScanner

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController, state: AddState, onEvent: (AddEvent) -> Unit, isbnFromNav: String? = null) {

    // General error
    var showError by remember { mutableStateOf(false) }
    var errorTitleResource by remember { mutableIntStateOf(R.string.error) }
    var errorMessageResource by remember { mutableIntStateOf(R.string.error_message) }
    // Incomplete data error
    var showIncompleteError by remember { mutableStateOf(false) }

    // Others
    val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

    // LaunchedEffects
    // If an ISBN is passed from the scanner, fetch the book data
    LaunchedEffect (key1 = isbnFromNav) {
        if (isbnFromNav != null) {
            onEvent(AddEvent.IsbnChanged(isbnFromNav))
            val bookResponse = BookModel.bookService.getBook(
                isbn = "isbn:$isbnFromNav"
            )
            try {
                onEvent(AddEvent.TitleChanged(bookResponse.items[0].volumeInfo.title))
                onEvent(AddEvent.AuthorChanged(bookResponse.items[0].volumeInfo.authors.joinToString(", ")))
            } catch (e: Exception) {
                errorTitleResource = R.string.error_scan
                if (bookResponse.totalItems == 0 || state.title.isBlank()) {
                    errorMessageResource = R.string.error_msg_scan
                } else {
                    errorMessageResource = R.string.error_msg_scan_author
                }
                showError = true
            }
        }
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Screen.Overview.route) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(id = R.string.add_book),
                        modifier = Modifier.clickable { onEvent(AddEvent.ClearFields) }
                    )
                },
                actions = {
                    IconButton(onClick = {
                        if (state.title.trim().isBlank() || state.author.trim().isBlank() || state.isbn.trim().isBlank()) {
                            if (state.title.trim().isBlank()) {
                                errorTitleResource = R.string.error_add
                                errorMessageResource = R.string.error_msg_add
                                showError = true
                            } else {
                                showIncompleteError = true
                            }
                        } else {
                            onEvent(AddEvent.AddBook)
                            navController.navigate(Screen.Overview.route)
                        }
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.submit))
                    }
                }
            )
        }
    ) { pad ->
        val scrollState = rememberScrollState()

        if (showError) {
            AlertDialog(
                onDismissRequest = {
                    showError = false
                },
                confirmButton = {
                    TextButton(onClick = { showError = false }) {
                        Text(stringResource(R.string.ok))
                    }
                },
                title = { Text(stringResource(id = errorTitleResource)) },
                text = { Text(stringResource(id = errorMessageResource)) }
            )
        }

        if (showIncompleteError) {
            AlertDialog(
                onDismissRequest = {
                    showIncompleteError = false
                },
                confirmButton = {
                    TextButton(onClick = {
                        showIncompleteError = false
                        onEvent(AddEvent.AddBook)
                        navController.navigate(Screen.Overview.route)
                    }) {
                        Text(stringResource(R.string.yes))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showIncompleteError = false }) {
                        Text(stringResource(R.string.no))
                    }
                },
                title = { Text(stringResource(id = R.string.data_incomplete)) },
                text = { Text(stringResource(id = R.string.error_msg_add_incomplete)) }
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
                    onEvent(AddEvent.TitleChanged(it))
                },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.author,
                onValueChange = {
                    onEvent(AddEvent.AuthorChanged(it))
                },
                label = { Text(stringResource(R.string.author)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
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
                    onEvent(AddEvent.IsbnChanged(it))
                },
                label = { Text(stringResource(R.string.isbn)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = customIconBarcodeScanner(),
                        contentDescription = stringResource(R.string.scan),
                        modifier = Modifier
                            .clickable {
                                if (cameraPermission.status.isGranted) {
                                    navController.navigate(Screen.Scanner.route)
                                } else {
                                    cameraPermission.launchPermissionRequest()
                                }
                            }
                            .padding(end = 8.dp)
                    )
                }
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
                            onEvent(AddEvent.PossessionStatusChanged(it))
                        },
                    )
                    Text(stringResource(id = R.string.owned))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Checkbox(
                        checked = state.readStatus,
                        onCheckedChange = {
                            onEvent(AddEvent.ReadStatusChanged(it))
                        },
                    )
                    Text(stringResource(id = R.string.read))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column{
                    // Rating with clickable stars
                    RatingBar(
                        current = state.rating,
                        onRatingChanged = { newRating ->
                            onEvent(AddEvent.RatingChanged(newRating))
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RatingBar(
    max: Int = 5,
    current: Int,
    onRatingChanged: (Int) -> Unit,
    enabled: Boolean = true,
    changed: Boolean = false
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            for (i in 1..max) {
                Icon(
                    imageVector = if (i <= current) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "${stringResource(R.string.rating)}: $i",
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                if (enabled) {
                                    onRatingChanged(i)
                                }
                            },
                            onLongClick = {
                                if (enabled) {
                                    onRatingChanged(0)
                                }
                            }
                        )
                        .padding(horizontal = 4.dp)
                        .weight(1f) // This will divide the available space equally between the stars
                        .aspectRatio(1f), // This will make the stars square
                    tint = if (i <= current) ratingStars else Color.Gray,
                )
            }
        }
        Row {
            Text(
                text = when(current) {
                    0 -> stringResource(R.string.no_rating)
                    else -> "${stringResource(R.string.rating)}: $current★"
                } + if (changed) "*" else "",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

//@Preview
//@Composable
//fun AddBookScreenPreview() {
//    BookOverviewTheme {
//        AddBookScreen()
//    }
//}
