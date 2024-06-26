package de.gathok.bookoverview.overview

import de.gathok.bookoverview.Book

data class OverviewState(
    val books: List<Book> = emptyList(),
    val sortType: SortType = SortType.TITLE
)
