package pl.flowerbedapp.core.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Project

interface ProjectRepository {
    fun observeProjects(): Flow<List<Project>>
    suspend fun getProject(id: Long): Project?
    suspend fun saveProject(project: Project): Long
    suspend fun deleteProject(id: Long)
    suspend fun addPlantToProject(projectId: Long, plant: Plant)
    suspend fun removePlantFromProject(projectId: Long, plantId: Int)
}