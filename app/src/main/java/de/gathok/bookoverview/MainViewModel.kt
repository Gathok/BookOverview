package de.gathok.bookoverview

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.util.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(MainState())

    private val _selectedScreen = MutableStateFlow(Screen.Overview)

    val state = combine(_state, _selectedScreen) { state, selectedScreen ->
        state.copy(selectedScreen = selectedScreen)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MainState())

    fun setSelectedScreen(screen: Screen) {
        _selectedScreen.value = screen
    }
}