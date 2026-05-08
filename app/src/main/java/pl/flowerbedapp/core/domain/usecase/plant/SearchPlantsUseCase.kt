package pl.flowerbedapp.core.domain.usecase.plant

import pl.flowerbedapp.core.domain.model.GardenSearchParams
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.repository.PlantRepository
import javax.inject.Inject

class SearchPlantsUseCase @Inject constructor(
    private val repo: PlantRepository,
) {
    suspend operator fun invoke(params: GardenSearchParams): Result<List<Plant>> {
        if (params.soilPhMin > params.soilPhMax) {
            return Result.Error(IllegalArgumentException("pH min must be ≤ pH max"))
        }
        return repo.searchPlants(params)
    }
}