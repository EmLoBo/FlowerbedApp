package pl.flowerbedapp.core.domain.usecase.plant

import kotlinx.coroutines.flow.Flow
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.repository.PlantRepository
import javax.inject.Inject

class BrowsePlantsUseCase @Inject constructor(
    private val repo: PlantRepository,
) {
    operator fun invoke(query: String, page: Int): Flow<Result<List<Plant>>> =
        repo.browseAllPlants(query, page)
}