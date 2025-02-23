package de.gathok.bookoverview.series

import de.gathok.bookoverview.data.BookSeries

sealed interface SeriesOverviewEvent {
    data class SearchQueryChanged(val searchQuery: String): SeriesOverviewEvent
    data class SubmitSeries(val series: BookSeries): SeriesOverviewEvent
    data class DeleteSeries(val series: BookSeries): SeriesOverviewEvent
}
