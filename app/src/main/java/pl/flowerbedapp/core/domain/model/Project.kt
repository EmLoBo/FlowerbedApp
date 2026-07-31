package pl.flowerbedapp.core.domain.model

data class Project(
    val id: Long = 0,
    val name: String,
    val description: String,
    val createdAt: Long = System.currentTimeMillis(),
    val plants: List<Plant> = emptyList(),
    val searchParams: GardenSearchParams? = null,
    val isFavorites: Boolean = false,
)