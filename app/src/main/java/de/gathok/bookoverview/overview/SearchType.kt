package de.gathok.bookoverview.overview

enum class SearchType {
    TITLE,
    AUTHOR,
    ISBN;

    val queryValue: String
        get() = when (this) {
            TITLE -> "title"
            AUTHOR -> "author"
            ISBN -> "isbn"
        }
}