package de.gathok.bookoverview.add.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BookModel {
    val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val bookService = retrofit.create(BookService::class.java)
}