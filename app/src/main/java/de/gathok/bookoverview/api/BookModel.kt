package de.gathok.bookoverview.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BookModel {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val openLibraryRetrofit = Retrofit.Builder()
        .baseUrl("https://openlibrary.org/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val googleRetrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val openLibraryService = openLibraryRetrofit.create(OpenLibraryService::class.java)

    val googleBookService = googleRetrofit.create(GoogleBookService::class.java)
}