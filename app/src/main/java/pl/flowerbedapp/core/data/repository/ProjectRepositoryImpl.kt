package pl.flowerbedapp.core.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.flowerbedapp.core.data.local.dao.ProjectDao
import pl.flowerbedapp.core.data.local.entity.ProjectEntity
import pl.flowerbedapp.core.data.mapper.toDomain
import pl.flowerbedapp.core.data.mapper.toEntity
import pl.flowerbedapp.core.data.mapper.toProjectPlantEntity
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Project
import pl.flowerbedapp.core.domain.repository.ProjectRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepositoryImpl @Inject constructor(
    private val dao: ProjectDao,
) : ProjectRepository {

    override fun observeProjects(): Flow<List<Project>> =
        dao.observeProjectsWithPlants().map { list -> list.map { it.toDomain() } }

    override suspend fun getProject(id: Long): Project? =
        dao.getProjectWithPlants(id)?.toDomain()

    override suspend fun saveProject(project: Project): Long =
        dao.insertProject(project.toEntity())

    override suspend fun deleteProject(id: Long) = dao.deleteProject(id)

    override suspend fun addPlantToProject(projectId: Long, plant: Plant) =
        dao.insertPlant(plant.toProjectPlantEntity(projectId))

    override suspend fun removePlantFromProject(projectId: Long, plantId: String) =
        dao.deletePlant(projectId, plantId)

    override suspend fun getOrCreateFavorites(): Long =
        dao.getFavoritesProject()?.id ?: dao.insertProject(
            ProjectEntity(
                name        = FAVORITES_NAME,
                description = "",
                createdAt   = System.currentTimeMillis(),
                isFavorites = true,
            )
        )

    override fun observeFavoritePlantIds(): Flow<Set<String>> =
        dao.observeFavoritePlantNames().map { it.toSet() }

    override suspend fun toggleFavorite(plant: Plant) {
        val favoritesId = getOrCreateFavorites()
        if (dao.containsPlant(favoritesId, plant.id)) dao.deletePlant(favoritesId, plant.id)
        else addPlantToProject(favoritesId, plant)
    }

    private companion object {
        const val FAVORITES_NAME = "❤ Favorites"
    }
}
