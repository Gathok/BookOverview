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
import de.gathok.bookoverview.add.AddBookScreen
import de.gathok.bookoverview.add.AddEvent
import de.gathok.bookoverview.add.AddState
import de.gathok.bookoverview.add.AddViewModel
import de.gathok.bookoverview.data.BookDatabase
import de.gathok.bookoverview.overview.BookOverviewScreen
import de.gathok.bookoverview.overview.OverviewState
import de.gathok.bookoverview.overview.OverviewViewModel
import de.gathok.bookoverview.ui.theme.BookOverviewTheme
import de.gathok.bookoverview.util.Screen

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            BookDatabase::class.java,
            "books.db"
        ).build()
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookOverviewTheme {
                val overviewState by overviewViewModel.state.collectAsState()
                val addState by addViewModel.state.collectAsState()
                NavGraph(overviewState, addState, addViewModel::onEvent)
            }
        }
    }
}

@Composable
fun NavGraph(overviewState: OverviewState, addState: AddState, onEvent: (AddEvent) -> Unit){
    val navController = rememberNavController()
    NavHost(navController, startDestination = Screen.BookList.route) {
        composable(Screen.BookList.route) {
            // Your book list screen
            BookOverviewScreen(navController, overviewState)
        }
        composable(Screen.AddBook.route) {
            // Your add book screen
            AddBookScreen(navController, addState, onEvent)
        }
    }
}
