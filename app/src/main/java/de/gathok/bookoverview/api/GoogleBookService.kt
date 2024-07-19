package de.gathok.bookoverview.api

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBookService {
    @GET("volumes")
    suspend fun getBookByIsbn(
        @Query("q") isbn:String,
    ): GoogleBookResponse
}