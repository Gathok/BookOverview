@file:OptIn(ExperimentalPermissionsApi::class)

package de.gathok.bookoverview.add

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.gathok.bookoverview.R
import de.gathok.bookoverview.api.BookModel
import de.gathok.bookoverview.data.BookSeries
import de.gathok.bookoverview.ui.CustomAlertDialog
import de.gathok.bookoverview.ui.Dropdown
import de.gathok.bookoverview.ui.customIconBarcodeScanner
import de.gathok.bookoverview.util.NavAddScreen
import de.gathok.bookoverview.util.NavOverviewScreen
import de.gathok.bookoverview.util.NavScannerScreen

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController, state: AddState, onEvent: (AddEvent) -> Unit, pIsbnFromNav: String? = null) {

    var isbnFromNav by remember { mutableStateOf(pIsbnFromNav) }
    var forceUpdate by remember { mutableStateOf(false) }
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
    LaunchedEffect (key1 = isbnFromNav, key2 = forceUpdate) {
        if (isbnFromNav != null) {
            onEvent(AddEvent.IsbnChanged(isbnFromNav!!))
            val errorTriple = completeWithIsbn(
                onEvent,
                isbnFromNav!!,
                errorTitleResource,
                state,
                errorMessageResource,
                showError
            )
            errorTitleResource = errorTriple.first
            errorMessageResource = errorTriple.second
            showError = errorTriple.third
        } else {
            onEvent(AddEvent.IsbnChanged(""))
        }
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(NavOverviewScreen()) }) {
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
                                errorMessageResource = R.string.error_msg_no_title
                                showError = true
                            } else {
                                showIncompleteError = true
                            }
                        } else {
                            onEvent(AddEvent.AddBook)
                            navController.navigate(NavAddScreen())
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
            CustomAlertDialog(
                onDismissRequest = {
                    showError = false
                },
                onConfirm = {
                    showError = false
                },
                noConfirmButton = true,
                title = { Text(stringResource(id = errorTitleResource)) },
                text = { Text(stringResource(id = errorMessageResource)) },
            )
        }

        if (showIncompleteError) {
            CustomAlertDialog(
                onDismissRequest = {
                    showIncompleteError = false
                },
                onConfirm = {
                    showIncompleteError = false
                    onEvent(AddEvent.AddBook)
                    navController.navigate(NavOverviewScreen())
                },
                title = { Text(stringResource(id = R.string.data_incomplete)) },
                text = { Text(stringResource(id = R.string.error_msg_add_incomplete)) }
            )
        }

        Column(
            modifier = Modifier
                .padding(12.dp, pad.calculateTopPadding(), 12.dp, 12.dp)
                .verticalScroll(scrollState)
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
                    if (state.showCompleteWithIsbn) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.auto_complete),
                            modifier = Modifier
                                .clickable {
                                    if (isbnFromNav == state.isbn) forceUpdate = !forceUpdate
                                    else isbnFromNav = state.isbn
                                }
                                .padding(end = 8.dp)
                        )
                    } else {
                        Icon(
                            imageVector = customIconBarcodeScanner(),
                            contentDescription = stringResource(R.string.scan),
                            modifier = Modifier
                                .clickable {
                                    if (cameraPermission.status.isGranted) {
                                        navController.navigate(NavScannerScreen)
                                    } else {
                                        cameraPermission.launchPermissionRequest()
                                    }
                                }
                                .padding(end = 8.dp)
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
            Spacer(modifier = Modifier.height(16.dp))
            Row (
                verticalAlignment = Alignment.CenterVertically
            ) {
                Dropdown(
                    selectedOption = Pair(
                        state.bookSeries,
                        state.bookSeries?.title ?: stringResource(R.string.no_series)
                    ),
                    options = state.bookSeriesList.associateBy({ it }, { it.title }),
                    label = stringResource(R.string.book_series),
                    onValueChanged = { onEvent(AddEvent.BookSeriesChanged(it as BookSeries?)) },
                )
            }
        }
    }
}

private suspend fun completeWithIsbn(
    onEvent: (AddEvent) -> Unit,
    isbnFromNav: String,
    pErrorTitleResource: Int,
    state: AddState,
    pErrorMessageResource: Int,
    pShowError: Boolean
): Triple<Int, Int, Boolean> {
    var errorTitleResource = pErrorTitleResource
    var errorMessageResource = pErrorMessageResource
    var showError = pShowError
    val bookResponse = BookModel.bookService.getBook(
        isbn = "isbn:$isbnFromNav"
    )
    try {
        onEvent(AddEvent.TitleChanged(""))
        onEvent(AddEvent.AuthorChanged(""))
        onEvent(AddEvent.TitleChanged(bookResponse.items[0].volumeInfo.title))
        onEvent(AddEvent.AuthorChanged(bookResponse.items[0].volumeInfo.authors.joinToString(", ")))
    } catch (e: Exception) {
        errorTitleResource = R.string.error_scan
        errorMessageResource = if (state.title.isBlank()) {
            R.string.error_msg_scan
        } else {
            R.string.error_msg_scan_author
        }
        showError = true
    }
    //return list of errorTitleResource, errorMessageResource, showError
    return Triple(errorTitleResource, errorMessageResource, showError)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RatingBar(
    max: Int = 5,
    current: Int,
    onRatingChanged: (Int) -> Unit,
    enabled: Boolean = true,
    changed: Boolean = false,
    showText: Boolean = true,
    activeColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.tertiary,
    inactiveColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
) {

    val notEditingToast = Toast.makeText(
        LocalContext.current,
        stringResource(id = R.string.not_editing_desc),
        Toast.LENGTH_SHORT
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
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
                                } else {
                                    notEditingToast.show()
                                }
                            },
                            onLongClick = {
                                if (enabled) {
                                    onRatingChanged(0)
                                } else {
                                    notEditingToast.show()
                                }
                            }
                        )
                        .padding(horizontal = 4.dp)
                        .weight(1f) // This will divide the available space equally between the stars
                        .aspectRatio(1f), // This will make the stars square
                    tint =
                        if (i <= current) activeColor
                        else inactiveColor,
                )
            }
        }
        if (showText) {
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
}

/* RIP BookSeriesDropDown
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSeriesDropDown(
    selectedOption: String,
    options: List<String>,
    label: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var currentInput by remember { mutableStateOf(selectedOption) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
            if (expanded)
                currentInput = ""
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = !expanded,
            value = if (expanded) currentInput else selectedOption,
            onValueChange = {
                currentInput = it
                onValueChanged(it)
            },
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            singleLine = true,
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: String ->
                if (option.contains(currentInput, ignoreCase = true)) {
                    DropdownMenuItem(
                        text = { Text(text = option) },
                        onClick = {
                            expanded = false
                            currentInput = option
                            onValueChanged(option)
                        }
                    )
                }
            }
            if (currentInput.isNotBlank() && !options.contains(currentInput)) {
                DropdownMenuItem(
                    text = { Text(text = currentInput) },
                    onClick = {
                        expanded = false
                        onValueChanged(currentInput)
                    }
                )
            }
        }
    }
}
*/

//@Preview
//@Composable
//fun AddBookScreenPreview() {
//    BookOverviewTheme {
//        AddBookScreen()
//    }
//}
