package pl.flowerbedapp.core.domain.usecase.plant

import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Result
import pl.flowerbedapp.core.domain.repository.PlantRepository
import javax.inject.Inject

class GetPlantDetailUseCase @Inject constructor(
    private val repo: PlantRepository,
) {
    suspend operator fun invoke(id: Int): Result<Plant> = repo.getPlantDetail(id)
}