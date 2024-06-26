package de.gathok.bookoverview.overview

sealed interface OverviewEvent {
    data class SortBookOverview(val sortType: SortType): OverviewEvent
}