package de.gathok.bookoverview.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Book::class, BookSeries::class],
    version = 4,
    exportSchema = false
)
abstract class BookDatabase: RoomDatabase() {

    abstract val dao: BookDao
}

// Migrations to version 2
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE book ADD COLUMN deletedSince INTEGER NOT NULL DEFAULT 0")
    }
}

// Migrations to version 3
val MIGRATION_1_3 = object : Migration(1, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE book ADD COLUMN description TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE book ADD COLUMN deletedSince INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE book ADD COLUMN description TEXT NOT NULL DEFAULT ''")
    }
}

// Migrations to version 4
val MIGRATION_1_4 = object : Migration(1, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE book ADD COLUMN description TEXT NOT NULL DEFAULT ''")
        database.execSQL("ALTER TABLE book ADD COLUMN deletedSince INTEGER NOT NULL DEFAULT 0")
        database.execSQL("CREATE TABLE bookseries (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL DEFAULT '')")
        database.execSQL("ALTER TABLE book ADD COLUMN bookSeriesId INTEGER")
    }
}

val MIGRATION_2_4 = object : Migration(2, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE book ADD COLUMN description TEXT NOT NULL DEFAULT ''")
        database.execSQL("CREATE TABLE bookseries (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL DEFAULT '')")
        database.execSQL("ALTER TABLE book ADD COLUMN bookSeriesId INTEGER")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE bookseries (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT NOT NULL, description TEXT NOT NULL DEFAULT '')")
        database.execSQL("ALTER TABLE book ADD COLUMN bookSeriesId INTEGER")
    }
}