package pl.flowerbedapp.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.flowerbedapp.core.data.local.dao.PlantDao
import pl.flowerbedapp.core.data.local.seed.PlantSeeder
import pl.flowerbedapp.core.data.mapper.toDomain
import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.Moisture
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.repository.PlantRepository
import pl.flowerbedapp.core.util.safeCall
import pl.flowerbedapp.core.util.searchKeyOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val dao: PlantDao,
    private val seeder: PlantSeeder,
) : PlantRepository {

    override suspend fun searchPlants(params: GardenSearchParams): Result<List<Plant>> {
        // Right after install the first query can arrive while seeding is still running — wait for it
        seeder.seedIfNeeded()
        return safeCall {
            dao.find(
                phMin     = params.ph?.start,
                phMax     = params.ph?.endInclusive,
                sunMin    = params.sunHours?.first,
                sunMax    = params.sunHours?.last,
                soil      = params.soilType?.name,
                // No bed moisture given → accept every class, i.e. no filtering
                moistures = (params.moisture?.suitablePlants() ?: Moisture.entries).map { it.name },
                zone      = params.hardinessZone,
                query     = searchKeyOf(params.query),
                limit     = PAGE_SIZE,
                offset    = (params.page - 1) * PAGE_SIZE,
            ).map { it.toDomain() }
        }
    }

    override suspend fun getPlantDetail(id: String): Result<Plant> {
        seeder.seedIfNeeded()
        return safeCall {
            requireNotNull(dao.getByName(id)) { "No plant named $id" }.toDomain()
        }
    }

    override fun browseAllPlants(query: String, page: Int): Flow<Result<List<Plant>>> = flow {
        emit(Result.Loading)
        emit(searchPlants(GardenSearchParams(query = query, page = page)))
    }

    // Must match the ViewModels' PAGE_SIZE: a page shorter than this is how they detect the end
    private companion object { const val PAGE_SIZE = 20 }
}
