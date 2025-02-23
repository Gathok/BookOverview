package de.gathok.bookoverview.overview

import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.overview.util.SearchType
import de.gathok.bookoverview.overview.util.SortType

sealed interface OverviewEvent {
    data class ChangeFilterList(val possessionStatus: Boolean?, val readStatus: Boolean?,
                                val sortType: SortType, val searchType: SearchType
    ): OverviewEvent
    data object ResetFilter: OverviewEvent
    data class DeleteBook(val book: Book): OverviewEvent
    data class AddBook(val book: Book): OverviewEvent
    data class RestoreBook(val book: Book): OverviewEvent
    data class SearchQueryChanged(val searchQuery: String): OverviewEvent
}