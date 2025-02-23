package de.gathok.bookoverview.overview.util

enum class SearchType {
    TITLE,
    AUTHOR,
    ISBN,
    Series,
    SeriesId;

    val queryValue: String
        get() = when (this) {
            TITLE -> "title"
            AUTHOR -> "author"
            ISBN -> "isbn"
            Series -> "series"
            SeriesId -> "bookSeriesId"
        }
}