package de.gathok.bookoverview

sealed class Screen(val route: String) {
    data object BookList : Screen("bookList")
    data object AddBook : Screen("addBook")
}