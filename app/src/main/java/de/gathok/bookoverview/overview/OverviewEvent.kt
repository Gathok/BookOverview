package de.gathok.bookoverview.overview

sealed interface OverviewEvent {
    data class ChangePossessionStatus(val possessionStatus: Boolean?): OverviewEvent
    data class ChangeReadStatus(val readStatus: Boolean?): OverviewEvent
    data class ChangeFilterList(val possessionStatus: Boolean?, val readStatus: Boolean?): OverviewEvent
    data class ChangeSortType(val sortType: SortType): OverviewEvent
}