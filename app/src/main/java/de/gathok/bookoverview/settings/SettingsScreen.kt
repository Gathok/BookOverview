@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package de.gathok.bookoverview.settings

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.gathok.bookoverview.R
import de.gathok.bookoverview.overview.AppIcon
import de.gathok.bookoverview.settings.import.ImportDialog
import de.gathok.bookoverview.ui.CustomTopBar
import de.gathok.bookoverview.ui.customIconDelete
import de.gathok.bookoverview.util.NavTrashScreen
import de.gathok.bookoverview.util.Screen

@Composable
fun SettingsScreen(
    navController: NavController,
    openDrawer: () -> Unit,
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {

    onEvent(SettingsEvent.SettingsOpened)

    LaunchedEffect(key1 = state.curScreen) {
        when (state.curScreen) {
            Screen.Trash -> {
                navController.navigate(NavTrashScreen)
            }
            else -> {
                // Do nothing
            }
        }
    }

    var versionName: String?

    val context = LocalContext.current
    try {
        val pInfo: PackageInfo =
            context.packageManager.getPackageInfo(context.packageName, 0)
        versionName = pInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        versionName = null
    }

    val saveExportFile = rememberLauncherForActivityResult(
        CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            println("Hello")
            try {
                if (state.allBooks.isNullOrEmpty()) {
                    Toast.makeText(context, context.getString(R.string.no_data_to_export), Toast.LENGTH_SHORT).show()
                    onEvent(SettingsEvent.ResetExportData)
                    onEvent(SettingsEvent.SetLoading(false))
                    return@rememberLauncherForActivityResult
                }
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    var output = "Title,Author,ISBN,PossessionStatus,ReadStatus,Rating,Description,DeletedSince,BookSeries,ReadingTime,\n"
                    val seriesIdToName = state.allBookSeries?.associate { series ->
                        series.id to series.title
                    } ?: emptyMap()
                    state.allBooks.forEach { book ->
                        output += "${formatForCsv(book.title)},${formatForCsv(book.author)}," +
                                "${formatForCsv(book.isbn)},${formatForCsv(book.possessionStatus)}," +
                                "${formatForCsv(book.readStatus)},${formatForCsv(book.rating)}," +
                                "${formatForCsv(book.description)},${formatForCsv(book.deletedSince)}," +
                                "${formatForCsv(seriesIdToName[book.bookSeriesId] ?: "")}," +
                                "${formatForCsv(book.readingTime)}\n"
                    }
                    outputStream.write(output.toByteArray())
                }
                Toast.makeText(context, context.getString(R.string.file_saved), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, context.getString(R.string.file_save_error), Toast.LENGTH_SHORT).show()
            }
        }
        onEvent(SettingsEvent.ResetExportData)
        onEvent(SettingsEvent.SetLoading(false))
    }

    LaunchedEffect(state.export) {
        if (state.export) {
            saveExportFile.launch("BookOverview-export.csv")
        }
    }

    var showImportDialog by remember { mutableStateOf(false) }

    if (showImportDialog) {
        ImportDialog(
            onDismiss = { showImportDialog = false },
            onFinish = { uri ->
                showImportDialog = false
                onEvent(SettingsEvent.SetLoading(true))
                onEvent(SettingsEvent.OnImport(uri, context))
                onEvent(SettingsEvent.SetLoading(false))
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CustomTopBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
                },
                navigationIcon = {
                    IconButton(
                        onClick = { openDrawer() }
                    ) {
                        AppIcon()
                    }
                }
            )
        },
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
        ) {
            SettingsItem(
                title = stringResource(R.string.trash)
                        + if (state.trashIsEmpty) " (${stringResource(id = R.string.empty)})" else "",
                description = stringResource(R.string.settings_trash_desc),
                icon = customIconDelete(),
                onClick = { onEvent(SettingsEvent.OnTrashClicked) },
                onLongClick = { }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            SettingsItem(
                title = stringResource(R.string.export_data),
                description = stringResource(R.string.settings_export_desc),
                icon = Icons.Default.Download,
                onClick = { onEvent(SettingsEvent.OnExportClicked)},
                onLongClick = { }
            )
            Spacer(modifier = Modifier.padding(8.dp))
            SettingsItem(
                title = stringResource(R.string.import_data),
                description = stringResource(R.string.settings_import_desc),
                icon = Icons.Default.Upload,
                onClick = { showImportDialog = true },
                onLongClick = { }
            )
            Spacer(modifier = Modifier.weight(1f)) // This spacer pushes the version info to the bottom
            if (versionName != null) {
                Text(
                    text = stringResource(R.string.settings_version, versionName),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    // Loading ----------------------------------------------------------------
    AnimatedVisibility(visible = state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

fun formatForCsv(value: Any?): String {
    return "\"$value\""
}

@Composable
fun SettingsItem(title: String, description: String, icon: ImageVector,
                 onClick: () -> Unit, onLongClick: () -> Unit) {
    Row (
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        verticalAlignment = Alignment.CenterVertically,
    ){
        Column {
            Icon (
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier
                    .size(48.dp)
            )
        }
        Column (
            modifier = Modifier
                .padding(start = 12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsItemPreview() {
    SettingsItem(
        title = "Trash",
        description = "Here you can restore deleted books or delete them permanently.",
        icon = Icons.Filled.Delete,
        onClick = {},
        onLongClick = {}
    )
}