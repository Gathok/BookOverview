package de.gathok.bookoverview.api

data class BookResponse(
    val kind: String,
    val totalItems: Int,
    val items: List<BookItem>
)

data class BookItem(
    val kind: String,
    val id: String,
    val etag: String,
    val selfLink: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
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