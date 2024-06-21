package de.gathok.bookoverview

data class Book(val title: String, val author: String, val isbn: String,
                var possessionStatus: Boolean = false, var readStatus: Boolean = false,
                var price: Double? = null, var rating: Int? = null) {

    fun getRating(): String? {
        return when (rating) {
            0 -> "☆☆☆☆☆"
            1 -> "★☆☆☆☆"
            2 -> "★★☆☆☆"
            3 -> "★★★☆☆"
            4 -> "★★★★☆"
            5 -> "★★★★★"
            else -> null
        }
    }

    fun getPrice(): String {
        return price?.let { "$it €" } ?: "No price"
    }
}