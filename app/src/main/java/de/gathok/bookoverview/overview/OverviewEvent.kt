package de.gathok.bookoverview.overview

import de.gathok.bookoverview.data.Book

sealed interface OverviewEvent {
    data class ChangePossessionStatus(val possessionStatus: Boolean?): OverviewEvent
    data class ChangeReadStatus(val readStatus: Boolean?): OverviewEvent
    data class ChangeFilterList(val possessionStatus: Boolean?, val readStatus: Boolean?): OverviewEvent
    data class ChangeSortType(val sortType: SortType): OverviewEvent
    data class DeleteBook(val book: Book): OverviewEvent
    data class AddBook(val book: Book): OverviewEvent
    data class ChangeSearchQuery(val searchQuery: String): OverviewEvent
}