package de.gathok.bookoverview.util

sealed class Screen(val route: String) {
    data object Overview : Screen("overview")
    data object Add : Screen("add")
    data object Details : Screen("details")
    data object Scanner : Screen("add/scanner")
    data object Settings : Screen("settings")
    data object Trash : Screen("settings/trash")
}