package pl.flowerbedapp.core.data.mapper


import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TreflePlantDetailDto
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TreflePlantDto
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.PlantGrowth
import pl.flowerbedapp.core.domain.model.PlantSpecifications
import kotlin.collections.orEmpty

fun TreflePlantDto.toDomain() = Plant(
    id               = id,
    slug             = slug,
    commonName       = commonName,
    scientificName   = scientificName,
    imageUrl         = imageUrl,
    familyCommonName = familyCommonName,
    genus            = genus,
    specifications   = null,
    growth           = null,
)

fun TreflePlantDetailDto.toDomain() = Plant(
    id               = id,
    slug             = slug,
    commonName       = commonName,
    scientificName   = scientificName,
    imageUrl         = imageUrl,
    familyCommonName = familyCommonName,
    genus            = genus,
    specifications   = specifications?.let {
        PlantSpecifications(
            averageHeightCm = it.averageHeight?.cm,
            maximumHeightCm = it.maximumHeight?.cm,
            toxicity = it.toxicity,
            lifespan = it.lifespan,
        )
    },
    growth = growth?.let {
        PlantGrowth(
            phMinimum = it.phMinimum,
            phMaximum = it.phMaximum,
            light = it.light,
            atmosphericHumidity = it.atmosphericHumidity,
            soilTexture = it.soilTexture,
            growthMonths = it.growthMonths.orEmpty(),
            bloomMonths = it.bloomMonths.orEmpty(),
            rowSpacingCm = it.rowSpacing?.cm,
            spreadCm = it.spread?.cm,
        )
    },
)