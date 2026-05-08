package pl.flowerbedapp.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val id: Int,
    val slug: String,
    val commonName: String?,
    val scientificName: String,
    val imageUrl: String?,
    val familyCommonName: String?,
    val genus: String?,
    val specifications: PlantSpecifications?,
    val growth: PlantGrowth?,
) : Parcelable {
    val displayName: String get() = commonName ?: scientificName
}

@Parcelize
data class PlantSpecifications(
    val averageHeightCm: Double?,
    val maximumHeightCm: Double?,
    val toxicity: String?,
    val lifespan: String?,
) : Parcelable

@Parcelize
data class PlantGrowth(
    val phMinimum: Double?,
    val phMaximum: Double?,
    val light: Int?,              // 0–10 scale from Trefle
    val atmosphericHumidity: Int?,
    val soilTexture: Int?,        // 0–10 Trefle scale
    val growthMonths: List<String>,
    val bloomMonths: List<String>,
    val rowSpacingCm: Double?,
    val spreadCm: Double?,
) : Parcelable

data class GardenSearchParams(
    val latitude: Double? = null,
    val longitude: Double? = null,
    val soilPhMin: Double = 5.5,
    val soilPhMax: Double = 7.0,
    val sunExposure: SunExposure = SunExposure.PARTIAL_SUN,
    val soilType: SoilType = SoilType.LOAMY,
    val query: String = "",
    val page: Int = 1,
)

enum class SunExposure(val lightValue: Int, val label: String, val emoji: String) {
    FULL_SHADE(1,  "Full shade",    "🌑"),
    PARTIAL_SHADE(3, "Partial shade", "🌥"),
    PARTIAL_SUN(6,  "Partial sun",  "⛅"),
    FULL_SUN(9,    "Full sun",     "☀️"),
}

enum class SoilType(val textureValue: Int, val label: String, val emoji: String) {
    SANDY(2,  "Sandy", "🏖"),
    LOAMY(5,  "Loamy", "🌱"),
    CLAY(8,   "Clay",  "🧱"),
    PEATY(3,  "Peaty", "🟤"),
    CHALKY(7, "Chalky","⬜"),
}
