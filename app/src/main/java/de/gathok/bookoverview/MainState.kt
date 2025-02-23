package de.gathok.bookoverview

import de.gathok.bookoverview.util.Screen

data class MainState(
    val selectedScreen: Screen = Screen.Overview,
)
