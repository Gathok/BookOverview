package de.gathok.bookoverview.util

import kotlinx.serialization.Serializable

enum class Screen {
    Overview,
    Add,
    Details,
    Scanner,
    Settings,
    Trash,
    SeriesOverview,
}

@Serializable
data class NavOverviewScreen (
    val authorToSearch: String? = null,
    val seriesToSearch: Int? = null,
)

@Serializable
data class NavAddScreen (
    val isbn: String? = null
)

@Serializable
data class NavDetailsScreen (
    val bookId: Int
)

@Serializable
object NavScannerScreen

@Serializable
object NavSettingsScreen

@Serializable
object NavTrashScreen

@Serializable
object NavSeriesOverviewScreen