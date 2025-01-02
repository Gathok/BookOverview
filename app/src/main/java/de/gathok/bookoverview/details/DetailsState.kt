package de.gathok.bookoverview.details

import de.gathok.bookoverview.data.Book

data class DetailsState(
    val bookId: Int? = null,
    val book: Book? = null,

    val title: String = "",
    val titleChanged: Boolean = false,
    val author: String = "",
    val authorChanged: Boolean = false,
    val isbn: String = "",
    val isbnChanged: Boolean = false,
    val possessionStatus: Boolean = false,
    val possessionStatusChanged: Boolean = false,
    val readStatus: Boolean = false,
    val readStatusChanged: Boolean = false,
    val rating: Int = 0,
    val ratingChanged: Boolean = false,
    val description: String = "",
    val descriptionChanged: Boolean = false,
    val bookSeriesTitle: String = "",
    val bookSeriesTitleChanged: Boolean = false,

    val readingTime: Int? = null,
    val readingTimeChanged: Boolean = false,

    val somethingChanged: Boolean = false,

    val bookSeriesListMap: Map<String, Int> = emptyMap(),

    val isEditing: Boolean = false,
    val isDoubleIsbn: Boolean = false,

    val coverImage: String = "",
    val onlineDescription: String = "",
)
