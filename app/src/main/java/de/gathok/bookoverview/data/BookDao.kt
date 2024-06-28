package de.gathok.bookoverview.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
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
    fun getBookById(id: Int): Flow<Book>

    @Query("SELECT * FROM book WHERE (:isOwned IS NULL OR possessionStatus = :isOwned)" +
            " AND (:isRead IS NULL OR readStatus = :isRead) ORDER BY :sortType ASC")
    fun getBooks(isOwned: Boolean?, isRead: Boolean?, sortType: String): Flow<List<Book>>

    @Query("SELECT * FROM book WHERE (:isOwned IS NULL OR possessionStatus = :isOwned)" +
            " AND (:isRead IS NULL OR readStatus = :isRead)" +
            " AND title LIKE '%' || :searchQuery || '%'" +
            " ORDER BY :sortType ASC")
    fun getBooksByTitle(isOwned: Boolean?, isRead: Boolean?, sortType: String, searchQuery: String): Flow<List<Book>>

    @Query("SELECT * FROM book WHERE (:isOwned IS NULL OR possessionStatus = :isOwned)" +
            " AND (:isRead IS NULL OR readStatus = :isRead)" +
            " AND author LIKE '%' || :searchQuery || '%'" +
            " ORDER BY :sortType ASC")
    fun getBooksByAuthor(isOwned: Boolean?, isRead: Boolean?, sortType: String, searchQuery: String): Flow<List<Book>>

    @Query("SELECT * FROM book WHERE (:isOwned IS NULL OR possessionStatus = :isOwned)" +
            " AND (:isRead IS NULL OR readStatus = :isRead)" +
            " AND isbn LIKE '%' || :searchQuery || '%'" +
            " ORDER BY :sortType ASC")
    fun getBooksByIsbn(isOwned: Boolean?, isRead: Boolean?, sortType: String, searchQuery: String): Flow<List<Book>>
}