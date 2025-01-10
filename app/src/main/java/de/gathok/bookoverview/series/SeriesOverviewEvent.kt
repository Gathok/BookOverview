package de.gathok.bookoverview.series

import de.gathok.bookoverview.data.BookSeries

sealed interface SeriesOverviewEvent {
    data class SearchQueryChanged(val searchQuery: String): SeriesOverviewEvent
    data class AddSeries(val series: BookSeries): SeriesOverviewEvent
}
