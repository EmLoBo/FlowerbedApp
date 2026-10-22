package pl.flowerbedapp

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.LifeForm
import pl.flowerbedapp.core.domain.model.Moisture
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.PlantSource
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.SoilType
import pl.flowerbedapp.core.domain.model.SunExposure
import pl.flowerbedapp.core.domain.repository.PlantRepository
import pl.flowerbedapp.core.domain.usecase.plant.SearchPlantsUseCase

// ─── Domain use case tests

class SearchPlantsUseCaseTest {

    private val repo = mockk<PlantRepository>()
    private val useCase = SearchPlantsUseCase(repo)

    private val validParams = GardenSearchParams(
        ph       = 5.5..7.0,
        sunHours = SunExposure.PARTIAL_SUN.hours,
        soilType = SoilType.LOAMY,
    )

    @Test
    fun `returns error when phMin is greater than phMax`() = runTest {
        val params = validParams.copy(ph = 8.0..5.0)
        val result = useCase(params)
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IllegalArgumentException)
    }

    @Test
    fun `delegates to repository when params are valid`() = runTest {
        val plants = listOf(fakePlant("Planta prima"), fakePlant("Planta secunda"))
        coEvery { repo.searchPlants(any()) } returns Result.Success(plants)

        val result = useCase(validParams)

        assertTrue(result is Result.Success)
        assertEquals(plants, (result as Result.Success).data)
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery { repo.searchPlants(any()) } returns Result.Error(RuntimeException("Database error"))

        val result = useCase(validParams)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `allows equal phMin and phMax`() = runTest {
        val params = validParams.copy(ph = 7.0..7.0)
        coEvery { repo.searchPlants(any()) } returns Result.Success(emptyList())

        val result = useCase(params)
        assertTrue(result is Result.Success)
    }

    @Test
    fun `allows a search without a pH filter`() = runTest {
        val params = validParams.copy(ph = null)
        coEvery { repo.searchPlants(any()) } returns Result.Success(emptyList())

        val result = useCase(params)
        assertTrue(result is Result.Success)
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun fakePlant(scientificName: String) = Plant(
    scientificName = scientificName,
    commonName     = "Roślina $scientificName",
    commonNameEn   = null,
    imageUrl       = null,
    lifeForm       = LifeForm.PERENNIAL,
    ph             = 6.0..7.0,
    soilTypes      = setOf(SoilType.LOAMY),
    sunHours       = 4..8,
    hardinessZones = 4..8,
    moisture       = Moisture.MEDIUM,
    heightCm       = 30..60,
    spreadCm       = 40,
    bloomMonths    = listOf(6, 7),
    source         = PlantSource.SEED,
)
