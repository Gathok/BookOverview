package de.gathok.bookoverview.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = false
)
abstract class BookDatabase: RoomDatabase() {

    abstract val dao: BookDao
}