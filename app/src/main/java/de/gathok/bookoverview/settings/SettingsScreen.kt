@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package de.gathok.bookoverview.settings

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import de.gathok.bookoverview.ui.customIconDelete
import de.gathok.bookoverview.util.Screen

@Composable
fun SettingsScreen(navController: NavController, state: SettingsState, onEvent: (SettingsEvent) -> Unit) {

    onEvent(SettingsEvent.SettingsOpened)

    LaunchedEffect(key1 = state.curScreen) {
        when (state.curScreen) {
            Screen.Trash -> {
                navController.navigate("settings/trash")
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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
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