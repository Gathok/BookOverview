package de.gathok.bookoverview.overview

import de.gathok.bookoverview.data.Book

data class OverviewState(
    val books: List<Book> = emptyList(),
    val sortType: SortType = SortType.TITLE,
    var possessionStatus: Boolean? = null,
    var readStatus: Boolean? = null
)
