package pl.flowerbedapp.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pl.flowerbedapp.core.data.mapper.toDomain
import pl.flowerbedapp.core.data.remote.api.TrefleApi
import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.repository.PlantRepository
import pl.flowerbedapp.core.util.safeCall
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantRepositoryImpl @Inject constructor(
    private val api: TrefleApi,
) : PlantRepository {

    override suspend fun searchPlants(params: GardenSearchParams): Result<List<Plant>> =
        safeCall {
            api.filterPlants(
                // Trefle's light/soil_texture data is too sparse to filter on (a range filter
                // excludes every plant with a null value → empty results). Filter by pH only.
                phMinRange = ",${params.soilPhMax}",
                phMaxRange = "${params.soilPhMin},",
                query      = params.query.ifBlank { null },
                page       = params.page,
            ).data.map { it.toDomain() }
        }



    override suspend fun getPlantDetail(id: Int): pl.flowerbedapp.core.domain.model.Result<Plant> =
        safeCall { api.getPlantById(id).data.toDomain() }

    override suspend fun getSuggestions(query: String): pl.flowerbedapp.core.domain.model.Result<List<Plant>> =
        safeCall { api.searchPlants(query, page = 1).data.take(5).map { it.toDomain() } }

    override fun browseAllPlants(query: String, page: Int): Flow<pl.flowerbedapp.core.domain.model.Result<List<Plant>>> = flow {
        emit(Result.Loading)
        emit(safeCall {
            if (query.isBlank()) api.getPlants(page).data.map { it.toDomain() }
            else api.searchPlants(query, page).data.map { it.toDomain() }
        })
    }
}