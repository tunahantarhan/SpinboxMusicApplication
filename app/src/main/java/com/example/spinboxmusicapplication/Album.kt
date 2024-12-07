data class Album(
    val title: String = "",
    val artist: String = "",
    val imageUrl: String = "",
    val cdPrice: Double = 0.0,
    val lpPrice: Double = 0.0,
    val year: Int? = null,
    val country: String? = null,
    val rating: Double? = null,
    val genre: String? = null,
    val stock: Int? = null
)
