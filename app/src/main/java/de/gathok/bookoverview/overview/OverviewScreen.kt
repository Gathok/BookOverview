@file:OptIn(
    ExperimentalFoundationApi::class
)

package de.gathok.bookoverview.overview

import android.annotation.SuppressLint
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.overview.util.AppIcon
import de.gathok.bookoverview.overview.util.BookItem
import de.gathok.bookoverview.overview.util.FilterDialog
import de.gathok.bookoverview.overview.util.SearchBar
import de.gathok.bookoverview.overview.util.SearchType
import de.gathok.bookoverview.overview.util.SortType
import de.gathok.bookoverview.ui.CustomTopBar
import de.gathok.bookoverview.ui.swipeContainer.ActionIcon
import de.gathok.bookoverview.ui.swipeContainer.SwipeItem
import de.gathok.bookoverview.util.NavAddScreen
import de.gathok.bookoverview.util.NavDetailsScreen
import de.gathok.bookoverview.util.NavScannerScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    navController: NavController,
    openDrawer: () -> Unit,
    state: OverviewState,
    onEvent: (OverviewEvent) -> Unit,
    authorToSearch: String? = null,
    seriesToSearch: Int? = null,
) {
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(authorToSearch) {
        if (authorToSearch != null) {
            onEvent(OverviewEvent.ChangeFilterList(
                state.possessionStatus,
                state.readStatus,
                sortType = SortType.AUTHOR,
                searchType = SearchType.AUTHOR
            ))
            onEvent(OverviewEvent.SearchQueryChanged(authorToSearch))
        }
    }

    LaunchedEffect(seriesToSearch) {
        if (seriesToSearch != null) {
            onEvent(OverviewEvent.ChangeFilterList(
                state.possessionStatus,
                state.readStatus,
                sortType = state.sortType,
                searchType = SearchType.SeriesId,
            ))
            onEvent(OverviewEvent.SearchQueryChanged(seriesToSearch.toString()))
        }
    }

    var showFilterDialog by remember { mutableStateOf(false) }
    var deletedBook by remember { mutableStateOf<Book?>(null) }

    if (showFilterDialog) {
        FilterDialog(
            onDismiss = { showFilterDialog = false },
            onReset = {
                onEvent(OverviewEvent.ResetFilter)
                showFilterDialog = false
            },
            onPositiveClick = {
                showFilterDialog = false
                onEvent(
                    OverviewEvent.ChangeFilterList(
                        state.possessionStatus,
                        state.readStatus,
                        state.sortType,
                        state.searchType
                    )
                )
            },
            onFilterChange = { index, value ->
                when (index) {
                    0 -> state.possessionStatus = value
                    1 -> state.readStatus = value
                }
            },
            onTypeChange = { index, value ->
                when (index) {
                    0 -> {
                        state.sortType = when (value) {
                            0 -> SortType.TITLE
                            1 -> SortType.AUTHOR
                            else -> SortType.TITLE
                        }
                    }
                    1 -> {
                        state.searchType = when (value) {
                            0 -> SearchType.TITLE
                            1 -> SearchType.AUTHOR
                            2 -> SearchType.ISBN
                            3 -> SearchType.Series
                            4 -> SearchType.SeriesId
                            else -> SearchType.TITLE
                        }
                    }
                }
            },
            filterItemsList = listOf(stringResource(id = R.string.owned)+":", stringResource(id = R.string.read)+":"),
            typeItemsList = mapOf(
                stringResource(id = R.string.sort_by)+":" to listOf(stringResource(id = R.string.title), stringResource(id = R.string.author)),
                stringResource(id = R.string.search_by)+":" to listOf(stringResource(id = R.string.title), stringResource(id = R.string.author),
                    stringResource(id = R.string.isbn), stringResource(id = R.string.series), stringResource(id = R.string.series_id))
            ),
            filterStates = listOf(state.possessionStatus, state.readStatus),
            typeStates = listOf(
                when(state.sortType) {
                    SortType.TITLE -> stringResource(id = R.string.title)
                    SortType.AUTHOR -> stringResource(id = R.string.author)
                },
                when(state.searchType) {
                    SearchType.TITLE -> stringResource(id = R.string.title)
                    SearchType.AUTHOR -> stringResource(id = R.string.author)
                    SearchType.ISBN -> stringResource(id = R.string.isbn)
                    SearchType.Series -> stringResource(id = R.string.series)
                    SearchType.SeriesId -> stringResource(id = R.string.series_id)
                }
            )
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                navigationIcon = {
                    IconButton(onClick = {
                        openDrawer()
                    }) {
                        AppIcon()
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Add Book",
                        modifier = Modifier
                            .padding(12.dp)
                            .combinedClickable (
                                onClick = {
                                    navController.navigate(NavAddScreen())
                                },
                                onLongClick = {
                                    navController.navigate(NavScannerScreen)
                                }
                            )
                    )
                },
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 12.dp, bottomEnd = 12.dp))
            )
        },
        /* RIP FloatingActionButton
        floatingActionButton = {
        FloatingActionButton(
        onClick = {  }
        ) {
        Icon(
        imageVector = Icons.Filled.Add,
        contentDescription = stringResource(id = R.string.add_book),
        modifier = Modifier
        .padding(16.dp)
        .combinedClickable(
        onClick = {
        navController.navigate(NavAddScreen())
        },
        onLongClick = {
        navController.navigate(NavScannerScreen)
        }
        )
        )
        }
        },
        */
    ) { pad ->
        Box (modifier = Modifier.fillMaxSize()) {
            Column (
                modifier = Modifier
                    .padding(pad)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SearchBar(
                        value = state.searchQuery,
                        onValueChange = { onEvent(OverviewEvent.SearchQueryChanged(it)) },
                        modifier = Modifier
                            .weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Rounded.FilterList,
                        contentDescription = stringResource(id = R.string.filter),
                        modifier = Modifier
                            .size(48.dp)
                            .combinedClickable (
                                onClick = {
                                    showFilterDialog = true
                                },
                                onLongClick = {
                                    onEvent(OverviewEvent.ResetFilter)
                                    Toast.makeText(
                                        context,
                                        context.getString(R.string.filter_reset),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Vibrate
                                    (getSystemService(context, Vibrator::class.java) as Vibrator)
                                        .vibrate(
                                            VibrationEffect
                                                .createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                                        )
                                }
                            )
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(
                        items = state.books,
                        key = { it.id }
                    ) { book ->
                        val actionLabel = stringResource(id = R.string.restore)
                        val message = "\"${book.title}\" ${stringResource(id = R.string.deleted)}"
                        SwipeItem (
                            leftOptions = {
                                ActionIcon(
                                    onClick = {
                                        navController.navigate(NavDetailsScreen(book.id))
                                    },
                                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                    icon = Icons.Default.Info,
                                    contentDescription = "Details",
                                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            },
                            rightOptions = {
                                ActionIcon(
                                    onClick = {
                                        onEvent(OverviewEvent.DeleteBook(book))
                                        deletedBook = book
                                        coroutineScope.launch {
                                            val snackbarResult = snackbarHostState.showSnackbar(
                                                message = message,
                                                actionLabel = actionLabel,
                                                duration = SnackbarDuration.Short
                                            )
                                            if (snackbarResult == SnackbarResult.ActionPerformed) {
                                                onEvent(OverviewEvent.RestoreBook(deletedBook!!))
                                                println("Action Performed")
                                            }
                                        }
                                    },
                                    backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                    icon = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            },
                        ) {
                            Column (
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                Row (
                                    modifier = Modifier
                                        .padding(12.dp, 0.dp)
                                        .background(MaterialTheme.colorScheme.background)
                                ) {
                                    BookItem(book)
                                }
                            }
                        }
                        Row (
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            Spacer(modifier = Modifier
                                .height(2.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                            )
                        }
                    }
                    item {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stringResource(id = R.string.shown_books, state.books.size),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(128.dp))
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp, start = 12.dp, end = 12.dp),
            ) { snackbarData: SnackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    actionColor = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}