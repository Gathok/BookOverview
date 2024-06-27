package de.gathok.bookoverview.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.data.BookDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddViewModel (
    private val dao: BookDao
): ViewModel() {

    private val _state = MutableStateFlow(AddState())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AddState())

    fun onEvent(event: AddEvent) {
        when(event) {
            is AddEvent.TitleChanged -> {
                _state.value = _state.value.copy(title = event.title)
            }
            is AddEvent.AuthorChanged -> {
                _state.value = _state.value.copy(author = event.author)
            }
            is AddEvent.IsbnChanged -> {
                _state.value = _state.value.copy(isbn = event.isbn)
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
                    title = title,
                    author = author,
                    isbn = isbn,
                    possessionStatus = possessionStatus,
                    readStatus = readStatus,
                    rating = rating
                )

                viewModelScope.launch {
                    dao.upsertBook(book)
                }

                _state.value = AddState()
            }
            AddEvent.ClearFields -> {
                _state.value = AddState()
            }
        }
    }
}