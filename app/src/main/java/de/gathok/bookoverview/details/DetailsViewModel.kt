package de.gathok.bookoverview.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.data.BookDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class DetailsViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _state = MutableStateFlow(DetailsState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailsState())

    fun onEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.AuthorChanged -> {
                _state.value = _state.value.copy(author = event.author)
            }
            DetailsEvent.ResetState -> {
                _state.value = DetailsState()
            }
            is DetailsEvent.IsbnChanged -> {
                _state.value = _state.value.copy(isbn = event.isbn)
            }
            is DetailsEvent.PossessionStatusChanged -> {
                _state.value = _state.value.copy(possessionStatus = event.possessionStatus)
            }
            is DetailsEvent.RatingChanged -> {
                _state.value = _state.value.copy(rating = event.rating)
            }
            is DetailsEvent.ReadStatusChanged -> {
                _state.value = _state.value.copy(readStatus = event.readStatus)
            }
            is DetailsEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }
            DetailsEvent.UpdateBook -> {
                val id = _state.value.bookId
                val title = _state.value.title
                val author = _state.value.author
                val isbn = _state.value.isbn
                val possessionStatus = _state.value.possessionStatus
                val readStatus = _state.value.readStatus
                val rating = _state.value.rating

                if (title.isBlank() || author.isBlank() || isbn.isBlank()) {
                    return
                }

                val book = Book(
                    id = id!!,
                    title = title,
                    author = author,
                    isbn = isbn,
                    possessionStatus = possessionStatus,
                    readStatus = readStatus,
                    rating = when (rating) {
                        0 -> null
                        else -> rating
                    }
                )

                viewModelScope.launch {
                    dao.upsertBook(book)
                }
            }
            DetailsEvent.SwitchEditing -> {
                _state.value = _state.value.copy(isEditing = !_state.value.isEditing)
            }
            is DetailsEvent.FetchBook -> {
                viewModelScope.launch {
                    dao.getBookById(event.bookId).collect { book ->
                        if (book != null) { // TODO("ig this is not the nicest way to handle this")
                            _state.value = _state.value.copy(
                                bookId = book.id,
                                title = book.title,
                                author = book.author,
                                isbn = book.isbn,
                                possessionStatus = book.possessionStatus,
                                readStatus = book.readStatus,
                                rating = book.rating ?: 0
                            )
                        }
                    }
                }
            }
        }
    }
}
