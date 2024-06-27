package de.gathok.bookoverview.add

sealed class AddEvent {
    data class TitleChanged(val title: String): AddEvent()
    data class AuthorChanged(val author: String): AddEvent()
    data class IsbnChanged(val isbn: String): AddEvent()
    data class PossessionStatusChanged(val possessionStatus: Boolean): AddEvent()
    data class ReadStatusChanged(val readStatus: Boolean): AddEvent()
    data class RatingChanged(val rating: Int): AddEvent()

    object AddBook: AddEvent()
    object ClearFields: AddEvent()

    object ScanIsbn: AddEvent()
}