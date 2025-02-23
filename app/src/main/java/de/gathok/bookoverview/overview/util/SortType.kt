package de.gathok.bookoverview.overview.util

enum class SortType {
    TITLE,
    AUTHOR;

    val queryValue: String
        get() = when (this) {
            TITLE -> "title"
            AUTHOR -> "author"
        }
}