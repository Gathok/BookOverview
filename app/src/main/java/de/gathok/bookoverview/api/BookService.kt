package de.gathok.bookoverview.api

import retrofit2.http.GET
import retrofit2.http.Query

interface BookService {
    @GET("volumes")
    suspend fun getBook(
        @Query("q") isbn:String,
    ): BookResponse
}