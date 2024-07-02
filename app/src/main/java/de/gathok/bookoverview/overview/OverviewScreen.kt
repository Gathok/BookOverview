@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)

package de.gathok.bookoverview.overview

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.ui.customIconBook
import de.gathok.bookoverview.ui.customIconDelete
import de.gathok.bookoverview.ui.customIconFilterList
import de.gathok.bookoverview.ui.theme.ratingStars
import de.gathok.bookoverview.util.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    navController: NavController,
    state: OverviewState,
    onEvent: (OverviewEvent) -> Unit
) {
    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var showFilterDialog by remember { mutableStateOf(false) }
    var deletedBook by remember { mutableStateOf<Book?>(null) }

    if (showFilterDialog) {
        FilterDialog(
            onPositiveClick = {
                showFilterDialog = false
                onEvent(OverviewEvent.ChangeFilterList(
                    state.possessionStatus,
                    state.readStatus,
                    state.sortType,
                    state.searchType
                ))
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
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Settings.route)
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(id = R.string.settings))
                    }
                }
            )
        },
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
                                navController.navigate(Screen.Add.route + "/null")
                            },
                            onLongClick = {
                                navController.navigate(Screen.Scanner.route)
                            }
                        )
                )
            }
        },
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
                        onValueChange = { onEvent(OverviewEvent.ChangeSearchQuery(it)) },
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
                        SwipeContainer(
                            item = book,
                            onDetails = {
                                navController.navigate("${Screen.Details.route}/${it.id}")
                            },
                            onDelete = {
                                onEvent(OverviewEvent.DeleteBook(it))
                                deletedBook = it
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
                    .padding(bottom = 64.dp, start = 16.dp, end = 16.dp),
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
            Text(
                text = book.getRatingString(),
                modifier = Modifier.padding(end = 8.dp),
                color = when (book.rating) {
                    in 1..5 -> ratingStars
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                }
            )
            Text(
                text = book.title,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = book.author,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column (
            horizontalAlignment = Alignment.End,
        ) {
            if (book.possessionStatus) {
                Icon(
                    imageVector = customIconBook(),
                    contentDescription = stringResource(id = R.string.owned),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = " ",
                )
            }
            if (book.readStatus) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = stringResource(id = R.string.read),
                    tint = Color.Green,
                )
            }
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
    AlertDialog(
        onDismissRequest = {  },
        title = { Text(stringResource(id = R.string.filter_options)) },
        text = {
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
                                onFilterChange(filterItemsList.indexOf(item), when (text.trim()) {
                                    "+" -> true
                                    "o" -> null
                                    "-" -> false
                                    else -> null
                                })
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
        },
        confirmButton = {
            TextButton(onClick = {
                onPositiveClick()
            }) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
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
            Color.Green
        }
        SwipeToDismissBoxValue.EndToStart -> {
            Color.Red
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
}

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
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
    )
}
