package de.gathok.bookoverview

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Book(
    val title: String,
    val author: String,
    val isbn: String,
    var possessionStatus: Boolean = false,
    var readStatus: Boolean = false,
    var rating: Int? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
) {

    fun getRatingString(): String {
        return when (rating) {
            0 -> "☆☆☆☆☆"
            1 -> "★☆☆☆☆"
            2 -> "★★☆☆☆"
            3 -> "★★★☆☆"
            4 -> "★★★★☆"
            5 -> "★★★★★"
            else -> "No rating"
        }
    }
}