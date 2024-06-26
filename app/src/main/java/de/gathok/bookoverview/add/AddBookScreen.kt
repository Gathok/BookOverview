package de.gathok.bookoverview.add

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookScreen(navController: NavController, state: AddState, onEvent: (AddEvent) -> Unit) {
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text("Add Book")
                }
            )
        }
    ) { pad ->
        val scrollState = rememberScrollState()

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
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.author,
                onValueChange = {
                    onEvent(AddEvent.AuthorChanged(it))
                },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = state.isbn,
                onValueChange = {
                    onEvent(AddEvent.IsbnChanged(it))
                },
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
                        checked = state.possessionStatus,
                        onCheckedChange = {
                            onEvent(AddEvent.PossessionStatusChanged(it))
                        },
                    )
                    Text("Owned")
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
                    Text("Read")
                }
            }

            Button(onClick = {
                onEvent(AddEvent.AddBook)
                // redirect to book overview screen
                navController.navigateUp()
            }) {
                Text("Submit")
            }
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
