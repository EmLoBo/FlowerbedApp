package pl.flowerbedapp.core.domain.usecase.project

import pl.flowerbedapp.core.domain.model.Project
import pl.flowerbedapp.core.domain.repository.ProjectRepository
import javax.inject.Inject

class SaveProjectUseCase @Inject constructor(
    private val repo: ProjectRepository,
) {
    suspend operator fun invoke(project: Project): Long = repo.saveProject(project)
}