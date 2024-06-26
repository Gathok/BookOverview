package de.gathok.bookoverview.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import de.gathok.bookoverview.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao{

    @Upsert
    suspend fun upsertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM book ORDER BY title ASC")
    fun getBooksOrderedByTitle(): Flow<List<Book>>

    @Query("SELECT * FROM book ORDER BY author ASC")
    fun getBooksOrderedByAuthor(): Flow<List<Book>>
}