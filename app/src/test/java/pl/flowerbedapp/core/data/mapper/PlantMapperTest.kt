package pl.flowerbedapp.core.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test
import pl.flowerbedapp.core.data.local.entity.PlantEntity
import pl.flowerbedapp.core.domain.model.LifeForm
import pl.flowerbedapp.core.domain.model.Moisture
import pl.flowerbedapp.core.domain.model.PlantSource
import pl.flowerbedapp.core.domain.model.SoilType

class PlantMapperTest {

    @Test
    fun `PlantEntity maps names, image and identity`() {
        val plant = lavender().toDomain()

        assertEquals("Lavandula angustifolia", plant.id)
        assertEquals("Lavandula angustifolia", plant.scientificName)
        assertEquals("Lawenda wąskolistna", plant.commonName)
        assertEquals("English lavender", plant.commonNameEn)
        assertEquals("https://example.com/lavender.jpg", plant.imageUrl)
    }

    @Test
    fun `PlantEntity min and max columns become ranges`() {
        val plant = lavender().toDomain()

        assertEquals(6.5..8.0, plant.ph)
        assertEquals(6..12, plant.sunHours)
        assertEquals(5..9, plant.hardinessZones)
        assertEquals(40..60, plant.heightCm)
        assertEquals(60, plant.spreadCm)
    }

    @Test
    fun `PlantEntity text columns become enums`() {
        val plant = lavender().toDomain()

        assertEquals(setOf(SoilType.SANDY, SoilType.LOAMY, SoilType.CHALKY), plant.soilTypes)
        assertEquals(Moisture.DRY, plant.moisture)
        assertEquals(LifeForm.SHRUB, plant.lifeForm)
        assertEquals(PlantSource.SEED, plant.source)
    }

    @Test
    fun `PlantEntity without photo or bloom months maps to null and empty list`() {
        val fern = lavender().copy(imageUrl = null, trefleId = null, bloomMonths = emptyList())

        val plant = fern.toDomain()

        assertNull(plant.imageUrl)
        assertEquals(emptyList<Int>(), plant.bloomMonths)
    }

    @Test
    fun `PlantEntity with an unknown enum name fails loudly`() {
        // Rows must be validated before they are written — see SeedPlantMapper
        assertThrows(IllegalArgumentException::class.java) {
            lavender().copy(soilTypes = listOf("CLAYY")).toDomain()
        }
    }

    private fun lavender() = PlantEntity(
        scientificName   = "Lavandula angustifolia",
        commonNamePl     = "Lawenda wąskolistna",
        commonNameEn     = "English lavender",
        searchKey        = "lawenda waskolistna lavandula angustifolia english lavender",
        phMin            = 6.5,
        phMax            = 8.0,
        soilTypes        = listOf("SANDY", "LOAMY", "CHALKY"),
        sunHoursMin      = 6,
        sunHoursMax      = 12,
        hardinessZoneMin = 5,
        hardinessZoneMax = 9,
        moisture         = "DRY",
        heightMinCm      = 40,
        heightMaxCm      = 60,
        spreadCm         = 60,
        bloomMonths      = listOf(6, 7, 8),
        lifeForm         = "SHRUB",
        trefleId         = 55246,
        imageUrl         = "https://example.com/lavender.jpg",
        source           = "SEED",
    )
}
