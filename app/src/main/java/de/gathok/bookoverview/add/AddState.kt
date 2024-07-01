package de.gathok.bookoverview.add

data class AddState(
    val title: String = "",
    val author: String = "",
    val isbn: String = "",
    val possessionStatus: Boolean = true,
    val readStatus: Boolean = false,
    val rating: Int = 0,
    val cameraPermissionGranted: Boolean = false,
    val isScanning: Boolean = false,
    val isDoubleIsbn: Boolean = false,
)
