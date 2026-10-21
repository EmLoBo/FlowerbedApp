package pl.flowerbedapp.core.data.local.seed

import com.squareup.moshi.JsonClass

/** Shape of assets/plants_seed.json. JSON keys equal the property names, so no @Json needed. */
@JsonClass(generateAdapter = true)
data class SeedFileDto(
    val version: Int,
    val plants: List<SeedPlantDto>,
)

@JsonClass(generateAdapter = true)
data class SeedPlantDto(
    val scientificName: String,
    val commonNamePl: String,
    val commonNameEn: String?,
    val phMin: Double,
    val phMax: Double,
    val soilTypes: List<String>,
    val sunHoursMin: Int,
    val sunHoursMax: Int,
    val hardinessZoneMin: Int,
    val hardinessZoneMax: Int,
    val moisture: String,
    val heightMinCm: Int,
    val heightMaxCm: Int,
    val spreadCm: Int,
    val bloomMonths: List<Int>,
    val lifeForm: String,
    val trefleId: Int?,
    val imageUrl: String?,
)
