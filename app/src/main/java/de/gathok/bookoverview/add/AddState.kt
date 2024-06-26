package de.gathok.bookoverview.add

data class AddState(
    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val possessionStatus: Boolean = false,
    val readStatus: Boolean = false
)
