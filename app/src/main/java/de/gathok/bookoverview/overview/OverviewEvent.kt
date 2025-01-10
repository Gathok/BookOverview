package de.gathok.bookoverview.overview

import de.gathok.bookoverview.data.Book

sealed interface OverviewEvent {
    data class ChangeFilterList(val possessionStatus: Boolean?, val readStatus: Boolean?,
        val sortType: SortType, val searchType: SearchType): OverviewEvent
    data class DeleteBook(val book: Book): OverviewEvent
    data class AddBook(val book: Book): OverviewEvent
    data class RestoreBook(val book: Book): OverviewEvent
    data class SearchQueryChanged(val searchQuery: String): OverviewEvent
}