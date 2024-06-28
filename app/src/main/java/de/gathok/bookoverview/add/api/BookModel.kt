package de.gathok.bookoverview.add.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BookModel {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.googleapis.com/books/v1/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val bookService: BookService = retrofit.create(BookService::class.java)
}