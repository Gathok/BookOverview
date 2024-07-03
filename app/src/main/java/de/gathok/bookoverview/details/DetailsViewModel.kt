@file:OptIn(ExperimentalCoroutinesApi::class)

package de.gathok.bookoverview.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.data.BookDao
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class DetailsViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _bookId = MutableStateFlow(0)

    private val _book = _bookId
        .flatMapLatest { bookId ->
            dao.getBookById(bookId)
        }

    private val _state = MutableStateFlow(DetailsState())
    val state = combine(_book, _state) { book, state ->
        state.copy(
            bookId = book?.id ?: 0,
            book = book,
            titleChanged = state.title != book?.title,
            authorChanged = state.author != book?.author,
            isbnChanged = state.isbn != book?.isbn,
            descriptionChanged = state.description != book?.description,
            possessionStatusChanged = state.possessionStatus != book?.possessionStatus,
            readStatusChanged = state.readStatus != book?.readStatus,
            ratingChanged = !((state.rating == book?.rating) ||
                    (state.rating == 0 && book?.rating == null)),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DetailsState())

    fun onEvent(event: DetailsEvent) {
        when (event) {
            is DetailsEvent.ChangeBookId -> {
                _bookId.value = event.bookId
            }

            is DetailsEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }

            is DetailsEvent.AuthorChanged -> {
                _state.value = _state.value.copy(author = event.author)
            }

            is DetailsEvent.IsbnChanged -> {
                val newIsbn = event.isbn
                _state.value = _state.value.copy(isbn = newIsbn)
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
            }

            is DetailsEvent.ReadStatusChanged -> {
                _state.value = _state.value.copy(readStatus = event.readStatus)
            }

            is DetailsEvent.RatingChanged -> {
                _state.value = _state.value.copy(rating = event.rating)
            }

            is DetailsEvent.DescriptionChanged -> {
                _state.value = _state.value.copy(description = event.description)
            }

            is DetailsEvent.SetCoverImage -> {
                _state.value = _state.value.copy(coverImage = event.coverImage)
            }

            is DetailsEvent.SetOnlineDescription -> {
                _state.value = _state.value.copy(onlineDescription = event.onlineDescription)
            }

            DetailsEvent.SwitchEditing -> {
                _state.value = _state.value.copy(isEditing = !_state.value.isEditing)
            }

            DetailsEvent.UpdateBook -> {
                val book = Book(
                    id = _bookId.value,
                    title = _state.value.title,
                    author = _state.value.author,
                    isbn = _state.value.isbn,
                    possessionStatus = _state.value.possessionStatus,
                    readStatus = _state.value.readStatus,
                    rating = when (_state.value.rating) {
                        0 -> null
                        else -> _state.value.rating
                    },
                    description = _state.value.description
                )

                viewModelScope.launch {
                    dao.upsertBook(book)
                }
            }

            DetailsEvent.ResetState -> {
                _state.value = DetailsState()
            }
        }
    }
}
