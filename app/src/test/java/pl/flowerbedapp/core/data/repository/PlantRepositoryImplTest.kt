package pl.flowerbedapp.core.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import pl.flowerbedapp.core.data.local.dao.PlantDao
import pl.flowerbedapp.core.data.local.seed.PlantSeeder
import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.model.SoilType

class PlantRepositoryImplTest {

    private val dao = mockk<PlantDao>()
    private val seeder = mockk<PlantSeeder>()
    private val repo = PlantRepositoryImpl(dao, seeder)

    @Before
    fun setUp() {
        coEvery { seeder.seedIfNeeded() } returns Result.Success(0)
        coEvery { dao.find(any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns emptyList()
    }

    @Test
    fun `garden params are translated into dao arguments`() = runTest {
        repo.searchPlants(
            GardenSearchParams(
                ph            = 5.5..6.5,
                sunHours      = 2..4,
                soilType      = SoilType.CLAY,
                hardinessZone = 6,
                query         = "Łubin",
                page          = 3,
            )
        )

        coVerify {
            dao.find(
                phMin  = 5.5,
                phMax  = 6.5,
                sunMin = 2,
                sunMax = 4,
                soil   = "CLAY",
                zone   = 6,
                query  = "lubin",
                limit  = 20,
                offset = 40,
            )
        }
    }

    @Test
    fun `missing criteria are passed as null so the dao ignores them`() = runTest {
        repo.searchPlants(GardenSearchParams())

        coVerify {
            dao.find(
                phMin  = null,
                phMax  = null,
                sunMin = null,
                sunMax = null,
                soil   = null,
                zone   = null,
                query  = "",
                limit  = 20,
                offset = 0,
            )
        }
    }

    @Test
    fun `seeding finishes before the first query`() = runTest {
        repo.searchPlants(GardenSearchParams())

        coVerifyOrder {
            seeder.seedIfNeeded()
            dao.find(any(), any(), any(), any(), any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `unknown plant name returns an error instead of crashing`() = runTest {
        coEvery { dao.getByName("Planta inexistens") } returns null

        val result = repo.getPlantDetail("Planta inexistens")

        assertTrue(result is Result.Error)
    }
}
