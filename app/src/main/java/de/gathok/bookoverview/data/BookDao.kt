package de.gathok.bookoverview.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Upsert
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao{

    @Upsert
    suspend fun upsertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM book WHERE id = :id")
    suspend fun deleteBookById(id: Int)

    @Query("SELECT * FROM book WHERE id = :id")
    fun getBookById(id: Int): Flow<Book?>

    @Query("SELECT * FROM book WHERE isbn = :isbn")
    fun getBookByIsbn(isbn: String): Flow<Book?>

    @RawQuery(observedEntities = [Book::class])
    fun rawQuery(query: SupportSQLiteQuery): Flow<List<Book>>
}