package de.gathok.bookoverview.series

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.Alignment
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
import de.gathok.bookoverview.ui.swipeContainer.ActionIcon
import de.gathok.bookoverview.ui.swipeContainer.SwipeItem
import de.gathok.bookoverview.ui.theme.BookOverviewTheme

@Composable
fun SeriesOverviewScreen(
    navController: NavController,
    openDrawer: () -> Unit,
    state: SeriesOverviewState,
    onEvent: (SeriesOverviewEvent) -> Unit,
) {

    var showSeriesDialog by remember { mutableStateOf(false) }

    var seriesToEdit by remember { mutableStateOf<BookSeries?>(null) }

    if (showSeriesDialog) {
        val invalidNames = state.seriesList.map { it.title }.toMutableList()
        invalidNames.remove(seriesToEdit?.title)
        invalidNames.add(stringResource(R.string.no_series))
        SeriesDialog(
            onDismiss = {
                showSeriesDialog = false
                seriesToEdit = null
            },
            onAdd = { series ->
                onEvent(SeriesOverviewEvent.SubmitSeries(series))
                showSeriesDialog = false
                seriesToEdit = null
            },
            seriesToEdit = seriesToEdit,
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
                    SwipeItem (
                        leftOptions = {
                            ActionIcon(
                                onClick = {
                                    seriesToEdit = series
                                    showSeriesDialog = true
                                },
                                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                                icon = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        },
                        rightOptions = {
                            ActionIcon(
                                onClick = {
                                    onEvent(SeriesOverviewEvent.DeleteSeries(series))
                                },
                                backgroundColor = MaterialTheme.colorScheme.errorContainer,
                                icon = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                            )
                        },
                    ) {
                        BookSeriesItem(series)
                    }
                    Row (
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                    ) {
                        Spacer(modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f)),
                        )
                    }
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
            .padding(12.dp, 4.dp)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column (
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = series.title,
                modifier = Modifier.padding(end = 8.dp),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = series.description.ifBlank { stringResource(R.string.no_description) },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = count?.toString() ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
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
            Column {
                BookSeriesItem(
                    series = BookSeries(
                        title = "Series Title",
                        description = "Series Description",
                    ),
                    count = 5,
                )
                BookSeriesItem(
                    series = BookSeries(
                        title = "Series2",
                        description = "Extra long and very specific and spicy series description with some extra salt and pepper",
                    ),
                    count = 14,
                )
            }
        }
    }
}