package de.gathok.bookoverview.settings

import de.gathok.bookoverview.data.Book
import de.gathok.bookoverview.util.Screen

data class SettingsState(
    val curScreen: Screen = Screen.Settings,

    val trashIsEmpty: Boolean = true,
    val trashedBooks: List<Book> = emptyList()
)
