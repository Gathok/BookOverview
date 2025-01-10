package de.gathok.bookoverview.add

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.data.BookDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _bookSeriesList = dao.getAllBookSeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _state = MutableStateFlow(AddState())

    val state = combine(_state, _bookSeriesList) { state, bookSeriesList ->
        state.copy(
            bookSeriesList = bookSeriesList
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddState())

    fun onEvent(event: AddEvent) {
        when(event) {
            is AddEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }
            is AddEvent.AuthorChanged -> {
                _state.value = _state.value.copy(author = event.author)
            }
            is AddEvent.IsbnChanged -> {
                val isbn = event.isbn.replace(Regex("\\D"), "")

                _state.value = _state.value.copy(isbn = isbn)
                //check if isbn is already in the database and if it is a valid isbn to show the complete button
                if (isbn.length == 13 && isbn.isDigitsOnly() && isbn.startsWith("978")) {
                    viewModelScope.launch {
                        dao.checkForDoubleIsbn(isbn).collect { book ->
                            _state.value = _state.value.copy(
                                isDoubleIsbn = book != null,
                                showCompleteWithIsbn = true
                            )
                        }
                    }
                } else {
                    _state.value = _state.value.copy(
                        isDoubleIsbn = false,
                        showCompleteWithIsbn = false
                    )
                }
            }
            is AddEvent.PossessionStatusChanged -> {
                _state.value = _state.value.copy(possessionStatus = event.possessionStatus)
            }
            is AddEvent.ReadStatusChanged -> {
                _state.value = _state.value.copy(readStatus = event.readStatus)
            }
            is AddEvent.RatingChanged -> {
                _state.value = _state.value.copy(rating = event.rating)
            }
            is AddEvent.AddBook -> {
                val title = _state.value.title.trim()
                if (title.isBlank()) return
                val author = _state.value.author.trim()
                val isbn = _state.value.isbn.trim()
                val possessionStatus = _state.value.possessionStatus
                val readStatus = _state.value.readStatus
                val rating = _state.value.rating

                val book = Book(
                    title = title,
                    author = author,
                    isbn = isbn,
                    possessionStatus = possessionStatus,
                    readStatus = readStatus,
                    rating = rating,
                    bookSeriesId = _state.value.bookSeries?.id
                )

                viewModelScope.launch {
                    dao.upsertBook(book)
                }

                _state.value = AddState()
            }
            is AddEvent.ClearFields -> {
                _state.value = AddState()
            }
            is AddEvent.BookSeriesChanged -> {
                _state.value = _state.value.copy(
                    bookSeries = event.bookSeries
                )
            }
        }
    }
}