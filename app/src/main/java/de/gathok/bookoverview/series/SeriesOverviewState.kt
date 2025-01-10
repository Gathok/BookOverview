package de.gathok.bookoverview.series

import de.gathok.bookoverview.data.BookSeries

data class SeriesOverviewState(
    val seriesList: List<BookSeries> = emptyList(),
    val searchQuery: String = "",
)
