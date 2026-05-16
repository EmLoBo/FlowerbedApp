package pl.flowerbedapp

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.Plant
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
        soilPhMin = 5.5,
        soilPhMax = 7.0,
        sunExposure = SunExposure.PARTIAL_SUN,
        soilType = SoilType.LOAMY,
    )

    @Test
    fun `returns error when phMin is greater than phMax`() = runTest {
        val params = validParams.copy(soilPhMin = 8.0, soilPhMax = 5.0)
        val result = useCase(params)
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is IllegalArgumentException)
    }

    @Test
    fun `delegates to repository when params are valid`() = runTest {
        val plants = listOf(fakePlant(1), fakePlant(2))
        coEvery { repo.searchPlants(any()) } returns Result.Success(plants)

        val result = useCase(validParams)

        assertTrue(result is Result.Success)
        assertEquals(plants, (result as Result.Success).data)
    }

    @Test
    fun `propagates repository error`() = runTest {
        coEvery { repo.searchPlants(any()) } returns Result.Error(RuntimeException("Network error"))

        val result = useCase(validParams)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `allows equal phMin and phMax`() = runTest {
        val params = validParams.copy(soilPhMin = 7.0, soilPhMax = 7.0)
        coEvery { repo.searchPlants(any()) } returns Result.Success(emptyList())

        val result = useCase(params)
        assertTrue(result is Result.Success)
    }
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

private fun fakePlant(id: Int) = Plant(
    id               = id,
    slug             = "plant-$id",
    commonName       = "Plant $id",
    scientificName   = "Planta scientifica $id",
    imageUrl         = null,
    familyCommonName = null,
    genus            = null,
    specifications   = null,
    growth           = null,
)
