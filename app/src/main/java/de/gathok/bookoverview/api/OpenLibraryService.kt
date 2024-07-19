package de.gathok.bookoverview.api

import retrofit2.http.GET
import retrofit2.http.Path

interface OpenLibraryService {
    @GET("isbn/{isbn}.json")
    suspend fun getBookByIsbn(
        @Path("isbn") isbn: String
    ): OpenLibraryResponse
}