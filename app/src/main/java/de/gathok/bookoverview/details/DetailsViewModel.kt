@file:OptIn(ExperimentalCoroutinesApi::class)

package de.gathok.bookoverview.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.data.BookDao
import de.gathok.bookoverview.data.BookSeries
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

    private val _bookSeriesList = dao.getAllBookSeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _book = _bookId
        .flatMapLatest { bookId ->
            dao.getBookById(bookId)
        }

    private val _state = MutableStateFlow(DetailsState())
    val state = combine(_book, _state, _bookSeriesList) { book, state, bookSeriesList ->
        val titleChanged = state.title != book?.title
        val authorChanged = state.author != book?.author
        val isbnChanged = state.isbn != book?.isbn
        val descriptionChanged = state.description != book?.description
        val possessionStatusChanged = state.possessionStatus != book?.possessionStatus
        val readStatusChanged = state.readStatus != book?.readStatus
        val ratingChanged = !((state.rating == book?.rating) ||
                (state.rating == 0 && book?.rating == null))
        val bookSeriesTitleChanged = (book?.bookSeriesId == null && state.bookSeriesTitle.isBlank()).xor(
            state.bookSeriesTitle.trim() != book?.bookSeriesId?.let { bookSeriesId ->
                bookSeriesList.find { it.id == bookSeriesId }?.title ?: "" }
        )
        val readingTimeChanged = state.readingTime != book?.readingTime

        state.copy(
            bookId = book?.id ?: 0,
            book = book,
            titleChanged = titleChanged,
            authorChanged = authorChanged,
            isbnChanged = isbnChanged,
            descriptionChanged = descriptionChanged,
            possessionStatusChanged = possessionStatusChanged,
            readStatusChanged = readStatusChanged,
            ratingChanged = ratingChanged,
            bookSeriesTitleChanged = bookSeriesTitleChanged,
            readingTimeChanged = readingTimeChanged,
            somethingChanged = titleChanged || authorChanged || isbnChanged || descriptionChanged ||
                    possessionStatusChanged || readStatusChanged || ratingChanged || bookSeriesTitleChanged
                    || readingTimeChanged,
            bookSeriesListMap = bookSeriesList.associateBy({ it.title }, { it.id }),
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

            is DetailsEvent.BookSeriesTitleChanged -> {
                _state.value = _state.value.copy(bookSeriesTitle = event.bookSeriesTitle)
            }

            is DetailsEvent.ReadingTimeChanged -> {
                _state.value = _state.value.copy(readingTime = event.readingTime)
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
                var bookSeriesId: Int? = null
                if (state.value.bookSeriesListMap.keys.contains(_state.value.bookSeriesTitle)) {
                    bookSeriesId = state.value.bookSeriesListMap[_state.value.bookSeriesTitle]
                } else if (_state.value.bookSeriesTitle.isNotBlank()) {
                    val bookSeries = BookSeries(title = _state.value.bookSeriesTitle)
                    viewModelScope.launch {
                        dao.upsertBookSeries(bookSeries)
                    }
                }

//                if (bookSeriesId == null) {
//                    dao.getAllBookSeries().collect { bookSeriesList ->
//                        bookSeriesId = bookSeriesList.find { it.title == _state.value.bookSeriesTitle }?.id
//                    }
//                }
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
                    description = _state.value.description,
                    bookSeriesId = bookSeriesId,
                    readingTime = _state.value.readingTime
                )
                viewModelScope.launch {
                    dao.upsertBook(book)
                }
            }

            DetailsEvent.ResetState -> {
                _state.value = DetailsState()
            }

            is DetailsEvent.ChangeBook -> {
                _state.value = _state.value.copy(
                    title = event.book.title,
                    author = event.book.author,
                    isbn = event.book.isbn,
                    possessionStatus = event.book.possessionStatus,
                    readStatus = event.book.readStatus,
                    rating = event.book.rating ?: 0,
                    description = event.book.description,
                    bookSeriesTitle = event.book.bookSeriesId?.let { bookSeriesId ->
                        state.value.bookSeriesListMap.entries.find { it.value == bookSeriesId }?.key ?: ""
                    } ?: "",
                    readingTime = event.book.readingTime,
                    coverImage = "",
                    onlineDescription = "",
                    isEditing = false
                )
            }
        }
    }
}
