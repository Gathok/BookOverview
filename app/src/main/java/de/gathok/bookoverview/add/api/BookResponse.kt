package de.gathok.bookoverview.add.api

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
    val subtitle: String,
    val authors: List<String>,
    val publishedDate: String,
    val industryIdentifiers: List<IndustryIdentifier>
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)