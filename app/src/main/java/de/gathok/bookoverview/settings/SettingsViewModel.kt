package de.gathok.bookoverview.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.data.BookDao
import de.gathok.bookoverview.util.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _state = MutableStateFlow(SettingsState())

    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())


    fun onEvent(event: SettingsEvent) {
        when(event) {
            SettingsEvent.SettingsOpened -> {
                // Set current screen to Settings
                _state.value = _state.value.copy(curScreen = Screen.Settings)
                // set trashIsEmpty
                viewModelScope.launch {
                    dao.getTrash().collect{ trash ->
                        _state.value = _state.value.copy(trashIsEmpty = trash.isEmpty())
                    }
                }
            }
            // for /settings/trash
            SettingsEvent.OnTrashClicked -> {
                // Show Trash Screen
                viewModelScope.launch {
                    dao.getTrash().collect { trashedBooks ->
                        _state.value = _state.value.copy(trashedBooks = trashedBooks)
                    }
                }
                _state.value = _state.value.copy(curScreen = Screen.Trash)
            }
            is SettingsEvent.OnTrashRestoreClicked -> {
                viewModelScope.launch {
                    dao.restoreBookById(event.book.id)
                    dao.getTrash().collect { trashedBooks ->
                        _state.value = _state.value.copy(trashedBooks = trashedBooks)
                    }
                }
            }
            is SettingsEvent.OnTrashDeleteClicked -> {
                viewModelScope.launch {
                    dao.deleteBook(event.book)
                    dao.getTrash().collect { trashedBooks ->
                        _state.value = _state.value.copy(trashedBooks = trashedBooks)
                    }
                }
            }
        }
    }
}