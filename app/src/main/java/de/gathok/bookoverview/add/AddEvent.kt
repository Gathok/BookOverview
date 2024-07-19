package de.gathok.bookoverview.add

import de.gathok.bookoverview.data.BookSeries

sealed class AddEvent {
    data class TitleChanged(val title: String): AddEvent()
    data class AuthorChanged(val author: String): AddEvent()
    data class IsbnChanged(val isbn: String): AddEvent()
    data class PossessionStatusChanged(val possessionStatus: Boolean): AddEvent()
    data class ReadStatusChanged(val readStatus: Boolean): AddEvent()
    data class RatingChanged(val rating: Int): AddEvent()

    data class SetOnlineDescription(val onlineDescription: String): AddEvent()
    data class SetPageCount(val pageCount: Int): AddEvent()
    data class SetImageUrl(val imageUrl: String): AddEvent()
    data class SetSeries(val place: Int, val series: BookSeries): AddEvent()

    object AddBook: AddEvent()
    object ClearFields: AddEvent()
}