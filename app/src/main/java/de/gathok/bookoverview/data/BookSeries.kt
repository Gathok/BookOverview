package de.gathok.bookoverview.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookSeries(
    val name: String,
    var order: Map<Int, String>? = null,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
) {

    fun getPlaceByIsbn(isbn: String): String? {
        val order = order ?: return null
        return order.entries.find { it.value == isbn }?.key?.toString()
    }

    fun insertBook(place: Int, isbn: String) {
        val order = order?.toMutableMap() ?: mutableMapOf()
        order[order.size + 1] = isbn
        this.order = order
    }
}
