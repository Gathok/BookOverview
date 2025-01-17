package de.gathok.bookoverview.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.data.BookDao
import de.gathok.bookoverview.util.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _allBooks = dao.getAllBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _allBookSeries = dao.getAllBookSeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _state = MutableStateFlow(SettingsState())

    val state = combine(_state, _allBooks, _allBookSeries) { state, allBooks, allBookSeries ->
        state.copy(
            allBooks = allBooks,
            allBookSeries = allBookSeries,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SettingsState())

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
            // Trash ------------------------------------------------
            is SettingsEvent.OnTrashDeleteClicked -> {
                viewModelScope.launch {
                    dao.deleteBook(event.book)
                    dao.getTrash().collect { trashedBooks ->
                        _state.value = _state.value.copy(trashedBooks = trashedBooks)
                    }
                }
            }
            SettingsEvent.OnTrashDeleteAllClicked -> {
                viewModelScope.launch {
                    dao.emptyTrash()
                    _state.value = _state.value.copy(trashedBooks = emptyList())
                }
            }
            SettingsEvent.OnTrashRestoreAllClicked -> {
                viewModelScope.launch {
                    dao.restoreAllTrash()
                    _state.value = _state.value.copy(trashedBooks = emptyList())
                }
            }
            // Import/Export ------------------------------------------------
            SettingsEvent.OnExportClicked -> {
                _state.value = _state.value.copy(
                    export = true,
                )
            }
            is SettingsEvent.OnImport -> {
                event.context.contentResolver.openInputStream(event.uri)?.also {
                    val content = it.readBytes().decodeToString()
                    println("Content: $content")
                    val newBooks = readCsv(content)
                    println("New Books: $newBooks")
                    newBooks.forEach { book ->
                        println("Adding book: $book")
                        viewModelScope.launch {
                            dao.upsertBook(book)
                        }
                    }
                }?.close() ?: run {
                    Log.e("SettingsViewModel", "onImport: Error opening file")
                }
            }
            SettingsEvent.ResetExportData -> {
                _state.value = _state.value.copy(
                    export = false,
                    allBooks = emptyList(),
                    allBookSeries = emptyList()
                )
            }
            is SettingsEvent.SetLoading -> {
                _state.value = _state.value.copy(isLoading = event.isLoading)
            }
        }
    }

    private fun readCsv(content: String): List<Book> {
        val lines = content.split("\n")
        val books = mutableListOf<Book>()

        for (line in lines.subList(1, lines.size - 1)) {
            val values = line.substring(1, line.length - 1).split("\",\"")

            if (values.size == 10) {
                val book = Book(
                    title = values[0],
                    author = values[1],
                    isbn = values[2],
                    possessionStatus = values[3].toBoolean(),
                    readStatus = values[4].toBoolean(),
                    rating = values[5].toIntOrNull(),
                    description = values[6],
                    deletedSince = values[7].toLong(),
                    bookSeriesId = null,
                    readingTime = values[9].toIntOrNull()
                )
                books.add(book)
            }
        }
        return books
    }
}
