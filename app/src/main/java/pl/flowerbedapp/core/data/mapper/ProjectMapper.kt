package pl.flowerbedapp.core.data.mapper

import pl.flowerbedapp.core.data.local.entity.ProjectEntity
import pl.flowerbedapp.core.data.local.entity.ProjectPlantEntity
import pl.flowerbedapp.core.data.local.relation.ProjectWithPlants
import pl.flowerbedapp.core.domain.model.Plant
import pl.flowerbedapp.core.domain.model.Project

fun Project.toEntity() = ProjectEntity(
    id          = id,
    name        = name,
    description = description,
    createdAt   = createdAt,
    isFavorites = isFavorites,
)

fun Plant.toProjectPlantEntity(projectId: Long) = ProjectPlantEntity(
    projectId = projectId,
    plantName = id,
)

fun ProjectWithPlants.toDomain(): Project = Project(
    id          = project.id,
    name        = project.name,
    description = project.description,
    createdAt   = project.createdAt,
    // A relation has no ORDER BY — sort here, by the same key the plant lists use
    plants      = plants.sortedBy { it.searchKey }.map { it.toDomain() },
    isFavorites = project.isFavorites,
)
