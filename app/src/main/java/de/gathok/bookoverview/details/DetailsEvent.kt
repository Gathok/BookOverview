package de.gathok.bookoverview.details

sealed class DetailsEvent {
    data class FetchBook(val bookId: Int): DetailsEvent()

    data class TitleChanged(val title: String): DetailsEvent()
    data class AuthorChanged(val author: String): DetailsEvent()
    data class IsbnChanged(val isbn: String): DetailsEvent()
    data class PossessionStatusChanged(val possessionStatus: Boolean): DetailsEvent()
    data class ReadStatusChanged(val readStatus: Boolean): DetailsEvent()
    data class RatingChanged(val rating: Int): DetailsEvent()

    data object SwitchEditing: DetailsEvent()

    data object UpdateBook: DetailsEvent()
    data object ResetState: DetailsEvent()
}