package pl.flowerbedapp.core.domain.model

data class Plant(
    val scientificName: String,
    val commonName: String,
    val commonNameEn: String?,
    val imageUrl: String?,
    val lifeForm: LifeForm,
    val ph: ClosedFloatingPointRange<Double>,
    val soilTypes: Set<SoilType>,
    val sunHours: IntRange,
    val hardinessZones: IntRange,
    val moisture: Moisture,
    val heightCm: IntRange,
    val spreadCm: Int,
    val bloomMonths: List<Int>,
    val source: PlantSource,
) {
    /** The scientific name is the plant's identity — stable across seed versions, unlike Trefle ids. */
    val id: String get() = scientificName
    val displayName: String get() = commonName
}

/** Every criterion is optional — null means "don't filter on this". */
data class GardenSearchParams(
    val ph: ClosedFloatingPointRange<Double>? = null,
    val sunHours: IntRange? = null,
    val soilType: SoilType? = null,
    val moisture: Moisture? = null,
    val hardinessZone: Int? = null,
    val query: String = "",
    val page: Int = 1,
)

/** UI presets over sun hours; neighbours share an edge, so a borderline plant shows up in both. */
enum class SunExposure(val hours: IntRange, val label: String, val emoji: String) {
    FULL_SHADE(0..2,    "Full shade",    "🌑"),
    PARTIAL_SHADE(2..4, "Partial shade", "🌥"),
    PARTIAL_SUN(4..6,   "Partial sun",   "⛅"),
    FULL_SUN(6..12,     "Full sun",      "☀️");

    companion object {
        /** Category for a bed's sun hours; on a shared edge the sunnier one wins (6 h counts as full sun). */
        fun forHours(hours: Int): SunExposure = entries.lastOrNull { hours in it.hours } ?: FULL_SUN
    }
}

enum class SoilType(val label: String, val emoji: String) {
    SANDY("Sandy",   "🏖"),
    LOAMY("Loamy",   "🌱"),
    CLAY("Clay",     "🧱"),
    PEATY("Peaty",   "🟤"),
    CHALKY("Chalky", "⬜"),
}

enum class Moisture(val label: String, val emoji: String) {
    DRY("Dry",       "🌵"),
    MEDIUM("Medium", "💧"),
    WET("Wet",       "🌊");

    /**
     * Plant moisture classes that do well in a bed of this moisture. Tolerance runs one way:
     * a drought-tolerant plant copes with normal soil, a moisture lover doesn't cope with dry soil.
     */
    fun suitablePlants(): Set<Moisture> = when (this) {
        DRY    -> setOf(DRY)
        MEDIUM -> setOf(DRY, MEDIUM)
        WET    -> setOf(MEDIUM, WET)
    }
}

enum class LifeForm(val label: String, val emoji: String) {
    PERENNIAL("Perennial", "🌿"),
    ANNUAL("Annual",       "🌼"),
    BIENNIAL("Biennial",   "🌸"),
    SHRUB("Shrub",         "🪴"),
    TREE("Tree",           "🌳"),
    CLIMBER("Climber",     "🍃"),
    GRASS("Grass",         "🌾"),
    BULB("Bulb",           "🌷"),
}

/** Where a plant's data came from — drives the "estimated data" badge for AI entries. */
enum class PlantSource { SEED, AI, USER }
