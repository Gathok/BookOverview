package de.gathok.bookoverview.settings.import

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import de.gathok.bookoverview.R
import de.gathok.bookoverview.ui.CustomDialog
import de.gathok.bookoverview.ui.OutlinedText

@Composable
fun ImportDialog(
    onDismiss: () -> Unit,
    onFinish: (uri: Uri) -> Unit,
) {
    val context = LocalContext.current

    var uri by remember {
        mutableStateOf(null as Uri?)
    }
    var fileName by remember {
        mutableStateOf("")
    }

    // Intent for selecting a file to import data
    val selectFileIntent = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                if (intent != null) {
                    intent.data?.let { newUri ->
                        uri = newUri
                        context.contentResolver.query(newUri, null, null, null, null)?.use { cursor ->
                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            cursor.moveToFirst()
                            fileName = cursor.getString(nameIndex)
                        }
                    } ?: run {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_no_file_selected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    )

    CustomDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(id = R.string.import_data),
                style = MaterialTheme.typography.headlineMedium,
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
                imageVector = Icons.Default.Check,
                contentDescription = "Confirm",
                tint = if (uri != null) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                },
                modifier = Modifier.clickable {
                    if (uri != null) {
                        onFinish(uri!!)
                    }
                }
            )
        },
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "text/comma-separated-values"
                        putExtra(
                            Intent.EXTRA_MIME_TYPES,
                            arrayOf("application/json", "text/comma-separated-values")
                        )
                        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                    }
                    selectFileIntent.launch(intent)
                },
        ) {
            OutlinedText(
                text = { Text (
                    text =fileName.ifEmpty { stringResource(id = R.string.no_file_selected) },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (fileName.isEmpty()) {
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    } else MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )},
                label = { Text(stringResource(id = R.string.file)) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.FileCopy,
                        contentDescription = "Open file",
                    )
                }
            )
        }
    }
}