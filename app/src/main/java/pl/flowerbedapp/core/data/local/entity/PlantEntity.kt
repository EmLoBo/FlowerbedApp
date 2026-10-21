package pl.flowerbedapp.core.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import pl.flowerbedapp.core.data.local.PlantConverters

@Entity(tableName = "plants")
@TypeConverters(PlantConverters::class)
data class PlantEntity(
    // Natural key: stable across seed versions, so re-seeding updates rows instead of duplicating them
    @PrimaryKey
    @ColumnInfo(name = "scientific_name")     val scientificName: String,
    @ColumnInfo(name = "common_name_pl")      val commonNamePl: String,
    @ColumnInfo(name = "common_name_en")      val commonNameEn: String?,
    // Lowercase, diacritics stripped: case/accent-blind search and a Polish-friendly sort order
    @ColumnInfo(name = "search_key")          val searchKey: String,
    @ColumnInfo(name = "ph_min")              val phMin: Double,
    @ColumnInfo(name = "ph_max")              val phMax: Double,
    @ColumnInfo(name = "soil_types")          val soilTypes: List<String>,
    @ColumnInfo(name = "sun_hours_min")       val sunHoursMin: Int,
    @ColumnInfo(name = "sun_hours_max")       val sunHoursMax: Int,
    @ColumnInfo(name = "hardiness_zone_min")  val hardinessZoneMin: Int,
    @ColumnInfo(name = "hardiness_zone_max")  val hardinessZoneMax: Int,
    @ColumnInfo(name = "moisture")            val moisture: String,
    @ColumnInfo(name = "height_min_cm")       val heightMinCm: Int,
    @ColumnInfo(name = "height_max_cm")       val heightMaxCm: Int,
    @ColumnInfo(name = "spread_cm")           val spreadCm: Int,
    @ColumnInfo(name = "bloom_months")        val bloomMonths: List<Int>,
    @ColumnInfo(name = "life_form")           val lifeForm: String,
    @ColumnInfo(name = "trefle_id")           val trefleId: Int?,
    @ColumnInfo(name = "image_url")           val imageUrl: String?,
    // SEED = shipped in assets, AI = added by the LLM fallback, USER = entered by hand
    @ColumnInfo(name = "source")              val source: String,
)
