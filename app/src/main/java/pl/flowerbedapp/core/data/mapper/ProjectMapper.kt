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
)

fun Plant.toProjectPlantEntity(projectId: Long, json: String) = ProjectPlantEntity(
    projectId = projectId,
    plantId   = id,
    plantJson = json,
)

fun ProjectWithPlants.toDomain(deserialize: (String) -> Plant?): Project = Project(
    id          = project.id,
    name        = project.name,
    description = project.description,
    createdAt   = project.createdAt,
    plants      = plants.mapNotNull { deserialize(it.plantJson) },
)