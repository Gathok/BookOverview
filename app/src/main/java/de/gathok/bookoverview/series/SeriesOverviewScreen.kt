package de.gathok.bookoverview.series

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.data.BookSeries
import de.gathok.bookoverview.series.util.BookSeriesItem
import de.gathok.bookoverview.series.util.SeriesDialog
import de.gathok.bookoverview.ui.CustomTopBar
import de.gathok.bookoverview.ui.swipeContainer.ActionIcon
import de.gathok.bookoverview.ui.swipeContainer.SwipeItem
import de.gathok.bookoverview.util.NavOverviewScreen

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
                        // Wrap the series item content with clickable to navigate to OverviewScreen with seriesToSearch.
                        BookSeriesItem(
                            series,
                            modifier = Modifier.clickable {
                                navController.navigate(
                                    NavOverviewScreen(
                                        authorToSearch = null,
                                        seriesToSearch = series.id
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}