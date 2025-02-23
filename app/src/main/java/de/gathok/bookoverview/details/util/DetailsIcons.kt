@file:OptIn(ExperimentalFoundationApi::class)

package de.gathok.bookoverview.details.util

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.gathok.bookoverview.R
import de.gathok.bookoverview.details.DetailsEvent
import de.gathok.bookoverview.details.DetailsState
import de.gathok.bookoverview.ui.customIconRead

@Composable
fun PossessionIcon(state: DetailsState, onEvent: (DetailsEvent) -> Unit, fillWidth: Float = 0.4f) {

    val context = LocalContext.current
    val description = if (state.possessionStatus) {
        stringResource(R.string.owned)
    } else {
        stringResource(R.string.not_owned)
    }

    Icon(
        imageVector = Icons.Default.Book,
        contentDescription = stringResource(R.string.owned),
        modifier = Modifier
            .fillMaxWidth(fillWidth)
            .combinedClickable(
                onClick = {
                    if (state.isEditing) {
                        onEvent(DetailsEvent.PossessionStatusChanged(!state.possessionStatus))
                    } else {
                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
                    }
                },
                onLongClick = {
                    if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                    onEvent(DetailsEvent.PossessionStatusChanged(!state.possessionStatus))
                }
            ),
        tint = if (state.possessionStatus) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        }
    )
}

@Composable
fun ReadIcon(state: DetailsState, onEvent: (DetailsEvent) -> Unit, fillWidth: Float = 0.5f) {

    val context = LocalContext.current
    val description = if (state.readStatus) {
        stringResource(R.string.read)
    } else {
        stringResource(R.string.not_read)
    }

    Icon(
        imageVector = customIconRead(),
        contentDescription = stringResource(R.string.read),
        modifier = Modifier
            .fillMaxWidth(fillWidth)
            .combinedClickable(
                onClick = {
                    if (state.isEditing) {
                        onEvent(DetailsEvent.ReadStatusChanged(!state.readStatus))
                    } else {
                        Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
                    }
                },
                onLongClick = {
                    if (!state.isEditing) onEvent(DetailsEvent.SwitchEditing)
                    onEvent(DetailsEvent.ReadStatusChanged(!state.readStatus))
                }
            )
            .padding(top = 6.dp, bottom = 6.dp),
        tint = if (state.readStatus) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        },
    )
}