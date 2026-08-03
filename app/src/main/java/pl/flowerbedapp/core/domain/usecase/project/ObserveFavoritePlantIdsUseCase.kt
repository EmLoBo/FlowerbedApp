package pl.flowerbedapp.core.domain.usecase.project

import kotlinx.coroutines.flow.Flow
import pl.flowerbedapp.core.domain.repository.ProjectRepository
import javax.inject.Inject

class ObserveFavoritePlantIdsUseCase @Inject constructor(
    private val repo: ProjectRepository,
) {
    operator fun invoke(): Flow<Set<Int>> = repo.observeFavoritePlantIds()
}
