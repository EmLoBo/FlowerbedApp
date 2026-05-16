package pl.flowerbedapp.core.data.mapper

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TrefleGenusDto
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TrefleGrowthDto
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TrefleHeightDto
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TreflePlantDetailDto
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TreflePlantDto
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TrefleSpacingDto
import pl.flowerbedapp.core.data.remote.dto.trefle.dto.TrefleSpecDto

class PlantMapperTest {

    @Test
    fun `TreflePlantDto maps all fields and leaves specifications and growth null`() {
        val dto = TreflePlantDto(
            id = 42,
            slug = "rosa-canina",
            commonName = "Dog rose",
            scientificName = "Rosa canina",
            imageUrl = "https://example.com/rosa.jpg",
            familyCommonName = "Rose family",
            genus = "Rosa",
        )

        val plant = dto.toDomain()

        assertEquals(42, plant.id)
        assertEquals("rosa-canina", plant.slug)
        assertEquals("Dog rose", plant.commonName)
        assertEquals("Rosa canina", plant.scientificName)
        assertEquals("https://example.com/rosa.jpg", plant.imageUrl)
        assertEquals("Rose family", plant.familyCommonName)
        assertEquals("Rosa", plant.genus)
        assertNull(plant.specifications)
        assertNull(plant.growth)
    }

    @Test
    fun `TreflePlantDto preserves nullable fields when null`() {
        val dto = TreflePlantDto(
            id = 1,
            slug = "x",
            commonName = null,
            scientificName = "Genus species",
            imageUrl = null,
            familyCommonName = null,
            genus = null,
        )

        val plant = dto.toDomain()

        assertNull(plant.commonName)
        assertNull(plant.imageUrl)
        assertNull(plant.familyCommonName)
        assertNull(plant.genus)
        assertEquals("Genus species", plant.scientificName)
    }

    @Test
    fun `TreflePlantDetailDto maps full specifications and growth`() {
        val dto = TreflePlantDetailDto(
            id = 7,
            slug = "lavandula-angustifolia",
            commonName = "Lavender",
            scientificName = "Lavandula angustifolia",
            imageUrl = "https://example.com/lavender.jpg",
            familyCommonName = "Mint family",
            genus = TrefleGenusDto(name = "Lavandula"),
            specifications = TrefleSpecDto(
                averageHeight = TrefleHeightDto(cm = 60.0),
                maximumHeight = TrefleHeightDto(cm = 90.0),
                toxicity = "none",
                lifespan = "perennial",
            ),
            growth = TrefleGrowthDto(
                phMinimum = 6.5,
                phMaximum = 7.5,
                light = 9,
                atmosphericHumidity = 4,
                soilTexture = 5,
                growthMonths = listOf("apr", "may", "jun"),
                bloomMonths = listOf("jul", "aug"),
                rowSpacing = TrefleSpacingDto(cm = 30.0),
                spread = TrefleSpacingDto(cm = 45.0),
            ),
        )

        val plant = dto.toDomain()

        assertEquals("Lavandula", plant.genus)

        assertEquals(60.0, plant.specifications?.averageHeightCm)
        assertEquals(90.0, plant.specifications?.maximumHeightCm)
        assertEquals("none", plant.specifications?.toxicity)
        assertEquals("perennial", plant.specifications?.lifespan)

        assertEquals(6.5, plant.growth?.phMinimum)
        assertEquals(7.5, plant.growth?.phMaximum)
        assertEquals(9, plant.growth?.light)
        assertEquals(4, plant.growth?.atmosphericHumidity)
        assertEquals(5, plant.growth?.soilTexture)
        assertEquals(listOf("apr", "may", "jun"), plant.growth?.growthMonths)
        assertEquals(listOf("jul", "aug"), plant.growth?.bloomMonths)
        assertEquals(30.0, plant.growth?.rowSpacingCm)
        assertEquals(45.0, plant.growth?.spreadCm)
    }

    @Test
    fun `TreflePlantDetailDto extracts genus name from nested object and tolerates nulls`() {
        val withGenus = baseDetailDto(specifications = null, growth = null)
            .copy(genus = TrefleGenusDto(name = "Rosa"))
        assertEquals("Rosa", withGenus.toDomain().genus)

        val genusNameNull = baseDetailDto(specifications = null, growth = null)
            .copy(genus = TrefleGenusDto(name = null))
        assertNull(genusNameNull.toDomain().genus)

        val genusObjectNull = baseDetailDto(specifications = null, growth = null)
            .copy(genus = null)
        assertNull(genusObjectNull.toDomain().genus)
    }

    @Test
    fun `TreflePlantDetailDto with null specifications and growth maps to null domain blocks`() {
        val dto = baseDetailDto(specifications = null, growth = null)

        val plant = dto.toDomain()

        assertNull(plant.specifications)
        assertNull(plant.growth)
    }

    @Test
    fun `TreflePlantDetailDto with null height and spacing wrappers maps to null cm values`() {
        val dto = baseDetailDto(
            specifications = TrefleSpecDto(
                averageHeight = null,
                maximumHeight = null,
                toxicity = null,
                lifespan = null,
            ),
            growth = baseGrowthDto(
                rowSpacing = null,
                spread = null,
            ),
        )

        val plant = dto.toDomain()

        assertNull(plant.specifications?.averageHeightCm)
        assertNull(plant.specifications?.maximumHeightCm)
        assertNull(plant.growth?.rowSpacingCm)
        assertNull(plant.growth?.spreadCm)
    }

    @Test
    fun `TreflePlantDetailDto with null month lists maps to empty lists`() {
        val dto = baseDetailDto(
            specifications = null,
            growth = baseGrowthDto(growthMonths = null, bloomMonths = null),
        )

        val plant = dto.toDomain()

        assertEquals(emptyList<String>(), plant.growth?.growthMonths)
        assertEquals(emptyList<String>(), plant.growth?.bloomMonths)
    }

    private fun baseDetailDto(
        specifications: TrefleSpecDto?,
        growth: TrefleGrowthDto?,
    ) = TreflePlantDetailDto(
        id = 1,
        slug = "x",
        commonName = null,
        scientificName = "X",
        imageUrl = null,
        familyCommonName = null,
        genus = null,
        specifications = specifications,
        growth = growth,
    )

    private fun baseGrowthDto(
        phMinimum: Double? = null,
        phMaximum: Double? = null,
        light: Int? = null,
        atmosphericHumidity: Int? = null,
        soilTexture: Int? = null,
        growthMonths: List<String>? = emptyList(),
        bloomMonths: List<String>? = emptyList(),
        rowSpacing: TrefleSpacingDto? = TrefleSpacingDto(cm = 0.0),
        spread: TrefleSpacingDto? = TrefleSpacingDto(cm = 0.0),
    ) = TrefleGrowthDto(
        phMinimum = phMinimum,
        phMaximum = phMaximum,
        light = light,
        atmosphericHumidity = atmosphericHumidity,
        soilTexture = soilTexture,
        growthMonths = growthMonths,
        bloomMonths = bloomMonths,
        rowSpacing = rowSpacing,
        spread = spread,
    )
}
