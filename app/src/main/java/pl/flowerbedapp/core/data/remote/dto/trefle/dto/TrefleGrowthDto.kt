package pl.flowerbedapp.core.data.remote.dto.trefle.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class TrefleGrowthDto(
    @Json(name = "ph_minimum")            val phMinimum: Double?,
    @Json(name = "ph_maximum")            val phMaximum: Double?,
    @Json(name = "light")                 val light: Int?,
    @Json(name = "atmospheric_humidity")  val atmosphericHumidity: Int?,
    @Json(name = "soil_texture")          val soilTexture: Int?,
    @Json(name = "growth_months")         val growthMonths: List<String>?,
    @Json(name = "bloom_months")          val bloomMonths: List<String>?,
    @Json(name = "row_spacing")           val rowSpacing: TrefleSpacingDto?,
    @Json(name = "spread")                val spread: TrefleSpacingDto?,
)
