package de.gathok.bookoverview.details

data class DetailsState(
    var bookId: Int? = null,
    var title: String = "",
    val author: String = "",
    val isbn: String = "",
    var possessionStatus: Boolean = false,
    var readStatus: Boolean = false,
    var rating: Int = 0,
    var isEditing: Boolean = false
)
