package de.gathok.bookoverview.api

data class OpenLibraryResponse(
    val title: String,
    val authors: List<Author>,
    val description: Description?,
    val series: List<String>?,
    val numberOfPages: Int?,
)

data class Author(
    val name: String
)

data class Description(
    val type: String,
    val value: String
)