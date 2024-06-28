@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package de.gathok.bookoverview.overview

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.ui.theme.ratingStars
import de.gathok.bookoverview.util.Screen
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    navController: NavController,
    state: OverviewState,
    onEvent: (OverviewEvent) -> Unit
) {
    var showFilterDialog by remember { mutableStateOf(false) }

    if (showFilterDialog) {
        FilterDialog(
            onPositiveClick = {
                showFilterDialog = false
                onEvent(OverviewEvent.ChangeFilterList(
                    state.possessionStatus,
                    state.readStatus
                ))
            },
            onSelectionChange = { index, value ->
                when (index) {
                    0 -> state.possessionStatus = value
                    1 -> state.readStatus = value
                }
            },
            states = listOf(state.possessionStatus, state.readStatus),
            itemsList = listOf(stringResource(id = R.string.owned)+":", stringResource(id = R.string.read)+":")
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
                        // TODO("Show settings")
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = stringResource(id = R.string.settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.Add.route +  "/null") },
                modifier = Modifier
                    .padding(12.dp)) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_book))
            }
        }
    ) { pad ->
        Column (
            modifier = Modifier
                .padding(pad)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                SearchBar(
                    value = state.searchQuery,
                    onValueChange = { onEvent(OverviewEvent.ChangeSearchQuery(it)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp, end = 12.dp, bottom = 8.dp)
                )
                IconButton(onClick = {
                    showFilterDialog = true
                }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = stringResource(id = R.string.filter))
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(
                    items = state.books,
                    key = { it.id }
                ) { book ->
                    SwipeContainer(
                        item = book,
                        onDetails = {
                            navController.navigate("${Screen.Details.route}/${it.id}")
                        },
                        onDelete = {
                            onEvent(OverviewEvent.DeleteBook(it))
                        },
                    ) {
                        BookItem(book)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(128.dp))
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book) {
    Row (
        modifier = Modifier
            .padding(12.dp, 0.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(1f)
        ) {
            Text(
                text = book.getRatingString() ?: stringResource(id = R.string.no_rating),
                modifier = Modifier.padding(end = 8.dp),
                color = when (book.rating) {
                    in 1..5 -> ratingStars
                    else -> Color.Gray
                }
            )
            Text(
                text = book.title,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = book.author,
                modifier = Modifier.padding(end = 8.dp),
                fontSize = 12.sp
            )
        }
        Column (
            horizontalAlignment = Alignment.End,
        ) {
            if (book.possessionStatus) {
                Icon(
                    Icons.Filled.ShoppingCart,
                    contentDescription = stringResource(id = R.string.owned),
                    tint = Color.Gray,
                )
            } else {
                Text(
                    text = " ",
                )
            }
            if (book.readStatus) {
                Icon(
                    Icons.Filled.CheckCircle,
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
    onSelectionChange: (Int, Boolean?) -> Unit,
    itemsList: List<String> = listOf("Owned:", "Read:"),
    states: List<Boolean?> = listOf(null, null)
) {
    AlertDialog(
        onDismissRequest = {  },
        title = { Text(stringResource(id = R.string.filter_options)) },
        text = {
            Column {
                itemsList.forEach { item ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item, Modifier.padding(start = 8.dp))
                        TripleSwitch(
                            onSelectionChange = { text ->
                                onSelectionChange(itemsList.indexOf(item), when (text.trim()) {
                                    "+" -> true
                                    "o" -> null
                                    "-" -> false
                                    else -> null
                                })
                            },
                            curState = when (states[itemsList.indexOf(item)]) {
                                true -> " + "
                                false -> " - "
                                else -> " o "
                            }
                        )
                    }
                    Spacer(modifier = Modifier.padding(4.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onPositiveClick()
            }) {
                Text(stringResource(id = R.string.ok))
            }
        }
    )
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
                    true
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
        Icon(
            imageVector = when (swipeToDismissBoxState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Info
                SwipeToDismissBoxValue.EndToStart -> Icons.Default.Delete
                else -> Icons.Default.Settings
            },
            contentDescription = null,
            tint = Color.White
        )
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

//@Preview(showBackground = true, name = "BookOverviewScreen", group = "MainActivity")
//@Composable
//fun BookOverviewScreenPreview() {
//    FilterDialog(
//        showDialog = remember { mutableStateOf(true) },
//        onPositiveClick = {},
//        onNegativeClick = {},
//        onItemChecked = { _, _ -> }
//    )
//}