package de.gathok.bookoverview.overview

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import de.gathok.bookoverview.Book
import de.gathok.bookoverview.add.AddEvent
import de.gathok.bookoverview.ui.theme.ratingStars
import de.gathok.bookoverview.util.Screen

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun OverviewScreen(
    navController: NavController? = null,
    state: OverviewState,
    onEvent: (OverviewEvent) -> Unit
) {
    var showDetailsDialog by remember { mutableStateOf(false) }
    var detailBook by remember { mutableStateOf<Book?>(null) }
    var showFilterDialog by remember { mutableStateOf(false) }

    if (showDetailsDialog) {
        AlertDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = { Text("Book Details") },
            text = {
                Text("ISBN: ${detailBook?.isbn ?: "Not found"}. More details soon.")
            },
            confirmButton = {
                Button(onClick = { showDetailsDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

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
            states = listOf(state.possessionStatus, state.readStatus)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Book Overview")
                },
                actions = {
                    IconButton(onClick = {
                        showFilterDialog = true
                    }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Filter")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController?.navigate(Screen.AddBook.route) },
                modifier = Modifier
                    .padding(12.dp)) {
                Icon(Icons.Filled.Add, contentDescription = "Add Book")
            }
        }
    ) { pad ->
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            items(state.books) { book ->
                BookItem(book) { clickedBook ->
                    detailBook = clickedBook
                    showDetailsDialog = true
                }
            }
        }
    }
}

@Composable
fun BookItem(book: Book, onBookClick: (Book) -> Unit) {
    Row (
        modifier = Modifier
            .clickable { onBookClick(book) }
            .padding(12.dp, 4.dp, 12.dp, 4.dp)
            .fillMaxWidth()
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
                    contentDescription = "Possessed",
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
                    contentDescription = "Read",
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
        title = { Text("Filter options") },
        text = {
            Column {
                itemsList.forEach { item ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = item, Modifier.padding(start = 8.dp))
                        TriStateToggle(
                            onSelectionChange = { text ->
                                onSelectionChange(itemsList.indexOf(item), when (text.trim()) {
                                    "+" -> true
                                    "o" -> null
                                    "-" -> false
                                    else -> null
                                })
                            },
                            curState = when (states[itemsList.indexOf(item)]) {
                                true -> " +"
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
                Text("Filter")
            }
        }
    )
}

@Composable
fun TriStateToggle(
    onSelectionChange: (String) -> Unit = {},
    states: List<String> = listOf(" +", " o ", " - "),
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