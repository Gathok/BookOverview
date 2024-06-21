package de.gathok.bookoverview

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.gathok.bookoverview.ui.theme.BookOverviewTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(navController: NavController? = null) {
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("Add Book")
                }
            )
        }
    ) { pad ->
        AddBookForm(pad)
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun AddBookForm(pad: PaddingValues) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var possessionStatus by remember { mutableStateOf(false) }
    var readStatus by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
//    var rating by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(12.dp, pad.calculateTopPadding(), 12.dp, 12.dp)
            .verticalScroll(scrollState)
            .clip(shape = RoundedCornerShape(10.dp))
    ) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = author,
            onValueChange = { author = it },
            label = { Text("Author") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = isbn,
            onValueChange = { isbn = it },
            label = { Text("ISBN") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = possessionStatus,
                    onCheckedChange = { possessionStatus = it },
                )
                Text("Owned")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Checkbox(
                    checked = readStatus,
                    onCheckedChange = { readStatus = it },
                )
                Text("Read")
            }
        }
//        Spacer(modifier = Modifier.height(16.dp))
//        Row {
//            Text(text = "Rating:")
//            StarRating(rating = rating, onValueChange = { rating = it })
//        }

        Button(onClick = {
            // TODO: Save book to database
        }) {
            Text("Submit")
        }
    }
}

//@Composable
//fun StarRating(rating: MutableState<Int>, onValueChange: (Int) -> Unit, maxRating: Int = 5) {
//    Row {
//        for (i in 1..maxRating) {
//            Icon(
//                imageVector = if (i <= rating.value) Icons.Filled.Star else Icons.Outlined.Star,
//                contentDescription = "Star",
//                tint = if (i <= rating.value) Color.Yellow else Color.Gray,
//                modifier = Modifier.clickable { onValueChange(i) }
//            )
//        }
//    }
//}

@Preview
@Composable
fun AddBookScreenPreview() {
    BookOverviewTheme {
        AddBookScreen()
    }
}
