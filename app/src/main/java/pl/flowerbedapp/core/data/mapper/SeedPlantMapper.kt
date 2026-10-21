package pl.flowerbedapp.core.data.mapper

import pl.flowerbedapp.core.data.local.entity.PlantEntity
import pl.flowerbedapp.core.data.local.seed.SeedPlantDto
import pl.flowerbedapp.core.util.searchKeyOf

private val SOIL_TYPES = setOf("SANDY", "LOAMY", "CLAY", "PEATY", "CHALKY")
private val MOISTURE   = setOf("DRY", "MEDIUM", "WET")
private val LIFE_FORMS = setOf("PERENNIAL", "ANNUAL", "BIENNIAL", "SHRUB", "TREE", "CLIMBER", "GRASS", "BULB")

/** Null when the record breaks the schema — the seeder skips it rather than crashing at startup. */
fun SeedPlantDto.toEntityOrNull(): PlantEntity? {
    val valid = soilTypes.all { it in SOIL_TYPES } &&
        moisture in MOISTURE &&
        lifeForm in LIFE_FORMS &&
        phMin <= phMax &&
        sunHoursMin <= sunHoursMax &&
        hardinessZoneMin <= hardinessZoneMax &&
        bloomMonths.all { it in 1..12 }
    if (!valid) return null

    return PlantEntity(
        scientificName   = scientificName,
        commonNamePl     = commonNamePl,
        commonNameEn     = commonNameEn,
        searchKey        = searchKeyOf(commonNamePl, scientificName, commonNameEn),
        phMin            = phMin,
        phMax            = phMax,
        soilTypes        = soilTypes,
        sunHoursMin      = sunHoursMin,
        sunHoursMax      = sunHoursMax,
        hardinessZoneMin = hardinessZoneMin,
        hardinessZoneMax = hardinessZoneMax,
        moisture         = moisture,
        heightMinCm      = heightMinCm,
        heightMaxCm      = heightMaxCm,
        spreadCm         = spreadCm,
        bloomMonths      = bloomMonths,
        lifeForm         = lifeForm,
        trefleId         = trefleId,
        imageUrl         = imageUrl,
        source           = "SEED",
    )
}
