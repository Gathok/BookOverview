@file:Suppress("UNCHECKED_CAST")

package de.gathok.bookoverview

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import de.gathok.bookoverview.add.AddScreen
import de.gathok.bookoverview.add.AddEvent
import de.gathok.bookoverview.add.AddState
import de.gathok.bookoverview.add.AddViewModel
import de.gathok.bookoverview.add.scanner.ScannerScreen
import de.gathok.bookoverview.data.BookDatabase
import de.gathok.bookoverview.data.MIGRATION_1_2
import de.gathok.bookoverview.details.DetailsEvent
import de.gathok.bookoverview.details.DetailsScreen
import de.gathok.bookoverview.details.DetailsState
import de.gathok.bookoverview.details.DetailsViewModel
import de.gathok.bookoverview.overview.OverviewEvent
import de.gathok.bookoverview.overview.OverviewScreen
import de.gathok.bookoverview.overview.OverviewState
import de.gathok.bookoverview.overview.OverviewViewModel
import de.gathok.bookoverview.settings.SettingsEvent
import de.gathok.bookoverview.settings.SettingsScreen
import de.gathok.bookoverview.settings.SettingsState
import de.gathok.bookoverview.settings.SettingsViewModel
import de.gathok.bookoverview.settings.trash.TrashScreen
import de.gathok.bookoverview.ui.theme.BookOverviewTheme
import de.gathok.bookoverview.util.Screen

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            BookDatabase::class.java,
            "books.db"
        )
            .addMigrations(MIGRATION_1_2)
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookOverviewTheme {
                val overviewState by overviewViewModel.state.collectAsState()
                val addState by addViewModel.state.collectAsState()
                val detailsState by detailsViewModel.state.collectAsState()
                val settingsState by settingsViewModel.state.collectAsState()
                NavGraph(overviewState, overviewViewModel::onEvent,
                    addState, addViewModel::onEvent,
                    detailsState, detailsViewModel::onEvent,
                    settingsState, settingsViewModel::onEvent)
            }
        }
    }
}

@Composable
fun NavGraph(overviewState: OverviewState, overviewEvent: (OverviewEvent) -> Unit,
             addState: AddState, addEvent: (AddEvent) -> Unit,
             detailsState: DetailsState, detailsEvent: (DetailsEvent) -> Unit,
             settingsState: SettingsState, settingsEvent: (SettingsEvent) -> Unit)
{
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.Overview.route) {
        composable(Screen.Overview.route) {
            OverviewScreen(navController, overviewState, overviewEvent)
        }
        composable(Screen.Add.route + "/{isbn}") { backStackEntry ->
            val arguments = backStackEntry.arguments
            var isbn = arguments?.getString("isbn")
            if (isbn == "null") isbn = null
            AddScreen(navController, addState, addEvent, isbn)
        }
        composable(Screen.Scanner.route) {
            ScannerScreen(navController)
        }
        composable(Screen.Details.route + "/{bookId}") { backStackEntry ->
            val arguments = backStackEntry.arguments
            val bookIdString = arguments?.getString("bookId")
            val bookId = bookIdString?.toInt()
            DetailsScreen(navController, detailsState, detailsEvent, bookId)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController, settingsState, settingsEvent)
        }
        composable(Screen.Trash.route) {
            TrashScreen(navController, settingsState, settingsEvent)
        }
    }
}
