package pl.flowerbedapp.core.domain.usecase.project

import pl.flowerbedapp.core.domain.repository.ProjectRepository
import javax.inject.Inject

class GetOrCreateFavoritesUseCase @Inject constructor(
    private val repo: ProjectRepository,
) {
    suspend operator fun invoke(): Long = repo.getOrCreateFavorites()
}
