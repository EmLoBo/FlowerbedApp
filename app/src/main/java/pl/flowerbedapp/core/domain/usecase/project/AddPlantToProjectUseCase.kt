package pl.flowerbedapp.core.domain.usecase.project

import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.repository.ProjectRepository
import javax.inject.Inject

class AddPlantToProjectUseCase @Inject constructor(
    private val repo: ProjectRepository,
) {
    suspend operator fun invoke(projectId: Long, plant: Plant) =
        repo.addPlantToProject(projectId, plant)
}
