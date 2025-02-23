package de.gathok.bookoverview.overview.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterListOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import de.gathok.bookoverview.R
import de.gathok.bookoverview.ui.CustomDialog

@Composable
fun FilterDialog(
    onDismiss: () -> Unit,
    onPositiveClick: () -> Unit,
    onReset: () -> Unit,
    onFilterChange: (Int, Boolean?) -> Unit,
    onTypeChange: (Int, Int) -> Unit,
    filterItemsList: List<String>,
    typeItemsList: Map<String, List<String>>,
    filterStates: List<Boolean?>,
    typeStates: List<String>
) {
    CustomDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = { Text(stringResource(id = R.string.filter_options)) },
        leftIcon = {
            Icon(
                imageVector = Icons.Default.FilterListOff,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onReset()
                }
            )
        },
        rightIcon = {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.clickable {
                    onPositiveClick()
                }
            )
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            filterItemsList.forEach { item ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item,
                        fontSize = 16.sp
                    )
                    TripleSwitch(
                        onSelectionChange = { text ->
                            onFilterChange(
                                filterItemsList.indexOf(item), when (text.trim()) {
                                    "+" -> true
                                    "o" -> null
                                    "-" -> false
                                    else -> null
                                }
                            )
                        },
                        curState = when (filterStates[filterItemsList.indexOf(item)]) {
                            true -> " + "
                            false -> " - "
                            else -> " o "
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
            }
            typeItemsList.forEach { (label, options) ->
                var selectedOption by remember {
                    mutableStateOf(typeStates[typeItemsList.keys.indexOf(label)])
                }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DynamicSelectTextField(
                        selectedOption = selectedOption,
                        options = options,
                        label = label,
                        onValueChanged = { text ->
                            selectedOption = text
                            onTypeChange(typeItemsList.keys.indexOf(label), options.indexOf(text))
                        }
                    )
                }
                Spacer(modifier = Modifier.padding(4.dp))
            }
        }
    }
}