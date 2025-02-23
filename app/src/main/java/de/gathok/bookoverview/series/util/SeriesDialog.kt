package de.gathok.bookoverview.series.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.gathok.bookoverview.R
import de.gathok.bookoverview.data.BookSeries
import de.gathok.bookoverview.ui.CustomDialog
import de.gathok.bookoverview.ui.theme.BookOverviewTheme

@Composable
fun SeriesDialog(
    onDismiss: () -> Unit,
    onAdd: (BookSeries) -> Unit,
    seriesToEdit: BookSeries? = null,
    invalidNames: List<String> = listOf(stringResource(R.string.no_series)),
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    if (seriesToEdit != null) {
        title = seriesToEdit.title
        description = seriesToEdit.description
    }

    CustomDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (seriesToEdit != null) stringResource(R.string.edit_series)
                    else stringResource(R.string.add_series),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        leftIcon = {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable { onDismiss() }
            )
        },
        rightIcon = {
            Icon(
                imageVector = if (seriesToEdit != null) Icons.Default.Check
                    else Icons.Default.AddCircle,
                contentDescription = "Close",
                tint = if (!invalidNames.contains(title.trim())) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.clickable {
                    if (!invalidNames.contains(title.trim())) {
                        if (seriesToEdit != null) {
                            onAdd(
                                BookSeries(
                                    id = seriesToEdit.id,
                                    title = title,
                                    description = description
                                )
                            )
                        } else {
                            onAdd(
                                BookSeries(
                                    title = title,
                                    description = description
                                )
                            )
                        }
                    }
                }
            )
        }
    ) {
        if (invalidNames.contains(title.trim())) {
            Row {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .height(16.dp)
                )
                Text(
                    text = stringResource(R.string.name_already_in_use),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier
                    .fillMaxWidth(),
                isError = invalidNames.contains(title.trim()),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier
                    .fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun SeriesDialogPreview() {
    BookOverviewTheme (
        darkTheme = true
    ) {
        SeriesDialog(
            onDismiss = {},
            onAdd = {},
            invalidNames = listOf("Series 1", "Series 2")
        )
    }
}