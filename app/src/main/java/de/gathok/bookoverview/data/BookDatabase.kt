package de.gathok.bookoverview.data

import androidx.room.Database
import androidx.room.RoomDatabase
import de.gathok.bookoverview.Book

@Database(
    entities = [Book::class],
    version = 1,
    exportSchema = false
)
abstract class BookDatabase: RoomDatabase() {

    abstract val dao: BookDao
}