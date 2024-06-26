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

    @Query("SELECT * FROM book WHERE (:isOwned IS NULL OR possessionStatus = :isOwned)" +
            " AND (:isRead IS NULL OR readStatus = :isRead) ORDER BY :sortType ASC")
    fun getBooks(isOwned: Boolean?, isRead: Boolean?, sortType: String): Flow<List<Book>>
}