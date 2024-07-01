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
            is DetailsEvent.FetchBook -> {
                viewModelScope.launch {
                    dao.getBookById(event.bookId).collect { book ->
                        if (book != null) {
                            _state.value = _state.value.copy(
                                bookId = book.id,
                                book = book,
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

            is DetailsEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
                if (event.title != _state.value.book?.title) {
                    _state.value = _state.value.copy(titleChanged = true)
                } else {
                    _state.value = _state.value.copy(titleChanged = false)
                }
            }
            is DetailsEvent.AuthorChanged -> {
                _state.value = _state.value.copy(author = event.author)
                if (event.author != _state.value.book?.author) {
                    _state.value = _state.value.copy(authorChanged = true)
                } else {
                    _state.value = _state.value.copy(authorChanged = false)
                }
            }
            is DetailsEvent.IsbnChanged -> {
                val newIsbn = event.isbn
                _state.value = _state.value.copy(isbn = newIsbn)
                if (newIsbn != _state.value.book?.isbn) {
                    _state.value = _state.value.copy(isbnChanged = true)
                } else {
                    _state.value = _state.value.copy(isbnChanged = false)
                }
                //check if isbn is already in the database
                viewModelScope.launch {
                    dao.getBookByIsbn(newIsbn).collect { book ->
                        _state.value = _state.value
                            .copy(isDoubleIsbn = (book != null && book.id != _state.value.bookId))
                    }
                }
            }
            is DetailsEvent.PossessionStatusChanged -> {
                _state.value = _state.value.copy(possessionStatus = event.possessionStatus)
                if (event.possessionStatus != _state.value.book?.possessionStatus) {
                    _state.value = _state.value.copy(possessionStatusChanged = true)
                } else {
                    _state.value = _state.value.copy(possessionStatusChanged = false)
                }
            }
            is DetailsEvent.ReadStatusChanged -> {
                _state.value = _state.value.copy(readStatus = event.readStatus)
                if (event.readStatus != _state.value.book?.readStatus) {
                    _state.value = _state.value.copy(readStatusChanged = true)
                } else {
                    _state.value = _state.value.copy(readStatusChanged = false)
                }
            }
            is DetailsEvent.RatingChanged -> {
                _state.value = _state.value.copy(rating = event.rating)
                if (event.rating == _state.value.book?.rating ||
                    (event.rating == 0 && _state.value.book?.rating == null)) {
                    _state.value = _state.value.copy(ratingChanged = false)
                } else {
                    _state.value = _state.value.copy(ratingChanged = true)
                }
            }

            DetailsEvent.SwitchEditing -> {
                _state.value = _state.value.copy(isEditing = !_state.value.isEditing)
            }

            DetailsEvent.UpdateBook -> {
                val id = _state.value.bookId
                val title = _state.value.title
                val author = _state.value.author
                val isbn = _state.value.isbn
                val possessionStatus = _state.value.possessionStatus
                val readStatus = _state.value.readStatus
                val rating = _state.value.rating

                if (title.isBlank()) {
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

                _state.value = _state.value.copy(
                    book = book,
                    titleChanged = false,
                    authorChanged = false,
                    isbnChanged = false,
                    possessionStatusChanged = false,
                    readStatusChanged = false,
                    ratingChanged = false
                )
            }
            DetailsEvent.ResetState -> {
                _state.value = DetailsState()
            }
        }
    }
}
