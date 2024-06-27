@file:OptIn(ExperimentalPermissionsApi::class)

package de.gathok.bookoverview.add

import androidx.camera.core.ExperimentalGetImage
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import de.gathok.bookoverview.R
import de.gathok.bookoverview.ui.theme.ratingStars
import de.gathok.bookoverview.util.Screen

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddScreen(navController: NavController, state: AddState, onEvent: (AddEvent) -> Unit,
              isbnFromNav: String? = null) {
    Scaffold (
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
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
                        onEvent(AddEvent.AddBook)
                        navController.navigateUp()
                    }) {
                        Icon(Icons.Filled.Check, contentDescription = stringResource(R.string.submit))
                    }
                }
            )
        }
    ) { pad ->
        val scrollState = rememberScrollState()
        val cameraPermission = rememberPermissionState(android.Manifest.permission.CAMERA)

        LaunchedEffect (key1 = isbnFromNav) {
            if (isbnFromNav != null) {
                onEvent(AddEvent.IsbnChanged(isbnFromNav))
            }
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
            OutlinedTextField(
                value = state.isbn,
                onValueChange = {
                    onEvent(AddEvent.IsbnChanged(it))
                },
                label = { Text(stringResource(R.string.isbn)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        if (cameraPermission.status.isGranted) {
                            navController.navigate(Screen.Scanner.route)
                        } else {
                            cameraPermission.launchPermissionRequest()
                        }
                    }) {
                        Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.scan))
                    }
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

@Composable
fun RatingBar(
    max: Int = 5,
    current: Int,
    onRatingChanged: (Int) -> Unit,
    enabled: Boolean = true
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
                    contentDescription = "${stringResource(id = R.string.rating)}: $i",
                    modifier = Modifier
                        .clickable {
                            if (enabled) {
                                onRatingChanged(i)
                            }
                        }
                        .padding(horizontal = 4.dp)
                        .weight(1f) // This will divide the available space equally between the stars
                        .aspectRatio(1f), // This will make the stars square
                    tint = if (i <= current) ratingStars else Color.Gray,
                )
            }
        }
        Row {
            Text(
                text = "${stringResource(id = R.string.rating)}: $current",
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
