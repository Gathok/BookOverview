@file:Suppress("UNCHECKED_CAST")

package de.gathok.bookoverview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import de.gathok.bookoverview.add.AddEvent
import de.gathok.bookoverview.add.AddScreen
import de.gathok.bookoverview.add.AddState
import de.gathok.bookoverview.add.AddViewModel
import de.gathok.bookoverview.add.scanner.ScannerScreen
import de.gathok.bookoverview.data.BookDatabase
import de.gathok.bookoverview.data.MIGRATION_1_2
import de.gathok.bookoverview.data.MIGRATION_1_3
import de.gathok.bookoverview.data.MIGRATION_1_4
import de.gathok.bookoverview.data.MIGRATION_1_5
import de.gathok.bookoverview.data.MIGRATION_2_3
import de.gathok.bookoverview.data.MIGRATION_2_4
import de.gathok.bookoverview.data.MIGRATION_2_5
import de.gathok.bookoverview.data.MIGRATION_3_4
import de.gathok.bookoverview.data.MIGRATION_3_5
import de.gathok.bookoverview.data.MIGRATION_4_5
import de.gathok.bookoverview.details.DetailsEvent
import de.gathok.bookoverview.details.DetailsScreen
import de.gathok.bookoverview.details.DetailsState
import de.gathok.bookoverview.details.DetailsViewModel
import de.gathok.bookoverview.overview.OverviewEvent
import de.gathok.bookoverview.overview.OverviewScreen
import de.gathok.bookoverview.overview.OverviewState
import de.gathok.bookoverview.overview.OverviewViewModel
import de.gathok.bookoverview.series.SeriesOverviewEvent
import de.gathok.bookoverview.series.SeriesOverviewScreen
import de.gathok.bookoverview.series.SeriesOverviewState
import de.gathok.bookoverview.series.SeriesOverviewViewModel
import de.gathok.bookoverview.settings.SettingsEvent
import de.gathok.bookoverview.settings.SettingsScreen
import de.gathok.bookoverview.settings.SettingsState
import de.gathok.bookoverview.settings.SettingsViewModel
import de.gathok.bookoverview.settings.trash.TrashScreen
import de.gathok.bookoverview.ui.theme.BookOverviewTheme
import de.gathok.bookoverview.util.NavAddScreen
import de.gathok.bookoverview.util.NavDetailsScreen
import de.gathok.bookoverview.util.NavOverviewScreen
import de.gathok.bookoverview.util.NavScannerScreen
import de.gathok.bookoverview.util.NavSeriesOverviewScreen
import de.gathok.bookoverview.util.NavSettingsScreen
import de.gathok.bookoverview.util.NavTrashScreen
import de.gathok.bookoverview.util.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            BookDatabase::class.java,
            "books.db"
        )
            .addMigrations(MIGRATION_1_2)
            .addMigrations(MIGRATION_1_3)
            .addMigrations(MIGRATION_2_3)
            .addMigrations(MIGRATION_1_4)
            .addMigrations(MIGRATION_2_4)
            .addMigrations(MIGRATION_3_4)
            .addMigrations(MIGRATION_1_5)
            .addMigrations(MIGRATION_2_5)
            .addMigrations(MIGRATION_3_5)
            .addMigrations(MIGRATION_4_5)
            .build()
    }
    private val overviewViewModel by viewModels<OverviewViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return OverviewViewModel(db.dao) as T
                }
            }
        }
    )
    private val addViewModel by viewModels<AddViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return AddViewModel(db.dao) as T
                }
            }
        }
    )
    private val detailsViewModel by viewModels<DetailsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DetailsViewModel(db.dao) as T
                }
            }
        }
    )
    private val settingsViewModel by viewModels<SettingsViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(db.dao) as T
                }
            }
        }
    )
    private val seriesOverviewViewModel by viewModels<SeriesOverviewViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SeriesOverviewViewModel(db.dao) as T
                }
            }
        }
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookOverviewTheme {
                val scope = rememberCoroutineScope()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

                val navController = rememberNavController()
                val selectedScreen = remember { mutableStateOf(Screen.Overview) }

                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            ModalDrawerSheet {
                                Spacer(modifier = Modifier.height(16.dp))
                                NavigationDrawerItem (
                                    label = { Text("Book Overview") },
                                    selected = selectedScreen.value == Screen.Overview,
                                    onClick = {
                                        selectedScreen.value = Screen.Overview
                                        navController.navigate(NavOverviewScreen())
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding),
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Book,
                                            contentDescription = "Book Overview"
                                        )
                                    },
                                )
                                NavigationDrawerItem (
                                    label = { Text("Book Series") },
                                    selected = selectedScreen.value == Screen.SeriesOverview,
                                    onClick = {
                                        selectedScreen.value = Screen.SeriesOverview
                                        navController.navigate(NavSeriesOverviewScreen)
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding),
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Book,
                                            contentDescription = "Book Overview"
                                        )
                                    },
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                NavigationDrawerItem (
                                    label = { Text("Settings") },
                                    selected = selectedScreen.value == Screen.Settings,
                                    onClick = {
                                        selectedScreen.value = Screen.Settings
                                        navController.navigate(NavSettingsScreen)
                                        scope.launch {
                                            drawerState.close()
                                        }
                                    },
                                    modifier = Modifier
                                        .padding(NavigationDrawerItemDefaults.ItemPadding),
                                    icon = {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = "Settings"
                                        )
                                    },
                                )
                            }
                        }
                    ) {
                        val overviewState by overviewViewModel.state.collectAsState()
                        val addState by addViewModel.state.collectAsState()
                        val detailsState by detailsViewModel.state.collectAsState()
                        val settingsState by settingsViewModel.state.collectAsState()
                        val seriesOverviewState by seriesOverviewViewModel.state.collectAsState()

                        NavGraph(
                            navController, { scope.launch { drawerState.open() } },
                            overviewState, overviewViewModel::onEvent,
                            addState, addViewModel::onEvent,
                            detailsState, detailsViewModel::onEvent,
                            settingsState, settingsViewModel::onEvent,
                            seriesOverviewState, seriesOverviewViewModel::onEvent
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController, openDrawer: () -> Unit,
             overviewState: OverviewState, overviewEvent: (OverviewEvent) -> Unit,
             addState: AddState, addEvent: (AddEvent) -> Unit,
             detailsState: DetailsState, detailsEvent: (DetailsEvent) -> Unit,
             settingsState: SettingsState, settingsEvent: (SettingsEvent) -> Unit,
             seriesOverviewState: SeriesOverviewState, seriesOverviewEvent: (SeriesOverviewEvent) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = NavOverviewScreen(),
    ) {
        composable<NavOverviewScreen> {
            val args = it.toRoute<NavOverviewScreen>()
            OverviewScreen(navController, openDrawer, overviewState, overviewEvent, args.authorToSearch)
        }
        composable<NavAddScreen> {
            val args = it.toRoute<NavAddScreen>()
            AddScreen(navController, addState, addEvent, args.isbn)
        }
        composable<NavScannerScreen> {
            ScannerScreen(navController)
        }
        composable<NavDetailsScreen> {
            val args = it.toRoute<NavDetailsScreen>()
            DetailsScreen(navController, detailsState, detailsEvent, args.bookId)
        }
        composable<NavSettingsScreen> {
            SettingsScreen(navController, openDrawer, settingsState, settingsEvent)
        }
        composable<NavTrashScreen> {
            TrashScreen(navController, settingsState, settingsEvent)
        }
        composable<NavSeriesOverviewScreen> {
            SeriesOverviewScreen(navController, openDrawer, seriesOverviewState, seriesOverviewEvent)
        }
    }
}
