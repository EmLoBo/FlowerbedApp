package pl.flowerbedapp.core.data.repository

import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
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
    moshi: Moshi,
) : ProjectRepository {

    @OptIn(ExperimentalStdlibApi::class)
    private val plantAdapter = moshi.adapter<Plant>()

    private fun deserialize(json: String): Plant? =
        runCatching { plantAdapter.fromJson(json) }.getOrNull()

    override fun observeProjects(): Flow<List<Project>> =
        dao.observeProjectsWithPlants().map { list ->
            list.map { it.toDomain(::deserialize) }
        }

    override suspend fun getProject(id: Long): Project? =
        dao.getProjectWithPlants(id)?.toDomain(::deserialize)

    override suspend fun saveProject(project: Project): Long =
        dao.insertProject(project.toEntity())

    override suspend fun deleteProject(id: Long) = dao.deleteProject(id)

    override suspend fun addPlantToProject(projectId: Long, plant: Plant) {
        val json = plantAdapter.toJson(plant)
        dao.insertPlant(plant.toProjectPlantEntity(projectId, json))
    }

    override suspend fun removePlantFromProject(projectId: Long, plantId: Int) =
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

    override fun observeFavoritePlantIds(): Flow<Set<Int>> =
        dao.observeFavoritePlantIds().map { it.toSet() }

    override suspend fun toggleFavorite(plant: Plant) {
        val favoritesId = getOrCreateFavorites()
        val alreadySaved = dao.getProjectWithPlants(favoritesId)
            ?.plants.orEmpty()
            .any { it.plantId == plant.id }

        if (alreadySaved) dao.deletePlant(favoritesId, plant.id)
        else addPlantToProject(favoritesId, plant)
    }

    private companion object {
        const val FAVORITES_NAME = "❤ Favorites"
    }
}
