package de.gathok.bookoverview.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults.DecorationBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.VisualTransformation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutlinedText(
    text: @Composable () -> Unit,
    label: @Composable () -> Unit = {},
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    DecorationBox(
        value = "Value",
        innerTextField = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column (
                    modifier = Modifier
                        .weight(1f)
                ) {
                    text()
                }
                Column {
                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                }
            }
        },
        enabled = true,
        singleLine = true,
        visualTransformation = VisualTransformation.None,
        interactionSource = remember { MutableInteractionSource() },
        label = label,
    )
}