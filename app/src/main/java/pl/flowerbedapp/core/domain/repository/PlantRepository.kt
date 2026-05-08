package pl.flowerbedapp.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result

interface PlantRepository {

    suspend fun searchPlants(params: GardenSearchParams): Result<List<Plant>>
    suspend fun getPlantDetail(id: Int): Result<Plant>
    suspend fun getSuggestions(query: String): Result<List<Plant>>

    fun browseAllPlants(query: String, page: Int): Flow<Result<List<Plant>>>
}