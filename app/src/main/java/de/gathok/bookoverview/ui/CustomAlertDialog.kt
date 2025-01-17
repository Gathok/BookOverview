package de.gathok.bookoverview.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun CustomAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    title: @Composable () -> Unit,
    text: @Composable () -> Unit,
    onDismiss: (() -> Unit)? = null,
    noConfirmButton: Boolean = false
) {
    CustomDialog(
        onDismissRequest = onDismissRequest,
        leftIcon = {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        if (onDismiss != null) {
                            onDismiss()
                        } else {
                            onDismissRequest()
                        }
                    }
            )
        },
        title = title,
        rightIcon = {
            if (!noConfirmButton) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onConfirm() }
                )
            }
        },
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            text()
        }
    }
}