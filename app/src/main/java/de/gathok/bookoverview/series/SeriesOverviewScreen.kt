package de.gathok.bookoverview.series

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.data.BookSeries
import de.gathok.bookoverview.series.util.SeriesDialog
import de.gathok.bookoverview.ui.CustomTopBar
import de.gathok.bookoverview.ui.theme.BookOverviewTheme

@Composable
fun SeriesOverviewScreen(
    navController: NavController,
    openDrawer: () -> Unit,
    state: SeriesOverviewState,
    onEvent: (SeriesOverviewEvent) -> Unit,
) {

    var showSeriesDialog by remember { mutableStateOf(false) }

    if (showSeriesDialog) {
        val invalidNames = state.seriesList.map { it.title }.toMutableList()
        invalidNames.add(stringResource(R.string.no_series))
        SeriesDialog(
            onDismiss = {
                showSeriesDialog = false
            },
            onAdd = { series ->
                onEvent(SeriesOverviewEvent.AddSeries(series))
                showSeriesDialog = false
            },
            invalidNames = invalidNames.toList(),
        )
    }

    Scaffold (
        topBar = {
            CustomTopBar(
                navigationAction = openDrawer,
                title = {
                    Text(
                        text = stringResource(R.string.book_series),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            showSeriesDialog = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Series",
                        )
                    }
                },
            )
        },
        modifier = Modifier
            .fillMaxSize(),
    ) { pad ->
        Box (
            modifier = Modifier
                .padding(pad)
        ) {
            LazyColumn {
                items(state.seriesList) { series ->
                    BookSeriesItem(series)
                }
            }
        }
    }
}

@Composable
fun BookSeriesItem(
    series: BookSeries,
    modifier: Modifier = Modifier,
    count: Int? = null,
) {
    Row (
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Text(
            text = series.title + if (count != null) " ($count ${stringResource(R.string.books)})" else "",
            modifier = Modifier.padding(end = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview
@Composable
private fun BookSeriesItemPreview() {
    BookOverviewTheme (
        darkTheme = true
    ) {
        Surface (
            modifier = Modifier.fillMaxSize(),
        ) {
            BookSeriesItem(
                BookSeries(
                    title = "Harry Potter"
                ),
                count = 7
            )
        }
    }
}