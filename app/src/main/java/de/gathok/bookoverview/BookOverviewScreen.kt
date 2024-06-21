package de.gathok.bookoverview

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import de.gathok.bookoverview.ui.theme.BookOverviewTheme
import de.gathok.bookoverview.ui.theme.ratingStars

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BookOverviewScreen(navController: NavController? = null) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Book Overview")
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
        val books = listOf(
            Book(
                title = "Harry Potter and the Philosopher's Stone",
                author = "J.K. Rowling",
                isbn = "978-0747532699",
                possessionStatus = true,
                readStatus = true,
                price = 6.99,
                rating = 5
            ),
            Book(
                title = "The Hobbit",
                author = "J.R.R. Tolkien",
                isbn = "978-0261102217",
                possessionStatus = true,
                readStatus = true,
                price = 7.99,
                rating = 5
            ),
            Book(
                title = "The Catcher in the Rye",
                author = "J.D. Salinger",
                isbn = "978-0241950432",
                possessionStatus = true,
                readStatus = true,
                price = 8.99,
                rating = 5
            )
        )
        LazyColumn (
            modifier = Modifier.fillMaxSize()
                .padding(pad)
        ) {
            items(books) { book ->
                BookItem(book)
            }
        }
    }
}

@Composable
fun BookItem(book: Book) {
    Row (
        modifier = Modifier
            .clickable { /* TODO: Handle click here */ }
            .padding(12.dp, 4.dp, 12.dp, 4.dp)
            .fillMaxWidth()
    ) {
        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(1f)
        ) {
            Text(
                text = book.getRating() ?: "No rating",
                modifier = Modifier.padding(end = 8.dp),
                color = when (book.rating) {
                    in 0..5 -> ratingStars
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

@Preview(showBackground = true, name = "BookItem", group = "MainActivity")
@Composable
fun BookItemPreview() {
    BookOverviewTheme {
        val book = Book(
            title = "Harry Potter and the Philosopher's Stone",
            author = "J.K. Rowling",
            isbn = "978-0747532699",
            possessionStatus = false,
            readStatus = true,
            price = 6.99,
            rating = 5
        )
        BookItem(book)
    }
}

@Preview(showBackground = true, name = "BookOverviewScreen", group = "MainActivity")
@Composable
fun BookOverviewScreenPreview() {
    BookOverviewScreen()
}