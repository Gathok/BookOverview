package de.gathok.bookoverview.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.DateFormat.getDateInstance
import java.util.Date

@Entity
data class Book(
    val title: String,
    val author: String,
    val isbn: String,
    var possessionStatus: Boolean = false,
    var readStatus: Boolean = false,
    var rating: Int? = null,
    var description: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var deletedSince: Long = 0
) {

    fun getRatingString(): String {
        return when (rating) {
            1 -> "★☆☆☆☆"
            2 -> "★★☆☆☆"
            3 -> "★★★☆☆"
            4 -> "★★★★☆"
            5 -> "★★★★★"
            else -> "☆☆☆☆☆"
        }
    }

    fun getDeletedSinceString(): String {
        return if (deletedSince == 0L) {
            ""
        } else {
            getDateInstance().format(Date(deletedSince))
        }
    }
}