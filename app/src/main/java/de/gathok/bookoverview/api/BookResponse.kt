package de.gathok.bookoverview.api

data class GoogleBookResponse(
    val title: String,
    val authors: List<String>,
    val description: String,
    val pageCount: Int,
    val imageLinks: ImageLinks,
)

data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
)