@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)

package de.gathok.bookoverview.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.ui.CustomDialog
import de.gathok.bookoverview.ui.CustomTopBar
import de.gathok.bookoverview.ui.customIconBook
import de.gathok.bookoverview.ui.customIconFilterList
import de.gathok.bookoverview.ui.customIconRead
import de.gathok.bookoverview.ui.swipeContainer.ActionIcon
import de.gathok.bookoverview.ui.swipeContainer.SwipeItem
import de.gathok.bookoverview.util.NavAddScreen
import de.gathok.bookoverview.util.NavDetailsScreen
import de.gathok.bookoverview.util.NavScannerScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    navController: NavController,
    openDrawer: () -> Unit,
    state: OverviewState,
    onEvent: (OverviewEvent) -> Unit,
    authorToSearch: String? = null
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
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

    var showFilterDialog by remember { mutableStateOf(false) }
    var deletedBook by remember { mutableStateOf<Book?>(null) }

    if (showFilterDialog) {
        FilterDialog(
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
                            else -> SearchType.TITLE
                        }
                    }
                }
            },
            filterItemsList = listOf(stringResource(id = R.string.owned)+":", stringResource(id = R.string.read)+":"),
            typeItemsList = mapOf(
                stringResource(id = R.string.sort_by)+":" to listOf(stringResource(id = R.string.title), stringResource(id = R.string.author)),
                stringResource(id = R.string.search_by)+":" to listOf(stringResource(id = R.string.title), stringResource(id = R.string.author), stringResource(id = R.string.isbn))
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
                        imageVector = customIconFilterList(),
                        contentDescription = stringResource(id = R.string.filter),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                showFilterDialog = true
                            }
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

@Composable
fun BookItem(book: Book) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(1f)
        ) {
            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = stringResource(id = R.string.rating),
                        modifier = Modifier.size(16.dp),
                        tint =
                            if (book.rating != null && i <= book.rating!!) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                }
            }
            Row {
                Text(
                    text = book.title,
                    modifier = Modifier.padding(end = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row {
                Text(
                    text = book.author,
                    modifier = Modifier.padding(end = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column (
            horizontalAlignment = Alignment.End,
        ) {
            Icon(
                imageVector = customIconBook(),
                contentDescription = stringResource(id = R.string.owned),
                modifier = Modifier.size(24.dp),
                tint = if (book.possessionStatus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Icon(
                imageVector = customIconRead(),
                contentDescription = stringResource(id = R.string.read),
                modifier = Modifier.size(24.dp),
                tint = if (book.readStatus) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
        }
    }
}

@Composable
fun FilterDialog(
    onPositiveClick: () -> Unit,
    onFilterChange: (Int, Boolean?) -> Unit,
    onTypeChange: (Int, Int) -> Unit,
    filterItemsList: List<String>,
    typeItemsList: Map<String, List<String>>,
    filterStates: List<Boolean?>,
    typeStates: List<String>
) {
    CustomDialog (
        onDismissRequest = {  },
        title = { Text(stringResource(id = R.string.filter_options)) },
        rightIcon = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onPositiveClick()
                }
            )
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column {
            filterItemsList.forEach { item ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item,
                        fontSize = 16.sp
                    )
                    TripleSwitch(
                        onSelectionChange = { text ->
                            onFilterChange(
                                filterItemsList.indexOf(item), when (text.trim()) {
                                    "+" -> true
                                    "o" -> null
                                    "-" -> false
                                    else -> null
                                }
                            )
                        },
                        curState = when (filterStates[filterItemsList.indexOf(item)]) {
                            true -> " + "
                            false -> " - "
                            else -> " o "
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
            }
            typeItemsList.forEach { (label, options) ->
                var selectedOption by remember {
                    mutableStateOf(typeStates[typeItemsList.keys.indexOf(label)])
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DynamicSelectTextField(
                        selectedOption = selectedOption,
                        options = options,
                        label = label,
                        onValueChanged = { text ->
                            selectedOption = text
                            onTypeChange(typeItemsList.keys.indexOf(label), options.indexOf(text))
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DynamicSelectTextField(
    selectedOption: String,
    options: List<String>,
    label: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            readOnly = true,
            value = selectedOption,
            onValueChange = {},
            label = { Text(text = label) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option: String ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        expanded = false
                        onValueChanged(option)
                    }
                )
            }
        }
    }
}

@Composable
fun TripleSwitch(
    onSelectionChange: (String) -> Unit = {},
    states: List<String> = listOf(" + ", " o ", " - "),
    curState: String = " o "
) {
    var selectedOption by remember {
        mutableStateOf(curState)
    }
    val onSelectionChangeIntern = { text: String ->
        selectedOption = text
        onSelectionChange(text)
    }


    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .wrapContentSize()
    ) {
        Row(
            modifier = Modifier
                .clip(shape = RoundedCornerShape(12.dp))
                .background(Color.LightGray)
        ) {
            states.forEach { text->
                Text(
                    text = text,
                    color = Color.White,
                    modifier = Modifier
                        .clip(shape = RoundedCornerShape(12.dp))
                        .clickable {
                            onSelectionChangeIntern(text)
                        }
                        .background(
                            if (text == selectedOption) {
                                when (text.trim()) {
                                    "+" -> Color.Green
                                    "-" -> Color.Red
                                    else -> Color.Gray
                                }
                            } else {
                                Color.LightGray
                            }
                        )
                        .padding(
                            vertical = 0.dp,
                            horizontal = 3.dp,
                        ),
                    fontSize = 16.sp
                )
            }
        }
    }
}

/* RIP SwipeContainer
@Composable
fun <T> SwipeContainer(
    item: T,
    onDetails: (T) -> Unit,
    onDelete: (T) -> Unit,
    animationDuration: Int = 500,
    content: @Composable (T) -> Unit
) {
    var isRemoved by remember {
        mutableStateOf(false)
    }
    var isDetails by remember {
        mutableStateOf(false)
    }
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> {
                    isRemoved = true
                    false
                }
                SwipeToDismissBoxValue.StartToEnd -> {
                    isDetails = true
                    false
                }
                else -> false
            }
        }
    )

    LaunchedEffect(key1 = isRemoved, key2 = isDetails) {
        if(isRemoved) {
            delay(animationDuration.toLong())
            onDelete(item)
        } else if (isDetails) {
            onDetails(item)
        }
    }

    AnimatedVisibility(
        visible = !isRemoved && !isDetails,
        exit = shrinkVertically(
            animationSpec = tween(durationMillis = animationDuration),
            shrinkTowards = Alignment.Top
        ) + fadeOut()
    ) {
        SwipeToDismissBox(
            state = state,
            backgroundContent = {
                SwipeBackground(state)
            },
            enableDismissFromStartToEnd = true,
            enableDismissFromEndToStart = true,
            content = { content(item) }
        )
    }
}

@Composable
fun SwipeBackground(
    swipeToDismissBoxState: SwipeToDismissBoxState
) {
    val color = when (swipeToDismissBoxState.dismissDirection) {
        SwipeToDismissBoxValue.StartToEnd -> {
            MaterialTheme.colorScheme.primary
        }
        SwipeToDismissBoxValue.EndToStart -> {
            MaterialTheme.colorScheme.error
        }
        else -> Color.Transparent
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(16.dp),
        contentAlignment = when (swipeToDismissBoxState.dismissDirection) {
            SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
            SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
            else -> Alignment.Center
        }
    ) {
        if (swipeToDismissBoxState.dismissDirection != SwipeToDismissBoxValue.Settled) {
            Icon(
                imageVector = when (swipeToDismissBoxState.dismissDirection) {
                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Info
                    SwipeToDismissBoxValue.EndToStart -> customIconDelete()
                    SwipeToDismissBoxValue.Settled -> Icons.Default.Info
                },
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}*/

@Composable
fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    hint: String = stringResource(R.string.search)
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        placeholder = { Text(text = hint) },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search)) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = "Clear Search")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )
}

@Composable
fun AppIcon() {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Image (
            painter = painterResource(id = R.mipmap.ic_launcher_foreground),
            contentDescription = "Menu",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .scale(1.5f)
        )
    }
}
