package pl.flowerbedapp.core.data.mapper

import pl.flowerbedapp.core.data.local.entity.PlantEntity
import pl.flowerbedapp.core.domain.model.LifeForm
import pl.flowerbedapp.core.domain.model.Moisture
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.PlantSource
import pl.flowerbedapp.core.domain.model.SoilType

// valueOf() throws on an unknown name — safe because every row was validated before it was written
fun PlantEntity.toDomain() = Plant(
    scientificName = scientificName,
    commonName     = commonNamePl,
    commonNameEn   = commonNameEn,
    imageUrl       = imageUrl,
    lifeForm       = LifeForm.valueOf(lifeForm),
    ph             = phMin..phMax,
    soilTypes      = soilTypes.map(SoilType::valueOf).toSet(),
    sunHours       = sunHoursMin..sunHoursMax,
    hardinessZones = hardinessZoneMin..hardinessZoneMax,
    moisture       = Moisture.valueOf(moisture),
    heightCm       = heightMinCm..heightMaxCm,
    spreadCm       = spreadCm,
    bloomMonths    = bloomMonths,
    source         = PlantSource.valueOf(source),
)
