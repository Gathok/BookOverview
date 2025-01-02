package de.gathok.bookoverview.details

import de.gathok.bookoverview.R

enum class EditType {
    TITLE,
    AUTHOR,
    ISBN,
    DESCRIPTION,
    BOOK_SERIES,
    READING_TIME;

    val getTitleStringId: Int
        get() = when(this) {
            TITLE -> R.string.title
            AUTHOR -> R.string.author
            ISBN -> R.string.isbn
            DESCRIPTION -> R.string.description
            BOOK_SERIES -> R.string.book_series
            READING_TIME -> R.string.reading_time
        }
}