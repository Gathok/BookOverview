package de.gathok.bookoverview.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Book::class],
    version = 2,
    exportSchema = false
)
abstract class BookDatabase: RoomDatabase() {

    abstract val dao: BookDao
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE book ADD COLUMN deletedSince INTEGER NOT NULL DEFAULT 0")
    }
}