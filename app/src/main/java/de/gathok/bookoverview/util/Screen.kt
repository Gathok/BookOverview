package de.gathok.bookoverview.util

sealed class Screen(val route: String) {
    data object BookList : Screen("bookList")
    data object AddBook : Screen("addBook")
}