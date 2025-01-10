package de.gathok.bookoverview.settings

import de.gathok.bookoverview.data.Book

sealed class SettingsEvent {

    data object SettingsOpened: SettingsEvent()

    data object OnTrashClicked : SettingsEvent()
    data class OnTrashRestoreClicked(val book: Book) : SettingsEvent()
    data class OnTrashDeleteClicked(val book: Book) : SettingsEvent()
    data object OnTrashDeleteAllClicked : SettingsEvent()
    data object OnTrashRestoreAllClicked : SettingsEvent()
    data object OnExportClicked : SettingsEvent()
    data class SetLoading(val isLoading: Boolean) : SettingsEvent()
    data object ResetExportData : SettingsEvent()
}