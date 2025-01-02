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

    @Query("SELECT * FROM book WHERE deletedSince != 0")
    fun getTrash(): Flow<List<Book>>

    @Query("UPDATE book SET deletedSince = :currentTimestamp WHERE id = :id")
    suspend fun trashBookById(id: Int, currentTimestamp: Long = System.currentTimeMillis())

    @Query("UPDATE book SET deletedSince = 0 WHERE id = :id")
    suspend fun restoreBookById(id: Int)

    @Query("DELETE FROM book WHERE deletedSince != 0")
    suspend fun emptyTrash()

    @Query("UPDATE book SET deletedSince = 0 WHERE deletedSince != 0")
    suspend fun restoreAllTrash()

    @Query("SELECT * FROM book WHERE id = :id")
    fun getBookById(id: Int): Flow<Book?>

    @Query("SELECT * FROM book WHERE isbn = :isbn")
    fun getBookByIsbn(isbn: String): Flow<Book?>

    @Query("SELECT * FROM book WHERE isbn = :isbn AND deletedSince = 0")
    fun checkForDoubleIsbn(isbn: String): Flow<Book?>

    @Query("UPDATE book SET bookSeriesId = :bookSeriesId WHERE id = :id")
    suspend fun setBookSeriesId(id: Int, bookSeriesId: Int)

    @RawQuery(observedEntities = [Book::class])
    fun rawQuery(query: SupportSQLiteQuery): Flow<List<Book>>


    @Upsert
    suspend fun upsertBookSeries(bookSeries: BookSeries)

    @Delete
    suspend fun deleteBookSeries(bookSeries: BookSeries)

    @Query("SELECT * FROM bookseries WHERE id = :id")
    fun getBookSeriesById(id: Int): Flow<BookSeries?>

    @Query("SELECT * FROM bookseries WHERE title = :title")
    fun getBookSeriesByTitle(title: String): Flow<BookSeries?>

    @Query("SELECT * FROM bookseries")
    fun getAllBookSeries(): Flow<List<BookSeries>>
}