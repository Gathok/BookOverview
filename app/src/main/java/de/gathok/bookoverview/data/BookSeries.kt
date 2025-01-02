package de.gathok.bookoverview.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookSeries (
    val title: String,
    var description: String = "",
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
)